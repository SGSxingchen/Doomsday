package org.lanstard.doomsday.client.manage;

import net.minecraft.client.Minecraft;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.lanstard.doomsday.client.gui.text.ScreenTextManager;
import org.lanstard.doomsday.client.gui.text.ScreenTextPreset;

import java.util.HashMap;
import java.util.Map;

@OnlyIn(Dist.CLIENT)
public class DuoXinPoStatusManager {
    private static final Map<String, Integer> targetPositions = new HashMap<>();
    private static int nextPosition = 0;
    private static final int LINE_HEIGHT = 15; // 增加行高，使文本更清晰
    private static final int START_Y = 20; // 向下移动一点，避免与其他UI重叠
    private static final int START_X = 20; // 向右移动一点，避免文本被切断
    
    public static void showControllerStatus(String targetName, long remainingTime) {
        String text = String.format("§b[夺心魄] §f正在控制: %s §7(%ds)", targetName, remainingTime);
        String id = "duoxinpo_controller_" + targetName;
        int yPos = getTargetPosition(targetName);
        ScreenTextManager.addFixedText(id, text, START_X, yPos, ScreenTextPreset.ECHO_STATUS);
    }
    
    public static void showControlledStatus(String controllerName, long remainingTime) {
        String text = String.format("§c[夺心魄] §f你正被 %s §f控制 §7(%ds)", controllerName, remainingTime);
        ScreenTextManager.addFixedText("duoxinpo_controlled", text, START_X, START_Y, ScreenTextPreset.ECHO_STATUS);
    }
    
    private static int getTargetPosition(String targetName) {
        return targetPositions.computeIfAbsent(targetName, k -> {
            int pos = nextPosition;
            nextPosition++;
            return START_Y + pos * LINE_HEIGHT;
        });
    }
    
    public static void clearStatus() {
        targetPositions.clear();
        nextPosition = 0;
        ScreenTextManager.clearAll();
    }
} 