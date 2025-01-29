package org.lanstard.doomsday.config;

import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.config.ModConfig;

public class EchoConfig {
    public static final ForgeConfigSpec.Builder BUILDER = new ForgeConfigSpec.Builder();
    public static final ForgeConfigSpec SPEC;

    // 无垢回响相关配置
    public static final ForgeConfigSpec.IntValue WUGU_SANITY_COST;
    public static final ForgeConfigSpec.IntValue WUGU_SANITY_HEAL;
    public static final ForgeConfigSpec.IntValue WUGU_MID_FAITH;
    public static final ForgeConfigSpec.IntValue WUGU_COOLDOWN_TICKS;
    public static final ForgeConfigSpec.DoubleValue WUGU_BASE_REACH;
    public static final ForgeConfigSpec.DoubleValue WUGU_HIGH_FAITH_RANGE;

    static {
        BUILDER.comment("十日终焉 - 回响配置");
        
        // 无垢回响配置
        BUILDER.push("无垢回响");
        
        WUGU_SANITY_COST = BUILDER
            .comment("无垢回响消耗的理智值")
            .defineInRange("sanity_cost", 150, 0, 1000);
            
        WUGU_SANITY_HEAL = BUILDER
            .comment("无垢回响恢复的理智值")
            .defineInRange("sanity_heal", 120, 0, 1000);
            
        WUGU_MID_FAITH = BUILDER
            .comment("无垢回响的中等信念要求")
            .defineInRange("mid_faith", 5, 0, 100);
            
        WUGU_COOLDOWN_TICKS = BUILDER
            .comment("无垢回响的冷却时间（tick）")
            .defineInRange("cooldown_ticks", 12000, 0, 72000);
            
        WUGU_BASE_REACH = BUILDER
            .comment("无垢回响的基础施法距离")
            .defineInRange("base_reach", 32.0, 1.0, 64.0);
            
        WUGU_HIGH_FAITH_RANGE = BUILDER
            .comment("无垢回响的高信念范围治疗半径")
            .defineInRange("high_faith_range", 3.0, 1.0, 16.0);
            
        BUILDER.pop();

        SPEC = BUILDER.build();
    }

    public static void register() {
        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, SPEC, "doomsday-echoes.toml");
    }
} 