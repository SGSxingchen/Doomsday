package org.lanstard.doomsday.utils;

import net.minecraft.world.phys.Vec3;

import java.util.List;

public class World3DTextData {
    private final String text;
    private double x, y, z;  // 世界坐标
    private final int color;
    private float scale;
    private float alpha;
    private final boolean glowing;
    private final long startTime;
    private final long duration;
    private final float fadeInTime;
    private final float fadeOutTime;
    private final List<Vec3> pathPoints; // 移动路径点
    private int currentPathIndex;
    private final boolean facingPlayer;
    private float rotationX, rotationY, rotationZ;  // 欧拉角旋转
    public World3DTextData(String text, double x, double y, double z, 
                          int color, float scale, float alpha, boolean glowing,
                          long duration, float fadeInTime, float fadeOutTime,
                          List<Vec3> pathPoints, boolean facingPlayer,
                          float rotationX, float rotationY, float rotationZ) {
        this.text = text;
        this.x = x;
        this.y = y;
        this.z = z;
        this.color = color;
        this.scale = scale;
        this.alpha = alpha;
        this.glowing = glowing;
        this.startTime = System.currentTimeMillis();
        this.duration = duration;
        this.fadeInTime = fadeInTime;
        this.fadeOutTime = fadeOutTime;
        this.pathPoints = pathPoints;
        this.currentPathIndex = 0;
        this.facingPlayer = facingPlayer;
        this.rotationX = rotationX;
        this.rotationY = rotationY;
        this.rotationZ = rotationZ;
    }
    
    // Getter 方法
    public String getText() {
        return text;
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    public double getZ() {
        return z;
    }

    public int getColor() {
        return color;
    }

    public float getScale() {
        return scale;
    }

    public float getAlpha() {
        return alpha;
    }

    public boolean isGlowing() {
        return glowing;
    }

    public long getStartTime() {
        return startTime;
    }

    public long getDuration() {
        return duration;
    }

    public float getFadeInTime() {
        return fadeInTime;
    }

    public float getFadeOutTime() {
        return fadeOutTime;
    }

    public List<Vec3> getPathPoints() {
        return pathPoints;
    }

    public int getCurrentPathIndex() {
        return currentPathIndex;
    }

    public boolean isFacingPlayer() {
        return facingPlayer;
    }

    public float getRotationX() { return rotationX; }
    public float getRotationY() { return rotationY; }
    public float getRotationZ() { return rotationZ; }

    // 可选：添加一些setter方法（如果需要的话）
    public void setScale(float scale) {
        this.scale = scale;
    }

    public void setAlpha(float alpha) {
        this.alpha = alpha;
    }

    public void setPosition(double x, double y, double z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    @Override
    public String toString() {
        return String.format("World3DTextData{text='%s', pos=(%f,%f,%f), progress=%f}", 
            text, x, y, z, getLifeProgress());
    }
    
    public void update() {
        if (pathPoints != null && !pathPoints.isEmpty()) {
            updatePosition();
        }
    }
    
    private void updatePosition() {
        if (pathPoints == null || pathPoints.size() < 2) return;
        
        float progress = getLifeProgress();
        
        int totalSegments = pathPoints.size() - 1;
        float segmentProgress = progress * totalSegments;
        int currentIndex = (int) Math.floor(segmentProgress);
        
        // 确保不超出数组范围
        if (currentIndex >= pathPoints.size() - 1) {
            Vec3 lastPoint = pathPoints.get(pathPoints.size() - 1);
            x = lastPoint.x;
            y = lastPoint.y;
            z = lastPoint.z;
            return;
        }
        
        // 计算当前段内的进度
        float localProgress = segmentProgress - currentIndex;
        
        // 获取当前段的起点和终点
        Vec3 current = pathPoints.get(currentIndex);
        Vec3 next = pathPoints.get(currentIndex + 1);
        
        // 使用平滑插值
        float smoothProgress = smoothStep(localProgress);
        
        // 更新位置
        x = lerp(current.x, next.x, smoothProgress);
        y = lerp(current.y, next.y, smoothProgress);
        z = lerp(current.z, next.z, smoothProgress);
    }
    
    private float smoothStep(float x) {
        return x * x * (3 - 2 * x);
    }
    
    private double lerp(double a, double b, float t) {
        return a + (b - a) * t;
    }
    
    public float getCurrentAlpha() {
        float progress = getLifeProgress();
        float currentAlpha = this.alpha;  // 基础透明度

        // 确保淡入和淡出时间不会重叠
        float effectiveInTime = Math.min(fadeInTime, 0.5f);
        float effectiveOutTime = Math.min(fadeOutTime, 0.5f);
        
        // 淡入效果
        if (progress < effectiveInTime && effectiveInTime > 0) {
            float fadeInProgress = progress / effectiveInTime;
            currentAlpha *= fadeInProgress;
        }
        
        // 淡出效果
        float fadeOutStart = 1.0f - effectiveOutTime;
        if (progress > fadeOutStart && effectiveOutTime > 0) {
            float fadeOutProgress = (progress - fadeOutStart) / effectiveOutTime;
            currentAlpha *= (1.0f - fadeOutProgress);
        }

        // 确保透明度在有效范围内
        currentAlpha = Math.max(0.0f, Math.min(1.0f, currentAlpha));

        return currentAlpha;
    }
    
    public float getLifeProgress() {
        long currentTime = System.currentTimeMillis();
        float progress = (float)(currentTime - startTime) / duration;
        return Math.min(1.0f, progress);
    }
    
    public boolean isExpired() {
        return System.currentTimeMillis() - startTime > duration;
    }
} 