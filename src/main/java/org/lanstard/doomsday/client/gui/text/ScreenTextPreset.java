package org.lanstard.doomsday.client.gui.text;

public enum ScreenTextPreset {
    DAMAGE(0xFF0000, 1.0f, 0.9f, true, 2000,
          200, 300, 1.0f, 1.5f, 0.2f, 0f, Direction.UP),
    HEAL(0x00FF00, 1.0f, 0.9f, true, 2000,
         200, 300, 1.0f, 1.5f, 0.2f, 0f, Direction.DOWN),
    CRITICAL(0xFFFF00, 1.2f, 1.0f, true, 2500,
            300, 400, 1.5f, 2.0f, 0.3f, 0f, Direction.RANDOM),
    STATUS(0x00FFFF, 0.8f, 0.8f, false, 3000,
           500, 500, 1.0f, 1.0f, 0f, 0f, Direction.NONE),
    ECHO_STATUS(0x00FFFF, 1.0f, 0.8f, false, 1000,
                0, 200, 1.0f, 1.0f, 0f, 0f, Direction.NONE);
    
    // 添加方向枚举
    public enum Direction {
        UP(0, -1),
        DOWN(0, 1),
        LEFT(-1, 0),
        RIGHT(1, 0),
        RANDOM(0, 0),  // 随机方向
        NONE(0, 0);    // 不移动
        
        final float dx, dy;
        
        Direction(float dx, float dy) {
            this.dx = dx;
            this.dy = dy;
        }
    }
    
    private final int color;           // 文本颜色
    private final float scale;         // 基础缩放
    private final float alpha;         // 基础透明度
    private final boolean glowing;     // 是否发光
    private final long duration;       // 持续时间（毫秒）
    private final float fadeInTime;    // 淡入时间（毫秒）
    private final float fadeOutTime;   // 淡出时间（毫秒）
    private final float scaleStart;    // 初始缩放
    private final float scaleEnd;      // 结束缩放
    private final float moveSpeed;     // 移动速度
    private final float rotationSpeed; // 旋转速度
    private final Direction direction;  // 添加方向属性
    
    ScreenTextPreset(int color, float scale, float alpha, boolean glowing,
                     long duration, float fadeInTime, float fadeOutTime,
                     float scaleStart, float scaleEnd,
                     float moveSpeed, float rotationSpeed, Direction direction) {
        this.color = color;
        this.scale = scale;
        this.alpha = alpha;
        this.glowing = glowing;
        this.duration = duration;
        this.fadeInTime = fadeInTime;
        this.fadeOutTime = fadeOutTime;
        this.scaleStart = scaleStart;
        this.scaleEnd = scaleEnd;
        this.moveSpeed = moveSpeed;
        this.rotationSpeed = rotationSpeed;
        this.direction = direction;
    }
    
    // Getter方法
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
    
    public Direction getDirection() {
        return direction;
    }
} 