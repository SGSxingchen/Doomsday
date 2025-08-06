package org.lanstard.doomsday.config;

import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.config.ModConfig;

public class DoomsdayConfig {
    public static final ForgeConfigSpec.Builder BUILDER = new ForgeConfigSpec.Builder();
    public static final ForgeConfigSpec SPEC;

    public static final ForgeConfigSpec.IntValue ECHO_ITEM_HEALTH_REDUCTION;
    public static final ForgeConfigSpec.IntValue ECHO_ITEM_SANITY_REDUCTION;
    
    // 本地聊天配置
    public static final ForgeConfigSpec.BooleanValue ENABLE_LOCAL_CHAT;
    public static final ForgeConfigSpec.DoubleValue LOCAL_CHAT_RANGE;
    
    // 眼球腐烂配置
    public static final ForgeConfigSpec.IntValue EYE_DECAY_TIME_HOURS;

    static {
        BUILDER.comment("十日终焉 - 通用配置");
        
        // 回响装配配置
        BUILDER.push("回响装配");
        ECHO_ITEM_HEALTH_REDUCTION = BUILDER
            .comment("装配眼球时减少的最大生命值")
            .defineInRange("healthReduction", 6, 0, 40);
        
        ECHO_ITEM_SANITY_REDUCTION = BUILDER
            .comment("装配眼球时减少的最大理智值")
            .defineInRange("sanityReduction", 200, 0, 1000);
        BUILDER.pop();

        // 本地聊天配置
        BUILDER.push("本地聊天");
        ENABLE_LOCAL_CHAT = BUILDER
            .comment("是否启用本地聊天功能（启用后普通玩家只能看到指定范围内的聊天消息，管理员不受影响）")
            .define("enable", true);
            
        LOCAL_CHAT_RANGE = BUILDER
            .comment("本地聊天的可见范围（方块）")
            .defineInRange("range", 32.0, 1.0, 256.0);
        BUILDER.pop();

        // 眼球腐烂配置
        BUILDER.push("眼球腐烂");
        EYE_DECAY_TIME_HOURS = BUILDER
            .comment("眼球腐烂时间（小时）")
            .defineInRange("decayTimeHours", 3, 1, 24);
        BUILDER.pop();

        SPEC = BUILDER.build();
    }

    public static void register() {
        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, SPEC, "doomsday.toml");
    }
} 