package org.lanstard.doomsday.client.event;

import net.minecraft.client.Minecraft;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.ClientPlayerNetworkEvent;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.common.Mod;
import org.lanstard.doomsday.Doomsday;
import org.lanstard.doomsday.network.ModMessages;
import org.lanstard.doomsday.network.packet.ClientInfoPacket;

import java.io.File;
import java.io.InputStream;
import java.nio.file.*;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;
import java.util.stream.Collectors;

@Mod.EventBusSubscriber(modid = Doomsday.MODID, value = Dist.CLIENT)
public class ClientEventHandler {
    // 创建线程池用于异步计算MD5
    private static final ExecutorService executor = Executors.newFixedThreadPool(3);

    private static CompletableFuture<String> getFileInfoAsync(Path path) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                if (Files.isDirectory(path)) {
                    long size = Files.walk(path)
                        .filter(Files::isRegularFile)
                        .mapToLong(p -> {
                            try {
                                return Files.size(p);
                            } catch (Exception e) {
                                return 0L;
                            }
                        })
                        .sum();
                    return String.format("%s (大小: %s, 目录)", 
                        path.getFileName().toString(), formatFileSize(size));
                } else {
                    long size = Files.size(path);
                    String sizeStr = formatFileSize(size);
                    String md5 = calculateMD5(path);
                    return String.format("%s (大小: %s, MD5: %s)", 
                        path.getFileName().toString(), sizeStr, md5);
                }
            } catch (Exception e) {
                Doomsday.LOGGER.error("计算文件信息时发生错误: " + path, e);
                return path.getFileName().toString() + " (信息获取失败)";
            }
        }, executor);
    }

    private static String formatFileSize(long size) {
        if (size < 1024) {
            return size + " B";
        } else if (size < 1024 * 1024) {
            return String.format("%.2f KB", size / 1024.0);
        } else if (size < 1024 * 1024 * 1024) {
            return String.format("%.2f MB", size / (1024.0 * 1024.0));
        } else {
            return String.format("%.2f GB", size / (1024.0 * 1024.0 * 1024.0));
        }
    }

    private static String calculateMD5(Path path) throws Exception {
        if (Files.isDirectory(path)) {
            return "目录";
        }
        
        MessageDigest md = MessageDigest.getInstance("MD5");
        try (InputStream is = Files.newInputStream(path)) {
            byte[] buffer = new byte[8192];
            int read;
            while ((read = is.read(buffer)) != -1) {
                md.update(buffer, 0, read);
            }
        }
        
        byte[] digest = md.digest();
        StringBuilder sb = new StringBuilder();
        for (byte b : digest) {
            sb.append(String.format("%02x", b));
        }
        return sb.toString();
    }

    // @SubscribeEvent
    // @OnlyIn(Dist.CLIENT)
    public static void onClientJoin(ClientPlayerNetworkEvent.LoggingIn event) {
        Minecraft mc = Minecraft.getInstance();
        
        // 收集已安装的模组信息（从ModList获取）
        List<String> mods = ModList.get().getMods().stream()
            .map(mod -> mod.getModId() + ":" + mod.getVersion())
            .collect(Collectors.toList());

        // 扫描mods目录获取详细信息
        List<CompletableFuture<String>> modFilesFutures = new ArrayList<>();
        try {
            File gameDir = mc.gameDirectory;
            Path modsDir = gameDir.toPath().resolve("mods");
            
            if (Files.exists(modsDir) && Files.isDirectory(modsDir)) {
                Files.walk(modsDir)
                    .filter(path -> {
                        String fileName = path.getFileName().toString().toLowerCase();
                        return fileName.endsWith(".jar") || 
                               fileName.endsWith(".zip") ||
                               (Files.isDirectory(path) && !fileName.startsWith("."));
                    })
                    .forEach(path -> modFilesFutures.add(getFileInfoAsync(path)));
            }
        } catch (Exception e) {
            Doomsday.LOGGER.error("扫描mods目录时发生错误", e);
        }

        // 收集材质包信息
        List<CompletableFuture<String>> resourcePacksFutures = new ArrayList<>();
        try {
            File gameDir = mc.gameDirectory;
            Path resourcepacksDir = gameDir.toPath().resolve("resourcepacks");
            
            if (Files.exists(resourcepacksDir) && Files.isDirectory(resourcepacksDir)) {
                Path optionsFile = gameDir.toPath().resolve("options.txt");
                List<String> enabledPacks = new ArrayList<>();
                
                if (Files.exists(optionsFile)) {
                    List<String> lines = Files.readAllLines(optionsFile);
                    for (String line : lines) {
                        if (line.startsWith("resourcePacks:")) {
                            String packsJson = line.substring("resourcePacks:".length()).trim();
                            if (!packsJson.isEmpty() && packsJson.startsWith("[") && packsJson.endsWith("]")) {
                                String[] packs = packsJson.substring(1, packsJson.length() - 1)
                                    .split(",");
                                for (String pack : packs) {
                                    pack = pack.trim();
                                    if (pack.startsWith("\"") && pack.endsWith("\"")) {
                                        pack = pack.substring(1, pack.length() - 1);
                                    }
                                    if (!pack.isEmpty()) {
                                        enabledPacks.add(pack);
                                    }
                                }
                            }
                            break;
                        }
                    }
                }
                
                Files.walk(resourcepacksDir)
                    .filter(path -> {
                        String fileName = path.getFileName().toString().toLowerCase();
                        return fileName.endsWith(".zip") || 
                               (Files.isDirectory(path) && !fileName.startsWith(".")) ||
                               fileName.endsWith(".jar");
                    })
                    .forEach(path -> {
                        CompletableFuture<String> future = getFileInfoAsync(path)
                            .thenApply(info -> {
                                if (enabledPacks.contains(path.getFileName().toString())) {
                                    return info + " [启用]";
                                }
                                return info;
                            });
                        resourcePacksFutures.add(future);
                    });
            }
        } catch (Exception e) {
            Doomsday.LOGGER.error("获取材质包信息时发生错误", e);
        }

        // 获取光影信息
        List<CompletableFuture<String>> shaderPacksFutures = new ArrayList<>();
        try {
            File gameDir = mc.gameDirectory;
            Path shaderpacksDir = gameDir.toPath().resolve("shaderpacks");
            
            if (Files.exists(shaderpacksDir) && Files.isDirectory(shaderpacksDir)) {
                Files.list(shaderpacksDir)
                    .filter(path -> {
                        String fileName = path.getFileName().toString().toLowerCase();
                        return fileName.endsWith(".zip") || 
                               fileName.endsWith(".jar") || 
                               (Files.isDirectory(path) && !fileName.startsWith("."));
                    })
                    .forEach(path -> shaderPacksFutures.add(getFileInfoAsync(path)));
            }
        } catch (Exception e) {
            Doomsday.LOGGER.error("获取光影信息时发生错误", e);
        }

        // 等待所有异步操作完成
        try {
            // 等待mod文件信息
            CompletableFuture.allOf(modFilesFutures.toArray(new CompletableFuture[0])).join();
            List<String> modFiles = modFilesFutures.stream()
                .map(CompletableFuture::join)
                .collect(Collectors.toList());

            // 添加 mods 目录扫描信息
            mods.addAll(modFiles);

            // 等待材质包信息
            CompletableFuture.allOf(resourcePacksFutures.toArray(new CompletableFuture[0])).join();
            List<String> resourcePacks = resourcePacksFutures.stream()
                .map(CompletableFuture::join)
                .collect(Collectors.toList());

            // 等待光影包信息
            CompletableFuture.allOf(shaderPacksFutures.toArray(new CompletableFuture[0])).join();
            List<String> shaderPacks = shaderPacksFutures.stream()
                .map(CompletableFuture::join)
                .collect(Collectors.toList());

            // 发送信息到服务端
            ModMessages.sendToServer(new ClientInfoPacket(mods, resourcePacks, shaderPacks));

        } catch (Exception e) {
            Doomsday.LOGGER.error("等待异步操作完成时发生错误", e);
            // 如果出错，尝试发送已经收集到的信息
            ModMessages.sendToServer(new ClientInfoPacket(mods, new ArrayList<>(), new ArrayList<>()));
        }
    }

    // 在类被卸载时关闭线程池
    static {
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            executor.shutdown();
            try {
                if (!executor.awaitTermination(3, TimeUnit.SECONDS)) {
                    executor.shutdownNow();
                }
            } catch (InterruptedException e) {
                executor.shutdownNow();
            }
        }));
    }
} 