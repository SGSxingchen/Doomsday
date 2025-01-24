package org.lanstard.doomsday.common.echo;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.List;
import java.util.stream.Collectors;

@OnlyIn(Dist.CLIENT)
public class ClientEchoManager {
    private static PlayerEchoData clientEchoData;

    public static void handleEchoUpdate(PlayerEchoData data) {
        // 检查回响列表是否发生变化
        boolean hasChanged = false;
        if (clientEchoData == null || 
            clientEchoData.getActiveEchoes().size() != data.getActiveEchoes().size()) {
            hasChanged = true;
        }
        
        clientEchoData = data;
        
        // 如果回响列表发生变化，重置选择器
        if (hasChanged) {
            ClientEchoSelector.reset();
        }
    }

    public static PlayerEchoData getEchoData() {
        return clientEchoData != null ? clientEchoData : new PlayerEchoData();
    }

    @OnlyIn(Dist.CLIENT)
    public static boolean hasEcho() {
        PlayerEchoData data = getEchoData();
        return !data.getActiveEchoes().isEmpty();
    }

    @OnlyIn(Dist.CLIENT)
    public static boolean hasEchoType(EchoType type) {
        PlayerEchoData data = getEchoData();
        return data.getActiveEchoes().stream()
            .anyMatch(echo -> echo.getType() == type);
    }

    @OnlyIn(Dist.CLIENT)
    public static boolean hasSpecificEcho(String echoId) {
        PlayerEchoData data = getEchoData();
        return data.getActiveEchoes().stream()
            .anyMatch(echo -> echo.getId().equals(echoId));
    }

    @OnlyIn(Dist.CLIENT)
    public static List<Echo> getEchoes() {
        return getEchoData().getActiveEchoes();
    }

    @OnlyIn(Dist.CLIENT)
    public static List<Echo> getEchoesOfType(EchoType type) {
        return getEchoData().getActiveEchoes().stream()
            .filter(echo -> echo.getType() == type)
            .collect(Collectors.toList());
    }
} 