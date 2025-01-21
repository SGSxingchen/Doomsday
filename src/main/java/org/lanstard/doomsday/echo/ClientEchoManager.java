package org.lanstard.doomsday.echo;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.List;
import java.util.stream.Collectors;

@OnlyIn(Dist.CLIENT)
public class ClientEchoManager {
    private static PlayerEchoData clientEchoData;

    public static void handleEchoUpdate(PlayerEchoData data) {
        clientEchoData = data;
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