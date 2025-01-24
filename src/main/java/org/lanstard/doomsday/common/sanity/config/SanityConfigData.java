package org.lanstard.doomsday.common.sanity.config;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffect;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.List;

public class SanityConfigData {
    public SanityLimits sanity_limits = new SanityLimits();
    public NaturalChange natural_change = new NaturalChange();
    public List<ThresholdEffect> thresholds = List.of();

    public static class SanityLimits {
        public int max = 1000;
        public int min = 0;
        public int critical_high = 800;
        public int critical_low = 200;
    }

    public static class NaturalChange {
        public int regen_rate = 1;
        public int drain_rate = 1;
        public int regen_interval = 100;
        public int drain_interval = 100;
    }

    public static class ThresholdEffect {
        public Range range;
        public List<EffectEntry> effects;
        public int health_modifier;
        
        public static class Range {
            public int min;
            public int max;
        }
    }

    public static class EffectEntry {
        public String type;
        public int duration;
        public int amplifier;

        public MobEffect getMobEffect() {
            return ForgeRegistries.MOB_EFFECTS.getValue(new ResourceLocation(type));
        }
    }
} 