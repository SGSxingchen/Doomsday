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
    public static final ForgeConfigSpec.DoubleValue WUGU_RANGE_HEAL_RATIO;
    public static final ForgeConfigSpec.DoubleValue WUGU_HIGH_FAITH_MULTIPLIER;
    public static final ForgeConfigSpec.DoubleValue WUGU_TARGET_BOX_INFLATE;

    // 爆燃回响相关配置
    public static final ForgeConfigSpec.IntValue BAORAN_SANITY_COST;
    public static final ForgeConfigSpec.IntValue BAORAN_MID_FAITH;
    public static final ForgeConfigSpec.IntValue BAORAN_MIN_FAITH;
    public static final ForgeConfigSpec.IntValue BAORAN_FREE_COST_THRESHOLD;
    public static final ForgeConfigSpec.IntValue BAORAN_COOLDOWN_TICKS;
    public static final ForgeConfigSpec.IntValue BAORAN_BASE_CONVERT_COUNT;
    public static final ForgeConfigSpec.IntValue BAORAN_MID_FAITH_CONVERT_COUNT;
    public static final ForgeConfigSpec.IntValue BAORAN_HIGH_FAITH_CONVERT_COUNT;
    // 爆燃回响强化等级配置
    public static final ForgeConfigSpec.IntValue BAORAN_BASE_ENHANCE_LEVEL;
    public static final ForgeConfigSpec.IntValue BAORAN_MID_ENHANCE_LEVEL;
    public static final ForgeConfigSpec.IntValue BAORAN_HIGH_ENHANCE_LEVEL;

    // 爆燃弹相关配置
    public static final ForgeConfigSpec.DoubleValue FIRE_BOMB_BASE_MAX_DAMAGE;
    public static final ForgeConfigSpec.DoubleValue FIRE_BOMB_BASE_MIN_DAMAGE;
    public static final ForgeConfigSpec.DoubleValue FIRE_BOMB_MID_MAX_DAMAGE;
    public static final ForgeConfigSpec.DoubleValue FIRE_BOMB_MID_MIN_DAMAGE;
    public static final ForgeConfigSpec.DoubleValue FIRE_BOMB_HIGH_MAX_DAMAGE;
    public static final ForgeConfigSpec.DoubleValue FIRE_BOMB_HIGH_MIN_DAMAGE;
    public static final ForgeConfigSpec.DoubleValue FIRE_BOMB_BASE_RADIUS;
    public static final ForgeConfigSpec.DoubleValue FIRE_BOMB_MID_RADIUS;
    public static final ForgeConfigSpec.DoubleValue FIRE_BOMB_HIGH_RADIUS;
    public static final ForgeConfigSpec.IntValue FIRE_BOMB_MID_BURN_DURATION;
    public static final ForgeConfigSpec.IntValue FIRE_BOMB_HIGH_BURN_DURATION;
    public static final ForgeConfigSpec.IntValue FIRE_BOMB_WEAKNESS_DURATION;
    public static final ForgeConfigSpec.DoubleValue FIRE_BOMB_BASE_SPEED;
    public static final ForgeConfigSpec.DoubleValue FIRE_BOMB_MID_SPEED;
    public static final ForgeConfigSpec.DoubleValue FIRE_BOMB_HIGH_SPEED;
    public static final ForgeConfigSpec.DoubleValue FIRE_BOMB_BASE_INACCURACY;
    public static final ForgeConfigSpec.DoubleValue FIRE_BOMB_MID_INACCURACY;
    public static final ForgeConfigSpec.DoubleValue FIRE_BOMB_HIGH_INACCURACY;

    static {
        BUILDER.comment("十日终焉 - 回响配置");
        
        // 无垢回响配置
        BUILDER.push("无垢回响");
        
        WUGU_SANITY_COST = BUILDER
            .comment("无垢回响施法消耗的理智值")
            .defineInRange("sanity_cost", 150, 0, 1000);
            
        WUGU_SANITY_HEAL = BUILDER
            .comment("无垢回响恢复目标的理智值（高信念时会被倍率加成）")
            .defineInRange("sanity_heal", 120, 0, 1000);
            
        WUGU_MID_FAITH = BUILDER
            .comment("无垢回响的中等信念要求（达到此信念值时触发加成效果）")
            .defineInRange("mid_faith", 5, 0, 100);
            
        WUGU_COOLDOWN_TICKS = BUILDER
            .comment("无垢回响的冷却时间（tick，20tick = 1秒）")
            .defineInRange("cooldown_ticks", 12000, 0, 72000);
            
        WUGU_BASE_REACH = BUILDER
            .comment("无垢回响的基础施法距离（方块）")
            .defineInRange("base_reach", 32.0, 1.0, 64.0);
            
        WUGU_HIGH_FAITH_RANGE = BUILDER
            .comment("无垢回响高信念时的范围治疗半径（方块）")
            .defineInRange("high_faith_range", 3.0, 1.0, 16.0);
            
        WUGU_RANGE_HEAL_RATIO = BUILDER
            .comment("无垢回响的范围治疗效果比例（相对于主目标的理智恢复量）")
            .defineInRange("range_heal_ratio", 0.5, 0.1, 2.0);
            
        WUGU_HIGH_FAITH_MULTIPLIER = BUILDER
            .comment("无垢回响高信念时的理智恢复倍率")
            .defineInRange("high_faith_multiplier", 2.0, 1.0, 5.0);
            
        WUGU_TARGET_BOX_INFLATE = BUILDER
            .comment("无垢回响目标检测的碰撞箱扩展范围（方块）")
            .defineInRange("target_box_inflate", 1.0, 0.1, 3.0);
            
        BUILDER.pop();

        // 爆燃回响配置
        BUILDER.push("爆燃回响");
        
        BAORAN_SANITY_COST = BUILDER
            .comment("爆燃回响施法消耗的理智值")
            .defineInRange("sanity_cost", 50, 0, 1000);
            
        BAORAN_MID_FAITH = BUILDER
            .comment("爆燃回响的中等信念要求（达到此信念值时消耗减半）")
            .defineInRange("mid_faith", 5, 0, 100);
            
        BAORAN_MIN_FAITH = BUILDER
            .comment("爆燃回响的最小信念要求（达到此信念值时可以免费释放）")
            .defineInRange("min_faith", 10, 0, 100);
            
        BAORAN_FREE_COST_THRESHOLD = BUILDER
            .comment("爆燃回响免费释放的理智值阈值")
            .defineInRange("free_cost_threshold", 300, 0, 1000);
            
        BAORAN_COOLDOWN_TICKS = BUILDER
            .comment("爆燃回响的冷却时间（tick，20tick = 1秒，6000tick = 5分钟）")
            .defineInRange("cooldown_ticks", 6000, 0, 72000);
            
        BAORAN_BASE_CONVERT_COUNT = BUILDER
            .comment("爆燃回响基础转换数量")
            .defineInRange("base_convert_count", 1, 1, 10);
            
        BAORAN_MID_FAITH_CONVERT_COUNT = BUILDER
            .comment("爆燃回响中等信念时的转换数量")
            .defineInRange("mid_faith_convert_count", 2, 1, 10);
            
        BAORAN_HIGH_FAITH_CONVERT_COUNT = BUILDER
            .comment("爆燃回响高等信念时的转换数量")
            .defineInRange("high_faith_convert_count", 3, 1, 10);

        BAORAN_BASE_ENHANCE_LEVEL = BUILDER
            .comment("爆燃回响基础强化等级")
            .defineInRange("base_enhance_level", 0, 0, 10);
            
        BAORAN_MID_ENHANCE_LEVEL = BUILDER
            .comment("爆燃回响中等信念时的强化等级")
            .defineInRange("mid_enhance_level", 1, 0, 10);
            
        BAORAN_HIGH_ENHANCE_LEVEL = BUILDER
            .comment("爆燃回响高等信念时的强化等级")
            .defineInRange("high_enhance_level", 2, 0, 10);
            
        BUILDER.pop();

        // 爆燃弹配置
        BUILDER.push("爆燃弹");
        
        FIRE_BOMB_BASE_MAX_DAMAGE = BUILDER
            .comment("爆燃弹基础最大伤害")
            .defineInRange("base_max_damage", 10.0, 1.0, 100.0);
            
        FIRE_BOMB_BASE_MIN_DAMAGE = BUILDER
            .comment("爆燃弹基础最小伤害")
            .defineInRange("base_min_damage", 4.0, 1.0, 100.0);
            
        FIRE_BOMB_MID_MAX_DAMAGE = BUILDER
            .comment("爆燃弹中等强化最大伤害")
            .defineInRange("mid_max_damage", 15.0, 1.0, 100.0);
            
        FIRE_BOMB_MID_MIN_DAMAGE = BUILDER
            .comment("爆燃弹中等强化最小伤害")
            .defineInRange("mid_min_damage", 6.0, 1.0, 100.0);
            
        FIRE_BOMB_HIGH_MAX_DAMAGE = BUILDER
            .comment("爆燃弹高等强化最大伤害")
            .defineInRange("high_max_damage", 20.0, 1.0, 100.0);
            
        FIRE_BOMB_HIGH_MIN_DAMAGE = BUILDER
            .comment("爆燃弹高等强化最小伤害")
            .defineInRange("high_min_damage", 8.0, 1.0, 100.0);
            
        FIRE_BOMB_BASE_RADIUS = BUILDER
            .comment("爆燃弹基础爆炸半径")
            .defineInRange("base_radius", 4.0, 1.0, 32.0);
            
        FIRE_BOMB_MID_RADIUS = BUILDER
            .comment("爆燃弹中等强化爆炸半径")
            .defineInRange("mid_radius", 5.0, 1.0, 32.0);
            
        FIRE_BOMB_HIGH_RADIUS = BUILDER
            .comment("爆燃弹高等强化爆炸半径")
            .defineInRange("high_radius", 6.0, 1.0, 32.0);
            
        FIRE_BOMB_MID_BURN_DURATION = BUILDER
            .comment("爆燃弹中等强化燃烧时间（秒）")
            .defineInRange("mid_burn_duration", 5, 1, 60);
            
        FIRE_BOMB_HIGH_BURN_DURATION = BUILDER
            .comment("爆燃弹高等强化燃烧时间（秒）")
            .defineInRange("high_burn_duration", 10, 1, 60);
            
        FIRE_BOMB_WEAKNESS_DURATION = BUILDER
            .comment("爆燃弹高等强化虚弱效果持续时间（tick）")
            .defineInRange("weakness_duration", 100, 20, 1200);
            
        FIRE_BOMB_BASE_SPEED = BUILDER
            .comment("爆燃弹基础投掷速度")
            .defineInRange("base_speed", 1.5, 0.1, 10.0);
            
        FIRE_BOMB_MID_SPEED = BUILDER
            .comment("爆燃弹中等强化投掷速度")
            .defineInRange("mid_speed", 1.75, 0.1, 10.0);
            
        FIRE_BOMB_HIGH_SPEED = BUILDER
            .comment("爆燃弹高等强化投掷速度")
            .defineInRange("high_speed", 2.0, 0.1, 10.0);
            
        FIRE_BOMB_BASE_INACCURACY = BUILDER
            .comment("爆燃弹基础不精确度（越大越不精确）")
            .defineInRange("base_inaccuracy", 1.0, 0.0, 5.0);
            
        FIRE_BOMB_MID_INACCURACY = BUILDER
            .comment("爆燃弹中等强化不精确度（越大越不精确）")
            .defineInRange("mid_inaccuracy", 0.75, 0.0, 5.0);
            
        FIRE_BOMB_HIGH_INACCURACY = BUILDER
            .comment("爆燃弹高等强化不精确度（越大越不精确）")
            .defineInRange("high_inaccuracy", 0.5, 0.0, 5.0);
            
        BUILDER.pop();

        SPEC = BUILDER.build();
    }

    public static void register() {
        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, SPEC, "doomsday-echoes.toml");
    }
} 