package org.lanstard.doomsday.utils;

public class ScreenTextData {
    private final String text;
    private float x, y;  // 屏幕坐标
    private final int color;
    private float scale;
    private float alpha;
    private final boolean glowing;
    private final long startTime;
    private final long duration;
    private final float fadeInTime;
    private final float fadeOutTime;
    private final float scaleStart;
    private final float scaleEnd;
    private final float moveSpeed;
    private final float rotationSpeed;
    private float rotation;
    private float dx, dy;  // 添加方向属性
    private final String id;  // 新增：文本ID
    
    public ScreenTextData(String text, float x, float y, int color, float scale, 
                         float alpha, boolean glowing, long duration, 
                         float fadeInTime, float fadeOutTime, 
                         float scaleStart, float scaleEnd,
                         float moveSpeed, float rotationSpeed, float dx, float dy) {
        this(text, x, y, color, scale, alpha, glowing, duration, fadeInTime, fadeOutTime,
             scaleStart, scaleEnd, moveSpeed, rotationSpeed, dx, dy, null);
    }
    
    public ScreenTextData(String text, float x, float y, int color, float scale, 
                         float alpha, boolean glowing, long duration, 
                         float fadeInTime, float fadeOutTime, 
                         float scaleStart, float scaleEnd,
                         float moveSpeed, float rotationSpeed, float dx, float dy,
                         String id) {
        this.text = text;
        this.x = x;
        this.y = y;
        this.color = color;
        this.scale = scale;
        this.alpha = alpha;
        this.glowing = glowing;
        this.startTime = System.currentTimeMillis();
        this.duration = duration;
        this.fadeInTime = fadeInTime;
        this.fadeOutTime = fadeOutTime;
        this.scaleStart = scaleStart;
        this.scaleEnd = scaleEnd;
        this.moveSpeed = moveSpeed;
        this.rotationSpeed = rotationSpeed;
        this.rotation = 0f;
        this.dx = dx;
        this.dy = dy;
        this.id = id;
    }
    
    // Getter 方法
    public String getText() {
        return text;
    }
    
    public float getX() {
        return x;
    }
    
    public float getY() {
        return y;
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
    
    public float getScaleStart() {
        return scaleStart;
    }
    
    public float getScaleEnd() {
        return scaleEnd;
    }
    
    public float getMoveSpeed() {
        return moveSpeed;
    }
    
    public float getRotationSpeed() {
        return rotationSpeed;
    }
    
    public float getRotation() {
        return rotation;
    }
    
    // 新增：获取ID
    public String getId() {
        return id;
    }
    
    // 功能方法
    public boolean isExpired() {
        return System.currentTimeMillis() - startTime > duration;
    }
    
    public float getCurrentAlpha() {
        long currentTime = System.currentTimeMillis() - startTime;
        
        // 确保不会超出持续时间
        if (currentTime >= duration) {
            return 0f;
        }
        
        // 淡入
        if (currentTime < fadeInTime) {
            return Math.min((currentTime / fadeInTime) * alpha, alpha);
        }
        // 淡出
        else if (currentTime > duration - fadeOutTime) {
            return Math.max(((duration - currentTime) / fadeOutTime) * alpha, 0f);
        }
        return alpha;
    }
    
    public float getCurrentScale() {
        long currentTime = System.currentTimeMillis() - startTime;
        float progress = currentTime / (float) duration;
        return scaleStart + (scaleEnd - scaleStart) * progress;
    }
    
    public void update() {
        long currentTime = System.currentTimeMillis();
        float deltaTime = (currentTime - startTime) / 1000.0f;
        
        // 根据方向更新位置
        x += dx * moveSpeed;
        y += dy * moveSpeed;
        
        // 更新旋转
        rotation += rotationSpeed * deltaTime;
        
        // 确保rotation在0-2π范围内
        if (rotation > Math.PI * 2) {
            rotation -= Math.PI * 2;
        } else if (rotation < 0) {
            rotation += Math.PI * 2;
        }
    }
    
    // 获取生命周期进度（0.0 - 1.0）
    public float getLifeProgress() {
        return (System.currentTimeMillis() - startTime) / (float) duration;
    }
    
    // 获取剩余时间（毫秒）
    public long getRemainingTime() {
        return Math.max(0, duration - (System.currentTimeMillis() - startTime));
    }
    
    // 设置新位置
    public void setPosition(float x, float y) {
        this.x = x;
        this.y = y;
    }
    
    // 检查文本是否在淡入阶段
    public boolean isFadingIn() {
        return System.currentTimeMillis() - startTime < fadeInTime;
    }
    
    // 检查文本是否在淡出阶段
    public boolean isFadingOut() {
        return System.currentTimeMillis() - startTime > duration - fadeOutTime;
    }
    
    @Override
    public String toString() {
        return String.format("ScreenTextData{text='%s', pos=(%f,%f), progress=%f}", 
            text, x, y, getLifeProgress());
    }
}