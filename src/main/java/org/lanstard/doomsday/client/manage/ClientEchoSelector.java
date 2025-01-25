package org.lanstard.doomsday.client;

import org.lanstard.doomsday.common.echo.Echo;

import java.util.List;

public class ClientEchoSelector {
    private static int currentEchoIndex = 0;
    
    public static void nextEcho(List<Echo> echoes) {
        if (echoes.isEmpty()) return;
        currentEchoIndex = (currentEchoIndex + 1) % echoes.size();
    }
    
    public static void previousEcho(List<Echo> echoes) {
        if (echoes.isEmpty()) return;
        currentEchoIndex = (currentEchoIndex - 1 + echoes.size()) % echoes.size();
    }
    
    public static Echo getCurrentEcho(List<Echo> echoes) {
        if (echoes.isEmpty()) {
            currentEchoIndex = 0;
            return null;
        }
        if (currentEchoIndex >= echoes.size()) {
            currentEchoIndex = 0;
        }
        return echoes.get(currentEchoIndex);
    }
    
    public static void reset() {
        currentEchoIndex = 0;
    }
} 