package org.lanstard.doomsday.client.manage;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class ClientTimeManager {
    private static int clientDays = 0;
    private static long clientWorldTime = 0;
    private static final int TICKS_PER_DAY = 24000;
    
    public static void handleTimeUpdate(int days, long worldTime) {
        clientDays = days;
        clientWorldTime = worldTime;
    }
    
    public static String getTimeString() {
        int currentTicks = (int)(clientWorldTime % TICKS_PER_DAY);

        if(currentTicks + 6000 <= 24000){
            currentTicks += 6000;
        }
        else{
            currentTicks -= 18000;
        }
        int hours = (currentTicks / 1000) % 24;
        int minutes = ((currentTicks % 1000) * 60 / 1000);
        return String.format("第%d天 %02d:%02d", clientDays, hours, minutes);
    }
} 