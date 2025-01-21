package org.lanstard.doomsday.client.data;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class ClientDisplayData {
    private static boolean showHealth = true;
    private static boolean isOp = false;
    private static boolean personalShowHealth = true;
    
    public static void updateSettings(boolean health, boolean op) {
        isOp = op;
        if (op) {
            personalShowHealth = health;
        } else {
            showHealth = health;
        }
    }
    
    public static boolean canSeeHealth() {
        return isOp ? personalShowHealth : showHealth;
    }
} 