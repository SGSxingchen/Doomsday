package org.lanstard.doomsday.common.sanity;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class ClientSanityManager {
    private static int clientSanity = 1000;
    private static int clientFaith = 0;
    
    public static void handleSanityUpdate(int sanity, int faith) {
        clientSanity = sanity;
        clientFaith = faith;
    }
    
    public static int getSanity() {
        return clientSanity;
    }
    
    public static int getFaith() {
        return clientFaith;
    }
} 