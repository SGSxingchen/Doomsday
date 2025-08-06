package org.lanstard.doomsday.config;

import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.config.ModConfig;

import java.util.List;

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

    // 硬化回响相关配置
    public static final ForgeConfigSpec.IntValue YINGHUA_SANITY_COST;
    public static final ForgeConfigSpec.IntValue YINGHUA_CONTINUOUS_SANITY_COST;
    public static final ForgeConfigSpec.IntValue YINGHUA_HIGH_FAITH_THRESHOLD;
    public static final ForgeConfigSpec.DoubleValue YINGHUA_REFLECT_DAMAGE;
    public static final ForgeConfigSpec.IntValue YINGHUA_DAMAGE_SANITY_COST;

    // 涡流回响相关配置
    public static final ForgeConfigSpec.IntValue WOLIU_SANITY_COST;
    public static final ForgeConfigSpec.IntValue WOLIU_COOLDOWN_TICKS;
    public static final ForgeConfigSpec.DoubleValue WOLIU_RANGE;
    public static final ForgeConfigSpec.DoubleValue WOLIU_ATTRACTION_STRENGTH;
    public static final ForgeConfigSpec.IntValue WOLIU_FAITH_REQUIREMENT;
    public static final ForgeConfigSpec.DoubleValue WOLIU_FAITH_RANGE_MULTIPLIER;
    public static final ForgeConfigSpec.DoubleValue WOLIU_FAITH_STRENGTH_MULTIPLIER;

    // 纳垢回响相关配置
    public static final ForgeConfigSpec.IntValue NAGOU_SANITY_COST;
    public static final ForgeConfigSpec.IntValue NAGOU_DIRT_COST_NORMAL;
    public static final ForgeConfigSpec.IntValue NAGOU_DIRT_COST_HIGH_FAITH;
    public static final ForgeConfigSpec.IntValue NAGOU_HIGH_FAITH_THRESHOLD;

    // 离析回响相关配置
    public static final ForgeConfigSpec.IntValue LIXI_SANITY_COST;
    public static final ForgeConfigSpec.IntValue LIXI_BASE_COOLDOWN_TICKS;
    public static final ForgeConfigSpec.IntValue LIXI_DAMAGE_COOLDOWN_TICKS;
    public static final ForgeConfigSpec.IntValue LIXI_FREE_COST_THRESHOLD;
    public static final ForgeConfigSpec.IntValue LIXI_MIN_FAITH_REQUIREMENT;
    public static final ForgeConfigSpec.IntValue LIXI_MID_FAITH;
    public static final ForgeConfigSpec.IntValue LIXI_DAMAGE_FAITH;
    public static final ForgeConfigSpec.DoubleValue LIXI_BASE_REACH;
    public static final ForgeConfigSpec.DoubleValue LIXI_MID_REACH;
    public static final ForgeConfigSpec.DoubleValue LIXI_HIGH_REACH;
    public static final ForgeConfigSpec.DoubleValue LIXI_BASE_DAMAGE;
    public static final ForgeConfigSpec.DoubleValue LIXI_DAMAGE_PER_FAITH;
    public static final ForgeConfigSpec.DoubleValue LIXI_DAMAGE_RADIUS;
    public static final ForgeConfigSpec.DoubleValue LIXI_DIRECT_TARGET_DAMAGE_MULTIPLIER;
    public static final ForgeConfigSpec.IntValue LIXI_CHARGING_DURATION_TICKS;
    public static final ForgeConfigSpec.ConfigValue<List<? extends String>> LIXI_UNBREAKABLE_BLOCKS;

    // 心锁回响相关配置
    public static final ForgeConfigSpec.IntValue XINSUO_SANITY_COST;
    public static final ForgeConfigSpec.IntValue XINSUO_COOLDOWN_TICKS;
    public static final ForgeConfigSpec.IntValue XINSUO_EFFECT_DURATION_TICKS;
    public static final ForgeConfigSpec.DoubleValue XINSUO_BASE_REACH;
    public static final ForgeConfigSpec.DoubleValue XINSUO_TARGET_BOX_INFLATE;

    // 失熵回响相关配置
    public static final ForgeConfigSpec.IntValue SHISHANG_SANITY_COST;
    public static final ForgeConfigSpec.IntValue SHISHANG_COOLDOWN_TICKS;
    public static final ForgeConfigSpec.DoubleValue SHISHANG_DEATH_DAMAGE;

    // 无常回响相关配置
    public static final ForgeConfigSpec.IntValue WUCHANG_SANITY_COST;
    public static final ForgeConfigSpec.IntValue WUCHANG_COOLDOWN_TICKS;
    public static final ForgeConfigSpec.IntValue WUCHANG_HIGH_FAITH_COOLDOWN_TICKS;
    public static final ForgeConfigSpec.IntValue WUCHANG_HIGH_FAITH_THRESHOLD;
    public static final ForgeConfigSpec.IntValue WUCHANG_DURATION_TICKS;

    // 探囊回响相关配置
    public static final ForgeConfigSpec.IntValue TANNANG_SANITY_COST;
    public static final ForgeConfigSpec.IntValue TANNANG_COOLDOWN_TICKS;
    public static final ForgeConfigSpec.DoubleValue TANNANG_BASE_REACH;
    public static final ForgeConfigSpec.DoubleValue TANNANG_TARGET_BOX_INFLATE;
    public static final ForgeConfigSpec.IntValue TANNANG_KIDNEY_FAITH_COST;
    public static final ForgeConfigSpec.IntValue TANNANG_KIDNEY_HEALTH_LOSS;
    public static final ForgeConfigSpec.IntValue TANNANG_MAX_KIDNEY_COUNT;

    // 轮息回响相关配置
    public static final ForgeConfigSpec.IntValue LUNXI_SANITY_COST;
    public static final ForgeConfigSpec.IntValue LUNXI_COOLDOWN_TICKS;
    public static final ForgeConfigSpec.DoubleValue LUNXI_BASE_REACH;
    public static final ForgeConfigSpec.DoubleValue LUNXI_TARGET_BOX_INFLATE;

    // 嫁祸回响相关配置
    public static final ForgeConfigSpec.IntValue JIAHUO_SANITY_COST;
    public static final ForgeConfigSpec.IntValue JIAHUO_DAMAGE_SANITY_COST;
    public static final ForgeConfigSpec.DoubleValue JIAHUO_TRANSFER_RANGE;

    // 无终狩回响相关配置
    public static final ForgeConfigSpec.IntValue WUZHONGSHOU_MARK_SANITY_COST;
    public static final ForgeConfigSpec.IntValue WUZHONGSHOU_HUNT_DURATION_TICKS;
    public static final ForgeConfigSpec.DoubleValue WUZHONGSHOU_BASE_REACH;
    public static final ForgeConfigSpec.DoubleValue WUZHONGSHOU_TARGET_BOX_INFLATE;
    public static final ForgeConfigSpec.IntValue WUZHONGSHOU_HUNT_SUCCESS_FAITH;
    public static final ForgeConfigSpec.IntValue WUZHONGSHOU_HUNT_FAILURE_SANITY_LOSS;
    public static final ForgeConfigSpec.DoubleValue WUZHONGSHOU_WARNING_DISTANCE;
    public static final ForgeConfigSpec.DoubleValue WUZHONGSHOU_HUNT_INSTINCT_DAMAGE_PER_LEVEL;
    public static final ForgeConfigSpec.DoubleValue WUZHONGSHOU_HUNT_INSTINCT_SPEED_PER_LEVEL;

    // 茂木回响相关配置
    public static final ForgeConfigSpec.IntValue MAOMU_SANITY_COST;
    public static final ForgeConfigSpec.IntValue MAOMU_MIN_FAITH;
    public static final ForgeConfigSpec.IntValue MAOMU_MID_FAITH;
    public static final ForgeConfigSpec.IntValue MAOMU_FREE_SANITY_THRESHOLD;
    public static final ForgeConfigSpec.IntValue MAOMU_COOLDOWN_TICKS;
    public static final ForgeConfigSpec.DoubleValue MAOMU_EFFECT_RANGE;
    public static final ForgeConfigSpec.IntValue MAOMU_EFFECT_DURATION;
    public static final ForgeConfigSpec.IntValue MAOMU_SLOWNESS_LEVEL;
    public static final ForgeConfigSpec.IntValue MAOMU_ENTITY_LIFETIME;

    static {
        BUILDER.comment("十日终焉 - 回响配置");
        
        // 无垢回响配置
        BUILDER.push("无垢回响");
        
        WUGU_SANITY_COST = BUILDER
            .comment("无垢回响施法消耗的理智值")
            .defineInRange("sanity_cost", 100, 0, 1000);
            
        WUGU_SANITY_HEAL = BUILDER
            .comment("无垢回响恢复目标的理智值（高信念时会被倍率加成）")
            .defineInRange("sanity_heal", 400, 0, 1000);
            
        WUGU_MID_FAITH = BUILDER
            .comment("无垢回响的中等信念要求（达到此信念值时触发加成效果）")
            .defineInRange("mid_faith", 5, 0, 100);
            
        WUGU_COOLDOWN_TICKS = BUILDER
            .comment("无垢回响的冷却时间（tick，20tick = 1秒）")
            .defineInRange("cooldown_ticks", 1200, 0, 72000);
            
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

        // 硬化回响配置
        BUILDER.push("硬化回响");
        
        YINGHUA_SANITY_COST = BUILDER
            .comment("硬化回响激活消耗的理智值")
            .defineInRange("sanity_cost", 0, 0, 1000);
            
        YINGHUA_CONTINUOUS_SANITY_COST = BUILDER
            .comment("硬化回响持续状态下每秒消耗的理智值")
            .defineInRange("continuous_sanity_cost", 20, 0, 100);
            
        YINGHUA_HIGH_FAITH_THRESHOLD = BUILDER
            .comment("硬化回响高信念要求（达到此信念值时获得抗性2）")
            .defineInRange("high_faith_threshold", 5, 0, 100);
            
        YINGHUA_REFLECT_DAMAGE = BUILDER
            .comment("硬化回响反弹的固定伤害值")
            .defineInRange("reflect_damage", 4.0, 0.0, 20.0);
            
        YINGHUA_DAMAGE_SANITY_COST = BUILDER
            .comment("硬化回响受到一次伤害扣除的理智值")
            .defineInRange("damage_sanity_cost", 20, 0, 100);
            
        BUILDER.pop();

        // 涡流回响配置
        BUILDER.push("涡流回响");
        
        WOLIU_SANITY_COST = BUILDER
            .comment("涡流回响施法消耗的理智值")
            .defineInRange("sanity_cost", 50, 0, 1000);
            
        WOLIU_COOLDOWN_TICKS = BUILDER
            .comment("涡流回响的冷却时间（tick，20tick = 1秒）")
            .defineInRange("cooldown_ticks", 600, 0, 72000);
            
        WOLIU_RANGE = BUILDER
            .comment("涡流回响的吸引范围（方块）")
            .defineInRange("range", 10.0, 1.0, 32.0);
            
        WOLIU_ATTRACTION_STRENGTH = BUILDER
            .comment("涡流回响的吸引力强度")
            .defineInRange("attraction_strength", 1.5, 0.1, 5.0);
            
        WOLIU_FAITH_REQUIREMENT = BUILDER
            .comment("涡流回响冷却减半所需的信念值")
            .defineInRange("faith_requirement", 5, 0, 100);
            
        WOLIU_FAITH_RANGE_MULTIPLIER = BUILDER
            .comment("涡流回响每点信念对范围的增益倍率（范围=基础范围*(1+信念*倍率)）")
            .defineInRange("faith_range_multiplier", 0.1, 0.0, 1.0);
            
        WOLIU_FAITH_STRENGTH_MULTIPLIER = BUILDER
            .comment("涡流回响每点信念对引力强度的增益倍率（强度=基础强度*(1+信念*倍率)）")
            .defineInRange("faith_strength_multiplier", 0.15, 0.0, 1.0);
            
        BUILDER.pop();

        // 纳垢回响配置
        BUILDER.push("纳垢回响");
        
        NAGOU_SANITY_COST = BUILDER
            .comment("纳垢回响激活消耗的理智值")
            .defineInRange("sanity_cost", 50, 0, 1000);
            
        NAGOU_DIRT_COST_NORMAL = BUILDER
            .comment("纳垢回响普通情况下抵挡一次伤害消耗的泥土数量")
            .defineInRange("dirt_cost_normal", 30, 1, 64);
            
        NAGOU_DIRT_COST_HIGH_FAITH = BUILDER
            .comment("纳垢回响高信念时抵挡一次伤害消耗的泥土数量")
            .defineInRange("dirt_cost_high_faith", 20, 1, 64);
            
        NAGOU_HIGH_FAITH_THRESHOLD = BUILDER
            .comment("纳垢回响高信念阈值（达到此信念值时减少泥土消耗）")
            .defineInRange("high_faith_threshold", 5, 0, 100);
            
        BUILDER.pop();

        // 离析回响配置
        BUILDER.push("离析回响");
        
        LIXI_SANITY_COST = BUILDER
            .comment("离析回响施法消耗的理智值")
            .defineInRange("sanity_cost", 10, 0, 1000);
            
        LIXI_BASE_COOLDOWN_TICKS = BUILDER
            .comment("离析回响基础冷却时间（tick，20tick = 1秒）")
            .defineInRange("base_cooldown_ticks", 5, 0, 1200);
            
        LIXI_DAMAGE_COOLDOWN_TICKS = BUILDER
            .comment("离析回响造成伤害后的冷却时间（tick，20tick = 1秒）")
            .defineInRange("damage_cooldown_ticks", 160, 0, 1200);
            
        LIXI_FREE_COST_THRESHOLD = BUILDER
            .comment("离析回响免费释放的理智值阈值")
            .defineInRange("free_cost_threshold", 300, 0, 1000);
            
        LIXI_MIN_FAITH_REQUIREMENT = BUILDER
            .comment("离析回响免费释放的最低信念要求")
            .defineInRange("min_faith_requirement", 10, 0, 100);
            
        LIXI_MID_FAITH = BUILDER
            .comment("离析回响中等信念要求（达到此信念值时消耗减半且冷却减半）")
            .defineInRange("mid_faith", 5, 0, 100);
            
        LIXI_DAMAGE_FAITH = BUILDER
            .comment("离析回响造成伤害所需的信念值")
            .defineInRange("damage_faith", 8, 0, 100);
            
        LIXI_BASE_REACH = BUILDER
            .comment("离析回响基础施法距离（方块）")
            .defineInRange("base_reach", 32.0, 1.0, 128.0);
            
        LIXI_MID_REACH = BUILDER
            .comment("离析回响中等信念时的施法距离（方块）")
            .defineInRange("mid_reach", 48.0, 1.0, 128.0);
            
        LIXI_HIGH_REACH = BUILDER
            .comment("离析回响高信念时的施法距离（方块）")
            .defineInRange("high_reach", 64.0, 1.0, 128.0);
            
        LIXI_BASE_DAMAGE = BUILDER
            .comment("离析回响基础伤害值")
            .defineInRange("base_damage", 8.0, 0.0, 100.0);
            
        LIXI_DAMAGE_PER_FAITH = BUILDER
            .comment("离析回响每点信念增加的伤害值")
            .defineInRange("damage_per_faith", 1.0, 0.0, 10.0);
            
        LIXI_DAMAGE_RADIUS = BUILDER
            .comment("离析回响破坏方块时的伤害范围（方块）")
            .defineInRange("damage_radius", 5.0, 1.0, 32.0);
            
        LIXI_DIRECT_TARGET_DAMAGE_MULTIPLIER = BUILDER
            .comment("离析回响直接目标伤害倍率")
            .defineInRange("direct_target_damage_multiplier", 1.2, 0.1, 5.0);
            
        LIXI_CHARGING_DURATION_TICKS = BUILDER
            .comment("离析回响充能持续时间（tick，20tick = 1秒）")
            .defineInRange("charging_duration_ticks", 100, 20, 600);
        
        LIXI_UNBREAKABLE_BLOCKS = BUILDER
            .comment("离析回响无法破坏的方块黑名单（使用方块ID，如minecraft:bedrock）")
            .defineListAllowEmpty("unbreakable_blocks", 
                java.util.Arrays.asList("minecraft:bedrock", "minecraft:barrier", "minecraft:command_block", "minecraft:chain_command_block", "minecraft:repeating_command_block", "minecraft:structure_block", "minecraft:jigsaw", "minecraft:structure_void"),
                obj -> obj instanceof String);
            
        BUILDER.pop();

        // 心锁回响配置
        BUILDER.push("心锁回响");
        
        XINSUO_SANITY_COST = BUILDER
            .comment("心锁回响施法消耗的理智值")
            .defineInRange("sanity_cost", 100, 0, 1000);
            
        XINSUO_COOLDOWN_TICKS = BUILDER
            .comment("心锁回响的冷却时间（tick，20tick = 1秒）")
            .defineInRange("cooldown_ticks", 12000, 0, 72000);
            
        XINSUO_EFFECT_DURATION_TICKS = BUILDER
            .comment("心之印效果持续时间（tick，20tick = 1秒，6000tick = 5分钟）")
            .defineInRange("effect_duration_ticks", 6000, 1200, 72000);
            
        XINSUO_BASE_REACH = BUILDER
            .comment("心锁回响的基础施法距离（方块）")
            .defineInRange("base_reach", 32.0, 1.0, 64.0);
            
        XINSUO_TARGET_BOX_INFLATE = BUILDER
            .comment("心锁回响目标检测的碰撞箱扩展范围（方块）")
            .defineInRange("target_box_inflate", 1.0, 0.1, 3.0);
            
        BUILDER.pop();

        // 失熵回响配置
        BUILDER.push("失熵回响");
        
        SHISHANG_SANITY_COST = BUILDER
            .comment("失熵回响施法消耗的理智值")
            .defineInRange("sanity_cost", 400, 0, 1000);
            
        SHISHANG_COOLDOWN_TICKS = BUILDER
            .comment("失熵回响的冷却时间（tick，20tick = 1秒）")
            .defineInRange("cooldown_ticks", 1200, 0, 72000);
            
        SHISHANG_DEATH_DAMAGE = BUILDER
            .comment("失熵回响造成的死亡伤害（设置为高值确保必死）")
            .defineInRange("death_damage", 1000.0, 100.0, 10000.0);
            
        BUILDER.pop();

        // 无常回响配置
        BUILDER.push("无常回响");
        
        WUCHANG_SANITY_COST = BUILDER
            .comment("无常回响施法消耗的理智值")
            .defineInRange("sanity_cost", 50, 0, 1000);
            
        WUCHANG_COOLDOWN_TICKS = BUILDER
            .comment("无常回响的冷却时间（tick，20tick = 1秒，36000tick = 30分钟）")
            .defineInRange("cooldown_ticks", 36000, 0, 144000);
            
        WUCHANG_HIGH_FAITH_COOLDOWN_TICKS = BUILDER
            .comment("无常回响高信念时的冷却时间（tick，24000tick = 20分钟）")
            .defineInRange("high_faith_cooldown_ticks", 24000, 0, 144000);
            
        WUCHANG_HIGH_FAITH_THRESHOLD = BUILDER
            .comment("无常回响的高信念要求（达到此信念值时冷却时间减少）")
            .defineInRange("high_faith_threshold", 10, 0, 100);
            
        WUCHANG_DURATION_TICKS = BUILDER
            .comment("无常回响临时回响的持续时间（tick，36000tick = 30分钟）")
            .defineInRange("duration_ticks", 36000, 1200, 144000);
            
        BUILDER.pop();

        // 探囊回响配置
        BUILDER.push("探囊回响");
        
        TANNANG_SANITY_COST = BUILDER
            .comment("探囊回响施法消耗的理智值")
            .defineInRange("sanity_cost", 50, 0, 1000);
            
        TANNANG_COOLDOWN_TICKS = BUILDER
            .comment("探囊回响的冷却时间（tick，20tick = 1秒，400tick = 20秒）")
            .defineInRange("cooldown_ticks", 400, 0, 72000);
            
        TANNANG_BASE_REACH = BUILDER
            .comment("探囊回响的基础施法距离（方块）")
            .defineInRange("base_reach", 32.0, 1.0, 64.0);
            
        TANNANG_TARGET_BOX_INFLATE = BUILDER
            .comment("探囊回响目标检测的碰撞箱扩展范围（方块）")
            .defineInRange("target_box_inflate", 1.0, 0.1, 3.0);
            
        TANNANG_KIDNEY_FAITH_COST = BUILDER
            .comment("探囊回响掏腰子所需的信念值")
            .defineInRange("kidney_faith_cost", 10, 0, 100);
            
        TANNANG_KIDNEY_HEALTH_LOSS = BUILDER
            .comment("被掏腰子时扣除的生命值上限（单位：半颗心）")
            .defineInRange("kidney_health_loss", 8, 1, 20);
            
        TANNANG_MAX_KIDNEY_COUNT = BUILDER
            .comment("每个玩家最多可以被掏的腰子数量")
            .defineInRange("max_kidney_count", 2, 1, 10);
            
        BUILDER.pop();

        // 轮息回响配置
        BUILDER.push("轮息回响");
        
        LUNXI_SANITY_COST = BUILDER
            .comment("轮息回响施法消耗的理智值")
            .defineInRange("sanity_cost", 200, 0, 1000);
            
        LUNXI_COOLDOWN_TICKS = BUILDER
            .comment("轮息回响的冷却时间（tick，20tick = 1秒，12000tick = 10分钟）")
            .defineInRange("cooldown_ticks", 12000, 0, 72000);
            
        LUNXI_BASE_REACH = BUILDER
            .comment("轮息回响的基础施法距离（方块）")
            .defineInRange("base_reach", 32.0, 1.0, 64.0);
            
        LUNXI_TARGET_BOX_INFLATE = BUILDER
            .comment("轮息回响目标检测的碰撞箱扩展范围（方块）")
            .defineInRange("target_box_inflate", 1.0, 0.1, 3.0);
            
        BUILDER.pop();

        // 嫁祸回响配置
        BUILDER.push("嫁祸回响");
        
        JIAHUO_SANITY_COST = BUILDER
            .comment("嫁祸回响激活消耗的理智值")
            .defineInRange("sanity_cost", 0, 0, 1000);
            
        JIAHUO_DAMAGE_SANITY_COST = BUILDER
            .comment("嫁祸回响转移伤害时消耗的理智值")
            .defineInRange("damage_sanity_cost", 20, 0, 100);
            
        JIAHUO_TRANSFER_RANGE = BUILDER
            .comment("嫁祸回响的伤害转移检测范围（方块）")
            .defineInRange("transfer_range", 10.0, 1.0, 32.0);
            
        BUILDER.pop();

        // 无终狩回响配置
        BUILDER.push("无终狩回响");
        
        WUZHONGSHOU_MARK_SANITY_COST = BUILDER
            .comment("无终狩回响标记目标消耗的理智值")
            .defineInRange("mark_sanity_cost", 100, 0, 1000);
            
        WUZHONGSHOU_HUNT_DURATION_TICKS = BUILDER
            .comment("无终狩回响狩猎持续时间（tick，36000tick = 30分钟）")
            .defineInRange("hunt_duration_ticks", 36000, 1200, 144000);
            
        WUZHONGSHOU_BASE_REACH = BUILDER
            .comment("无终狩回响的基础施法距离（方块）")
            .defineInRange("base_reach", 32.0, 1.0, 64.0);
            
        WUZHONGSHOU_TARGET_BOX_INFLATE = BUILDER
            .comment("无终狩回响目标检测的碰撞箱扩展范围（方块）")
            .defineInRange("target_box_inflate", 1.0, 0.1, 3.0);
            
        WUZHONGSHOU_HUNT_SUCCESS_FAITH = BUILDER
            .comment("无终狩回响狩猎成功获得的信念点数")
            .defineInRange("hunt_success_faith", 2, 1, 10);
            
        WUZHONGSHOU_HUNT_FAILURE_SANITY_LOSS = BUILDER
            .comment("无终狩回响狩猎失败扣除的理智值")
            .defineInRange("hunt_failure_sanity_loss", 400, 0, 1000);
            
        WUZHONGSHOU_WARNING_DISTANCE = BUILDER
            .comment("无终狩回响警告显示距离（方块）")
            .defineInRange("warning_distance", 30.0, 1.0, 64.0);
            
        WUZHONGSHOU_HUNT_INSTINCT_DAMAGE_PER_LEVEL = BUILDER
            .comment("无终狩回响每级狩猎本能增加的近战伤害")
            .defineInRange("hunt_instinct_damage_per_level", 1.0, 0.0, 10.0);
            
        WUZHONGSHOU_HUNT_INSTINCT_SPEED_PER_LEVEL = BUILDER
            .comment("无终狩回响每级狩猎本能增加的攻击速度（百分比）")
            .defineInRange("hunt_instinct_speed_per_level", 5.0, 0.0, 50.0);
            
        BUILDER.pop();

        // 茂木回响配置
        BUILDER.push("茂木回响");
        
        MAOMU_SANITY_COST = BUILDER
            .comment("茂木回响施法消耗的理智值")
            .defineInRange("sanity_cost", 20, 0, 1000);
            
        MAOMU_MIN_FAITH = BUILDER
            .comment("免费施法所需的最低信念值")
            .defineInRange("min_faith", 10, 0, 20);
            
        MAOMU_MID_FAITH = BUILDER
            .comment("中等信念值（减半消耗和冷却）")
            .defineInRange("mid_faith", 5, 0, 20);
            
        MAOMU_FREE_SANITY_THRESHOLD = BUILDER
            .comment("免费施法的理智值阈值（低于此值时不消耗理智）")
            .defineInRange("free_sanity_threshold", 300, 0, 1000);
            
        MAOMU_COOLDOWN_TICKS = BUILDER
            .comment("茂木回响冷却时间（tick）")
            .defineInRange("cooldown_ticks", 100, 0, 1200);
            
        MAOMU_EFFECT_RANGE = BUILDER
            .comment("茂木造物的缓慢效果范围（方块）")
            .defineInRange("effect_range", 5.0, 1.0, 10.0);
            
        MAOMU_EFFECT_DURATION = BUILDER
            .comment("缓慢效果持续时间（tick）")
            .defineInRange("effect_duration", 60, 20, 200);
            
        MAOMU_SLOWNESS_LEVEL = BUILDER
            .comment("缓慢效果等级（0-9）")
            .defineInRange("slowness_level", 4, 0, 9);
            
        MAOMU_ENTITY_LIFETIME = BUILDER
            .comment("茂木造物存在时间（tick）")
            .defineInRange("entity_lifetime", 200, 100, 1200);
            
        BUILDER.pop();

        SPEC = BUILDER.build();
    }

    public static void register() {
        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, SPEC, "doomsday-echoes.toml");
    }
} 