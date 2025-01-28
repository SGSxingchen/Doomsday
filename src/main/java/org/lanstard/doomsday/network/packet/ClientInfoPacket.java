package org.lanstard.doomsday.network.packet;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkEvent;
import java.util.List;
import java.util.ArrayList;
import java.util.function.Supplier;
import net.minecraft.network.chat.Component;
import org.lanstard.doomsday.Doomsday;
import net.minecraftforge.network.NetworkDirection;

public class ClientInfoPacket {
    private static final int MAX_MOD_COUNT = 1000; // 最大模组数量限制
    private static final int MAX_RESOURCE_PACK_COUNT = 100; // 最大材质包数量限制
    private static final int MAX_SHADER_PACK_COUNT = 50; // 添加光影包数量限制
    private static final int MAX_STRING_LENGTH = 256; // 字符串最大长度限制
    
    private final List<String> mods;
    private final List<String> resourcePacks;
    private final List<String> shaderPacks; // 改为列表

    public ClientInfoPacket(List<String> mods, List<String> resourcePacks, List<String> shaderPacks) {
        this.mods = new ArrayList<>(mods.size());
        this.resourcePacks = new ArrayList<>(resourcePacks.size());
        this.shaderPacks = new ArrayList<>(shaderPacks != null ? shaderPacks.size() : 0);
        
        // 过滤并限制字符串长度
        mods.stream()
            .filter(s -> s != null && !s.isEmpty())
            .map(s -> s.length() > MAX_STRING_LENGTH ? s.substring(0, MAX_STRING_LENGTH) : s)
            .forEach(this.mods::add);
            
        resourcePacks.stream()
            .filter(s -> s != null && !s.isEmpty())
            .map(s -> s.length() > MAX_STRING_LENGTH ? s.substring(0, MAX_STRING_LENGTH) : s)
            .forEach(this.resourcePacks::add);
            
        if (shaderPacks != null) {
            shaderPacks.stream()
                .filter(s -> s != null && !s.isEmpty())
                .map(s -> s.length() > MAX_STRING_LENGTH ? s.substring(0, MAX_STRING_LENGTH) : s)
                .forEach(this.shaderPacks::add);
        }
    }

    public static void encode(ClientInfoPacket msg, FriendlyByteBuf buf) {
        try {
            // 写入模组列表
            int modCount = Math.min(msg.mods.size(), MAX_MOD_COUNT);
            buf.writeInt(modCount);
            msg.mods.stream().limit(modCount).forEach(buf::writeUtf);

            // 写入材质包列表
            int packCount = Math.min(msg.resourcePacks.size(), MAX_RESOURCE_PACK_COUNT);
            buf.writeInt(packCount);
            msg.resourcePacks.stream().limit(packCount).forEach(buf::writeUtf);

            // 写入光影包列表
            int shaderCount = Math.min(msg.shaderPacks.size(), MAX_SHADER_PACK_COUNT);
            buf.writeInt(shaderCount);
            msg.shaderPacks.stream().limit(shaderCount).forEach(buf::writeUtf);
        } catch (Exception e) {
            Doomsday.LOGGER.error("编码客户端信息包时发生错误", e);
            // 写入空数据作为fallback
            buf.writeInt(0);
            buf.writeInt(0);
            buf.writeInt(0);
        }
    }

    public static ClientInfoPacket decode(FriendlyByteBuf buf) {
        try {
            // 读取模组列表
            List<String> mods = new ArrayList<>();
            int modCount = Math.min(buf.readInt(), MAX_MOD_COUNT);
            for (int i = 0; i < modCount && buf.isReadable(); i++) {
                String mod = buf.readUtf(MAX_STRING_LENGTH);
                if (!mod.isEmpty()) {
                    mods.add(mod);
                }
            }

            // 读取材质包列表
            List<String> resourcePacks = new ArrayList<>();
            int packCount = Math.min(buf.readInt(), MAX_RESOURCE_PACK_COUNT);
            for (int i = 0; i < packCount && buf.isReadable(); i++) {
                String pack = buf.readUtf(MAX_STRING_LENGTH);
                if (!pack.isEmpty()) {
                    resourcePacks.add(pack);
                }
            }

            // 读取光影包列表
            List<String> shaderPacks = new ArrayList<>();
            int shaderCount = Math.min(buf.readInt(), MAX_SHADER_PACK_COUNT);
            for (int i = 0; i < shaderCount && buf.isReadable(); i++) {
                String shader = buf.readUtf(MAX_STRING_LENGTH);
                if (!shader.isEmpty()) {
                    shaderPacks.add(shader);
                }
            }

            return new ClientInfoPacket(mods, resourcePacks, shaderPacks);
        } catch (Exception e) {
            Doomsday.LOGGER.error("解码客户端信息包时发生错误", e);
            return new ClientInfoPacket(new ArrayList<>(), new ArrayList<>(), new ArrayList<>());
        }
    }

    public static void handle(ClientInfoPacket msg, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            try {
                // 确保是客户端发往服务端的消息
                if (ctx.get().getDirection() != NetworkDirection.PLAY_TO_SERVER) {
                    Doomsday.LOGGER.warn("收到了错误方向的客户端信息包");
                    return;
                }

                // 获取发送者
                ServerPlayer player = ctx.get().getSender();
                if (player == null) {
                    Doomsday.LOGGER.warn("无法获取客户端信息包的发送者");
                    return;
                }

                // 检查消息大小
                if (msg.mods.size() > MAX_MOD_COUNT || 
                    msg.resourcePacks.size() > MAX_RESOURCE_PACK_COUNT ||
                    msg.shaderPacks.size() > MAX_SHADER_PACK_COUNT) {
                    Doomsday.LOGGER.warn("玩家 {} 发送了过大的客户端信息包", player.getName().getString());
                    return;
                }

                // 输出玩家信息
                Doomsday.LOGGER.info("玩家 {} 的客户端信息:", player.getName().getString());
                
                // 输出模组列表
                Doomsday.LOGGER.info("已安装模组 ({}/{}):", msg.mods.size(), MAX_MOD_COUNT);
                msg.mods.forEach(mod -> Doomsday.LOGGER.info("- {}", mod));
                
                // 输出材质包列表
                Doomsday.LOGGER.info("已启用材质包 ({}/{}):", msg.resourcePacks.size(), MAX_RESOURCE_PACK_COUNT);
                msg.resourcePacks.forEach(pack -> Doomsday.LOGGER.info("- {}", pack));
                
                // 输出光影包列表
                Doomsday.LOGGER.info("光影包 ({}/{}):", msg.shaderPacks.size(), MAX_SHADER_PACK_COUNT);
                msg.shaderPacks.forEach(shader -> Doomsday.LOGGER.info("- {}", shader));

                // 向管理员发送通知
                String notification = String.format("§e玩家 %s 已连接，模组数量: %d/%d, 材质包数量: %d/%d, 光影包数量: %d/%d",
                    player.getName().getString(), 
                    msg.mods.size(), MAX_MOD_COUNT,
                    msg.resourcePacks.size(), MAX_RESOURCE_PACK_COUNT,
                    msg.shaderPacks.size(), MAX_SHADER_PACK_COUNT);
                
                player.getServer().getPlayerList().getPlayers().stream()
                    .filter(p -> p.hasPermissions(2))
                    .forEach(admin -> admin.sendSystemMessage(Component.literal(notification)));
                
            } catch (Exception e) {
                Doomsday.LOGGER.error("处理客户端信息包时发生错误", e);
            }
        });
        ctx.get().setPacketHandled(true);
    }
} 