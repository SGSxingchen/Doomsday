package org.lanstard.doomsday.config;

import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.config.ModConfig;

public class DoomsdayConfig {
    public static final ForgeConfigSpec.Builder BUILDER = new ForgeConfigSpec.Builder();
    public static final ForgeConfigSpec SPEC;

    public static final ForgeConfigSpec.IntValue ECHO_ITEM_HEALTH_REDUCTION;
    public static final ForgeConfigSpec.IntValue ECHO_ITEM_SANITY_REDUCTION;

    static {
        BUILDER.comment("十日终焉 - 通用配置");
        
        BUILDER.push("回响装配");
        ECHO_ITEM_HEALTH_REDUCTION = BUILDER
            .comment("装配眼球时减少的最大生命值")
            .defineInRange("healthReduction", 6, 0, 40);
        
        ECHO_ITEM_SANITY_REDUCTION = BUILDER
            .comment("装配眼球时减少的最大理智值")
            .defineInRange("sanityReduction", 200, 0, 1000);
        BUILDER.pop();

        SPEC = BUILDER.build();
    }

    public static void register() {
        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, SPEC);
    }
} 