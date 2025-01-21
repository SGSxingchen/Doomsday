package org.lanstard.doomsday.client.render;

import net.minecraft.world.phys.Vec3;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiFunction;

public enum World3DTextPreset {
    DAMAGE(0xFF0000, 2.0f, 1.0f, true, 10000,
           500, 500, true, PathPattern.NONE),
    TEST(0xFF0000, 2.0f, 1.0f, false, 10000,
            500, 500, false, PathPattern.NONE),
           
    HEAL(0x00FF00, 1.0f, 1.0f, true, 10000,
         500, 500, true, PathPattern.FLOAT_UP),
         
    CRITICAL(0xFFFF00, 1.2f, 1.0f, true, 1000,
             500, 500, true, PathPattern.SPIRAL),
             
    STATUS(0x00FFFF, 0.8f, 1.0f, false, 10000,
           500, 500, true, PathPattern.ORBIT);

    public enum PathPattern {
        NONE((pos, camera) -> new ArrayList<>()),
        FLOAT_UP((pos, camera) -> {
            double totalDistance = 4.0;
            int segments = 60;
            List<Vec3> points = new ArrayList<>();
            for (int i = 0; i <= segments; i++) {
                double progress = (double) i / segments;
                points.add(pos.add(0, progress * totalDistance, 0));
            }
            return points;
        }),
        SPIRAL((pos, camera) -> {
            int segments = 120;
            double radius = 1.0;
            double heightGain = 3.0;
            List<Vec3> points = new ArrayList<>();

            for (int i = 0; i <= segments; i++) {
                double progress = (double) i / segments;
                double angle = progress * Math.PI * 4;
                double x = pos.x + Math.cos(angle) * radius * progress;
                double y = pos.y + progress * heightGain;
                double z = pos.z + Math.sin(angle) * radius * progress;
                points.add(new Vec3(x, y, z));
            }
            return points;
        }),
        ORBIT((pos, camera) -> {
            int segments = 180;
            double radius = 1.5;
            List<Vec3> points = new ArrayList<>();

            Vec3 toCamera = camera.subtract(pos).normalize();
            Vec3 right = toCamera.cross(new Vec3(0, 1, 0)).normalize();
            Vec3 up = right.cross(toCamera);

            for (int i = 0; i <= segments; i++) {
                double angle = ((double) i / segments) * Math.PI * 2;
                Vec3 offset = right.scale(Math.cos(angle) * radius)
                        .add(up.scale(Math.sin(angle) * radius));
                points.add(pos.add(offset));
            }
            return points;
        });

        private final BiFunction<Vec3, Vec3, List<Vec3>> pathGenerator;

        PathPattern(BiFunction<Vec3, Vec3, List<Vec3>> generator) {
            this.pathGenerator = generator;
        }

        public List<Vec3> generatePath(Vec3 position, Vec3 cameraPos) {
            return pathGenerator.apply(position, cameraPos);
        }
    }
    private final int color;
    private final float scale;
    private final float alpha;
    private final boolean glowing;
    private final long duration;
    private final float fadeInTime;
    private final float fadeOutTime;
    private final boolean facingPlayer;
    private final PathPattern pathPattern;

    World3DTextPreset(int color, float scale, float alpha, boolean glowing,
                      long duration, float fadeInTime, float fadeOutTime,
                      boolean facingPlayer, PathPattern pathPattern) {
        this.color = color;
        this.scale = scale;
        this.alpha = alpha;
        this.glowing = glowing;
        this.duration = duration;
        this.fadeInTime = fadeInTime;
        this.fadeOutTime = fadeOutTime;
        this.facingPlayer = facingPlayer;
        this.pathPattern = pathPattern;
    }

    // Getter方法
    public int getColor() { return color; }
    public float getScale() { return scale; }
    public float getAlpha() { return alpha; }
    public boolean isGlowing() { return glowing; }
    public long getDuration() { return duration; }
    public float getFadeInTime() { return fadeInTime; }
    public float getFadeOutTime() { return fadeOutTime; }
    public boolean isFacingPlayer() { return facingPlayer; }
    public PathPattern getPathPattern() { return pathPattern; }

    public List<Vec3> generatePath(Vec3 position, Vec3 cameraPos) {
        return pathPattern.generatePath(position, cameraPos);
    }

    public boolean isNonePathPattern () {
        return this.pathPattern == PathPattern.NONE;
    }
} 