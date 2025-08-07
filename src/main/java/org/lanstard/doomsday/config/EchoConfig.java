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
    public static final ForgeConfigSpec.IntValue WUZHONGSHOU_MARK_COOLDOWN_TICKS;
    public static final ForgeConfigSpec.IntValue WUZHONGSHOU_HUNTED_MARK_SPEED_LEVEL;
    public static final ForgeConfigSpec.IntValue WUZHONGSHOU_HUNTED_MARK_WEAKNESS_LEVEL;
    public static final ForgeConfigSpec.IntValue WUZHONGSHOU_ENDER_PEARL_COUNT;
    public static final ForgeConfigSpec.DoubleValue WUZHONGSHOU_HUNT_INSTINCT_MOVEMENT_SPEED_PER_LEVEL;
    public static final ForgeConfigSpec.DoubleValue WUZHONGSHOU_HUNT_INSTINCT_ARMOR_PER_LEVEL;
    public static final ForgeConfigSpec.DoubleValue WUZHONGSHOU_HUNT_INSTINCT_KNOCKBACK_RESISTANCE_PER_LEVEL;

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

    // 爆闪回响相关配置
    public static final ForgeConfigSpec.IntValue BAOSHAN_SANITY_COST;
    public static final ForgeConfigSpec.IntValue BAOSHAN_COOLDOWN_TICKS;
    public static final ForgeConfigSpec.IntValue BAOSHAN_MID_FAITH;
    public static final ForgeConfigSpec.IntValue BAOSHAN_MIN_FAITH;
    public static final ForgeConfigSpec.IntValue BAOSHAN_FREE_COST_THRESHOLD;
    public static final ForgeConfigSpec.DoubleValue BAOSHAN_BASE_SPEED;
    public static final ForgeConfigSpec.DoubleValue BAOSHAN_MID_SPEED;
    public static final ForgeConfigSpec.DoubleValue BAOSHAN_HIGH_SPEED;
    public static final ForgeConfigSpec.DoubleValue BAOSHAN_BASE_INACCURACY;
    public static final ForgeConfigSpec.DoubleValue BAOSHAN_MID_INACCURACY;
    public static final ForgeConfigSpec.DoubleValue BAOSHAN_SANITY_COST_REDUCTION_RATIO;
    public static final ForgeConfigSpec.IntValue BAOSHAN_BASE_ENHANCE_LEVEL;
    public static final ForgeConfigSpec.IntValue BAOSHAN_MID_ENHANCE_LEVEL;
    public static final ForgeConfigSpec.IntValue BAOSHAN_HIGH_ENHANCE_LEVEL;
    public static final ForgeConfigSpec.IntValue BAOSHAN_COOLDOWN_MILLISECONDS_PER_TICK;

    // 劲风回响相关配置
    public static final ForgeConfigSpec.IntValue JINFENG_SANITY_COST;
    public static final ForgeConfigSpec.IntValue JINFENG_COOLDOWN_TICKS;
    public static final ForgeConfigSpec.IntValue JINFENG_EFFECT_DURATION;
    public static final ForgeConfigSpec.DoubleValue JINFENG_BASE_PUSH_RANGE;
    public static final ForgeConfigSpec.DoubleValue JINFENG_MID_PUSH_RANGE;
    public static final ForgeConfigSpec.DoubleValue JINFENG_HIGH_PUSH_RANGE;
    public static final ForgeConfigSpec.DoubleValue JINFENG_BASE_PUSH_STRENGTH;
    public static final ForgeConfigSpec.DoubleValue JINFENG_MID_PUSH_STRENGTH;
    public static final ForgeConfigSpec.DoubleValue JINFENG_HIGH_PUSH_STRENGTH;
    public static final ForgeConfigSpec.IntValue JINFENG_FREE_COST_THRESHOLD;
    public static final ForgeConfigSpec.IntValue JINFENG_MIN_FAITH;
    public static final ForgeConfigSpec.IntValue JINFENG_MID_FAITH;

    // 蛮力回响相关配置
    public static final ForgeConfigSpec.IntValue MANLI_SANITY_COST;
    public static final ForgeConfigSpec.IntValue MANLI_COOLDOWN_TICKS;
    public static final ForgeConfigSpec.IntValue MANLI_EFFECT_DURATION;
    public static final ForgeConfigSpec.IntValue MANLI_STRENGTH_AMPLIFIER;
    public static final ForgeConfigSpec.IntValue MANLI_RESISTANCE_AMPLIFIER;
    public static final ForgeConfigSpec.IntValue MANLI_INCREASE_MAX_HEALTH;
    public static final ForgeConfigSpec.IntValue MANLI_FREE_COST_THRESHOLD;
    public static final ForgeConfigSpec.IntValue MANLI_MIN_FAITH;
    public static final ForgeConfigSpec.IntValue MANLI_MID_FAITH;

    // 隐匿回响相关配置
    public static final ForgeConfigSpec.IntValue YINNI_TOGGLE_SANITY_COST;
    public static final ForgeConfigSpec.IntValue YINNI_CONTINUOUS_SANITY_COST;
    public static final ForgeConfigSpec.IntValue YINNI_FREE_COST_THRESHOLD;
    public static final ForgeConfigSpec.IntValue YINNI_MIN_FAITH;
    public static final ForgeConfigSpec.IntValue YINNI_INVISIBILITY_DURATION;
    public static final ForgeConfigSpec.IntValue YINNI_HIGH_FAITH_DURATION;
    public static final ForgeConfigSpec.DoubleValue YINNI_GROUP_RANGE;
    public static final ForgeConfigSpec.IntValue YINNI_MAX_TARGETS;

    // 夺心魄回响相关配置
    public static final ForgeConfigSpec.DoubleValue DUOXINPO_RANGE;
    public static final ForgeConfigSpec.DoubleValue DUOXINPO_CONTROL_RANGE;
    public static final ForgeConfigSpec.IntValue DUOXINPO_CONTROL_DURATION;
    public static final ForgeConfigSpec.IntValue DUOXINPO_SANITY_DRAIN;
    public static final ForgeConfigSpec.IntValue DUOXINPO_MIN_SANITY;
    public static final ForgeConfigSpec.IntValue DUOXINPO_BELIEF_THRESHOLD;
    public static final ForgeConfigSpec.IntValue DUOXINPO_FREE_COST_THRESHOLD;
    public static final ForgeConfigSpec.IntValue DUOXINPO_CONTINUOUS_SANITY_COST;
    public static final ForgeConfigSpec.IntValue DUOXINPO_ACTIVE_SANITY_COST;
    public static final ForgeConfigSpec.IntValue DUOXINPO_TOGGLE_SANITY_COST;

    // 祸水回响相关配置
    public static final ForgeConfigSpec.DoubleValue HUOSHUI_BASE_RANGE;
    public static final ForgeConfigSpec.DoubleValue HUOSHUI_MID_RANGE;
    public static final ForgeConfigSpec.IntValue HUOSHUI_BASE_FIRE_COUNT;
    public static final ForgeConfigSpec.IntValue HUOSHUI_MID_FIRE_COUNT;
    public static final ForgeConfigSpec.IntValue HUOSHUI_BASE_FIRE_DURATION;
    public static final ForgeConfigSpec.IntValue HUOSHUI_MID_FIRE_DURATION;
    public static final ForgeConfigSpec.IntValue HUOSHUI_BASE_SANITY_REDUCTION;
    public static final ForgeConfigSpec.IntValue HUOSHUI_MID_SANITY_REDUCTION;
    public static final ForgeConfigSpec.IntValue HUOSHUI_NORMAL_COOLDOWN;
    public static final ForgeConfigSpec.IntValue HUOSHUI_LOW_SANITY_COOLDOWN;
    public static final ForgeConfigSpec.IntValue HUOSHUI_LOW_SANITY_THRESHOLD;
    public static final ForgeConfigSpec.IntValue HUOSHUI_MID_FAITH;
    public static final ForgeConfigSpec.IntValue HUOSHUI_MIN_FAITH;
    public static final ForgeConfigSpec.IntValue HUOSHUI_FREE_COST_THRESHOLD;

    // 双生花回响相关配置
    public static final ForgeConfigSpec.DoubleValue SHUANGSHENGHUA_PASSIVE_RANGE;
    public static final ForgeConfigSpec.IntValue SHUANGSHENGHUA_PASSIVE_SYNC_CD;
    public static final ForgeConfigSpec.IntValue SHUANGSHENGHUA_CONTINUOUS_SANITY_COST;
    public static final ForgeConfigSpec.IntValue SHUANGSHENGHUA_TOGGLE_SANITY_COST;
    public static final ForgeConfigSpec.IntValue SHUANGSHENGHUA_ACTIVE_SANITY_COST;
    public static final ForgeConfigSpec.IntValue SHUANGSHENGHUA_ACTIVE_DURATION;
    public static final ForgeConfigSpec.IntValue SHUANGSHENGHUA_ACTIVE_COOLDOWN;
    public static final ForgeConfigSpec.IntValue SHUANGSHENGHUA_DAMAGE_SHARE_CD;
    public static final ForgeConfigSpec.IntValue SHUANGSHENGHUA_LOW_SANITY_THRESHOLD;
    public static final ForgeConfigSpec.IntValue SHUANGSHENGHUA_FREE_COST_THRESHOLD;

    // 惊雷回响相关配置
    public static final ForgeConfigSpec.IntValue JINGLEI_SANITY_COST;
    public static final ForgeConfigSpec.IntValue JINGLEI_MIN_FAITH;
    public static final ForgeConfigSpec.IntValue JINGLEI_MID_FAITH;
    public static final ForgeConfigSpec.IntValue JINGLEI_FREE_COST_THRESHOLD;
    public static final ForgeConfigSpec.DoubleValue JINGLEI_RANGE;
    public static final ForgeConfigSpec.IntValue JINGLEI_BASE_COOLDOWN;
    public static final ForgeConfigSpec.DoubleValue JINGLEI_BASE_DAMAGE;
    public static final ForgeConfigSpec.DoubleValue JINGLEI_DAMAGE_PER_FAITH;

    // 生生不息回响相关配置
    public static final ForgeConfigSpec.DoubleValue SHENGSHENGBUXI_REGENERATION_RANGE;
    public static final ForgeConfigSpec.IntValue SHENGSHENGBUXI_MIN_SANITY_REQUIREMENT;
    public static final ForgeConfigSpec.IntValue SHENGSHENGBUXI_MIN_BELIEF_REQUIREMENT;
    public static final ForgeConfigSpec.IntValue SHENGSHENGBUXI_FREE_COST_THRESHOLD;

    // 不灭回响相关配置
    public static final ForgeConfigSpec.IntValue BUMIE_SANITY_COST;
    public static final ForgeConfigSpec.IntValue BUMIE_MID_FAITH;
    public static final ForgeConfigSpec.IntValue BUMIE_MIN_FAITH;
    public static final ForgeConfigSpec.IntValue BUMIE_FREE_COST_THRESHOLD;
    public static final ForgeConfigSpec.IntValue BUMIE_DURATION_TICKS;
    public static final ForgeConfigSpec.IntValue BUMIE_COOLDOWN_TICKS;
    public static final ForgeConfigSpec.DoubleValue BUMIE_BASE_RETALIATION_DAMAGE;
    public static final ForgeConfigSpec.DoubleValue BUMIE_MID_RETALIATION_DAMAGE;
    public static final ForgeConfigSpec.DoubleValue BUMIE_HIGH_RETALIATION_DAMAGE;

    // 跃迁回响相关配置
    public static final ForgeConfigSpec.IntValue YUEQIAN_SANITY_COST;
    public static final ForgeConfigSpec.IntValue YUEQIAN_MIN_FAITH;
    public static final ForgeConfigSpec.IntValue YUEQIAN_FREE_COST_THRESHOLD;
    public static final ForgeConfigSpec.DoubleValue YUEQIAN_MAX_RANGE;
    public static final ForgeConfigSpec.IntValue YUEQIAN_COOLDOWN_TICKS;

    // 治愈回响相关配置
    public static final ForgeConfigSpec.IntValue ZHIYU_SANITY_COST;
    public static final ForgeConfigSpec.IntValue ZHIYU_MIN_FAITH;
    public static final ForgeConfigSpec.IntValue ZHIYU_FREE_COST_THRESHOLD;
    public static final ForgeConfigSpec.IntValue ZHIYU_CONTINUOUS_SANITY_COST;
    public static final ForgeConfigSpec.DoubleValue ZHIYU_HEAL_RANGE;
    public static final ForgeConfigSpec.IntValue ZHIYU_BASE_MAX_SANITY;
    public static final ForgeConfigSpec.IntValue ZHIYU_HIGH_MAX_SANITY;
    public static final ForgeConfigSpec.IntValue ZHIYU_ACTIVE_DURATION;
    public static final ForgeConfigSpec.IntValue ZHIYU_COOLDOWN_TICKS;

    // 感知类回响共同配置 (灵触、灵嗅、灵闻、灵视)
    public static final ForgeConfigSpec.IntValue LING_SANITY_COST;
    public static final ForgeConfigSpec.IntValue LING_FREE_COST_THRESHOLD;
    public static final ForgeConfigSpec.IntValue LING_MIN_FAITH;
    public static final ForgeConfigSpec.IntValue LING_MID_FAITH;
    public static final ForgeConfigSpec.DoubleValue LING_BASE_RANGE;
    public static final ForgeConfigSpec.DoubleValue LING_MID_RANGE;
    public static final ForgeConfigSpec.DoubleValue LING_HIGH_RANGE;

    // 寒冰回响相关配置
    public static final ForgeConfigSpec.IntValue HANBING_SANITY_COST;
    public static final ForgeConfigSpec.IntValue HANBING_COOLDOWN_TICKS;
    public static final ForgeConfigSpec.IntValue HANBING_MID_FAITH;
    public static final ForgeConfigSpec.IntValue HANBING_MIN_FAITH;
    public static final ForgeConfigSpec.IntValue HANBING_FREE_COST_THRESHOLD;
    
    // 寒冰实体配置
    public static final ForgeConfigSpec.DoubleValue HANBING_ICE_DAMAGE;
    public static final ForgeConfigSpec.DoubleValue HANBING_ICE_ENHANCED_DAMAGE;
    public static final ForgeConfigSpec.DoubleValue HANBING_ICE_SPLIT_DAMAGE;
    public static final ForgeConfigSpec.DoubleValue HANBING_ICE_ENHANCED_SPLIT_DAMAGE;
    public static final ForgeConfigSpec.DoubleValue HANBING_ICE_SPLIT_SPEED_MULTIPLIER;
    public static final ForgeConfigSpec.IntValue HANBING_ICE_MAX_SPLIT;

    // 破万法回响相关配置
    public static final ForgeConfigSpec.DoubleValue BREAKALL_RANGE;
    public static final ForgeConfigSpec.IntValue BREAKALL_DISABLE_DURATION;
    public static final ForgeConfigSpec.IntValue BREAKALL_COOLDOWN_TICKS;
    public static final ForgeConfigSpec.IntValue BREAKALL_SANITY_COST;
    public static final ForgeConfigSpec.IntValue BREAKALL_FREE_COST_THRESHOLD;

    // 招灾回响相关配置
    public static final ForgeConfigSpec.DoubleValue ZHAOZAI_RANGE;
    public static final ForgeConfigSpec.IntValue ZHAOZAI_WITHER_DURATION;
    public static final ForgeConfigSpec.IntValue ZHAOZAI_WITHER_AMPLIFIER;
    public static final ForgeConfigSpec.IntValue ZHAOZAI_GLOWING_DURATION;
    public static final ForgeConfigSpec.IntValue ZHAOZAI_CHECK_INTERVAL;
    public static final ForgeConfigSpec.DoubleValue ZHAOZAI_BASE_SUCCESS_RATE;
    public static final ForgeConfigSpec.DoubleValue ZHAOZAI_HIGH_SUCCESS_RATE;
    public static final ForgeConfigSpec.DoubleValue ZHAOZAI_HIGH_FAITH_RANGE;
    public static final ForgeConfigSpec.IntValue ZHAOZAI_SUMMON_COST;
    public static final ForgeConfigSpec.IntValue ZHAOZAI_COOLDOWN_TICKS;
    public static final ForgeConfigSpec.IntValue ZHAOZAI_MIN_FAITH;
    public static final ForgeConfigSpec.IntValue ZHAOZAI_FREE_COST_THRESHOLD;

    // 挪移回响相关配置
    public static final ForgeConfigSpec.IntValue NUOYI_GIVE_COST;
    public static final ForgeConfigSpec.IntValue NUOYI_TAKE_COST;
    public static final ForgeConfigSpec.IntValue NUOYI_COOLDOWN_TICKS;
    public static final ForgeConfigSpec.IntValue NUOYI_FREE_COST_THRESHOLD;
    public static final ForgeConfigSpec.IntValue NUOYI_MIN_FAITH;

    // 替罪回响相关配置
    public static final ForgeConfigSpec.IntValue TIZUI_SANITY_COST;
    public static final ForgeConfigSpec.IntValue TIZUI_FREE_COST_THRESHOLD;
    public static final ForgeConfigSpec.IntValue TIZUI_MIN_FAITH;
    public static final ForgeConfigSpec.DoubleValue TIZUI_SELECTION_RANGE;

    // 缩千山回响相关配置
    public static final ForgeConfigSpec.IntValue SUOQIANSHAN_TOGGLE_SANITY_COST;
    public static final ForgeConfigSpec.IntValue SUOQIANSHAN_CONTINUOUS_SANITY_COST;
    public static final ForgeConfigSpec.IntValue SUOQIANSHAN_ACTIVE_SANITY_COST;
    public static final ForgeConfigSpec.IntValue SUOQIANSHAN_FREE_COST_THRESHOLD;
    public static final ForgeConfigSpec.IntValue SUOQIANSHAN_HIGH_FAITH;
    public static final ForgeConfigSpec.IntValue SUOQIANSHAN_COOLDOWN_TICKS;
    public static final ForgeConfigSpec.IntValue SUOQIANSHAN_SPEED_LEVEL;
    public static final ForgeConfigSpec.IntValue SUOQIANSHAN_JUMP_LEVEL;
    public static final ForgeConfigSpec.IntValue SUOQIANSHAN_SLOW_FALLING_LEVEL;

    // 天行健回响相关配置
    public static final ForgeConfigSpec.IntValue TIANXINGJIAN_COOLDOWN_TICKS;
    public static final ForgeConfigSpec.IntValue TIANXINGJIAN_EFFECT_DURATION;
    public static final ForgeConfigSpec.IntValue TIANXINGJIAN_LOW_SANITY_THRESHOLD;
    public static final ForgeConfigSpec.IntValue TIANXINGJIAN_FREE_COST_THRESHOLD;
    public static final ForgeConfigSpec.IntValue TIANXINGJIAN_PASSIVE_DURATION;
    public static final ForgeConfigSpec.IntValue TIANXINGJIAN_MID_FAITH;
    public static final ForgeConfigSpec.IntValue TIANXINGJIAN_HIGH_FAITH;
    public static final ForgeConfigSpec.IntValue TIANXINGJIAN_BASE_HEALTH_INCREASE;
    public static final ForgeConfigSpec.IntValue TIANXINGJIAN_MID_HEALTH_INCREASE;
    public static final ForgeConfigSpec.IntValue TIANXINGJIAN_HIGH_HEALTH_INCREASE;

    // 忘忧回响相关配置
    public static final ForgeConfigSpec.IntValue WANGYOU_SANITY_COST;
    public static final ForgeConfigSpec.IntValue WANGYOU_MIN_FAITH;
    public static final ForgeConfigSpec.IntValue WANGYOU_FREE_COST_THRESHOLD;
    public static final ForgeConfigSpec.IntValue WANGYOU_CONTINUOUS_SANITY_COST;
    public static final ForgeConfigSpec.DoubleValue WANGYOU_BASE_DAMAGE_REDUCTION;
    public static final ForgeConfigSpec.DoubleValue WANGYOU_FAITH_DAMAGE_REDUCTION;

    // 原物回响相关配置
    public static final ForgeConfigSpec.IntValue YUANWU_SANITY_COST;
    public static final ForgeConfigSpec.IntValue YUANWU_MIN_FAITH;
    public static final ForgeConfigSpec.IntValue YUANWU_FREE_COST_THRESHOLD;
    public static final ForgeConfigSpec.IntValue YUANWU_COOLDOWN_TICKS;

    // 御神君回响相关配置
    public static final ForgeConfigSpec.DoubleValue YUSHENJUN_RANGE;
    public static final ForgeConfigSpec.IntValue YUSHENJUN_EFFECT_DURATION;
    public static final ForgeConfigSpec.IntValue YUSHENJUN_SUMMON_DURATION;
    public static final ForgeConfigSpec.IntValue YUSHENJUN_COOLDOWN_TICKS;
    public static final ForgeConfigSpec.IntValue YUSHENJUN_ACTIVE_SANITY_COST;
    public static final ForgeConfigSpec.IntValue YUSHENJUN_TOGGLE_SANITY_COST;
    public static final ForgeConfigSpec.IntValue YUSHENJUN_CONTINUOUS_SANITY_COST;

    // 滞空回响相关配置
    public static final ForgeConfigSpec.IntValue ZHIKONG_TOGGLE_SANITY_COST;
    public static final ForgeConfigSpec.IntValue ZHIKONG_CONTINUOUS_SANITY_COST;
    public static final ForgeConfigSpec.IntValue ZHIKONG_FREE_COST_THRESHOLD;
    public static final ForgeConfigSpec.IntValue ZHIKONG_MIN_FAITH;
    public static final ForgeConfigSpec.IntValue ZHIKONG_EFFECT_DURATION;
    public static final ForgeConfigSpec.IntValue ZHIKONG_EFFECT_LEVEL;

    // 傀儡回响相关配置
    public static final ForgeConfigSpec.IntValue KUILEI_SANITY_COST;
    public static final ForgeConfigSpec.IntValue KUILEI_COOLDOWN_TICKS;
    public static final ForgeConfigSpec.IntValue KUILEI_FREE_COST_THRESHOLD;
    public static final ForgeConfigSpec.IntValue KUILEI_MIN_FAITH_REQUIREMENT;
    public static final ForgeConfigSpec.IntValue KUILEI_MID_FAITH;
    public static final ForgeConfigSpec.DoubleValue KUILEI_PUPPET_SCALE;

    // 乾坤回响相关配置
    public static final ForgeConfigSpec.IntValue QIANKUN_TOGGLE_SANITY_COST;
    public static final ForgeConfigSpec.IntValue QIANKUN_CONTINUOUS_SANITY_COST;
    public static final ForgeConfigSpec.IntValue QIANKUN_ACTIVE_SANITY_INCREASE;
    public static final ForgeConfigSpec.IntValue QIANKUN_FAITH_GAIN;
    public static final ForgeConfigSpec.IntValue QIANKUN_SANITY_THRESHOLD;
    public static final ForgeConfigSpec.IntValue QIANKUN_FREE_COST_THRESHOLD;
    public static final ForgeConfigSpec.IntValue QIANKUN_COOLDOWN_TICKS;

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
            
        WUZHONGSHOU_MARK_COOLDOWN_TICKS = BUILDER
            .comment("无终狩回响标记冷却时间（tick，36000tick = 30分钟）")
            .defineInRange("mark_cooldown_ticks", 36000, 0, 144000);
            
        WUZHONGSHOU_HUNTED_MARK_SPEED_LEVEL = BUILDER
            .comment("被狩猎标记给予的速度等级（1 = 速度II）")
            .defineInRange("hunted_mark_speed_level", 1, 0, 5);
            
        WUZHONGSHOU_HUNTED_MARK_WEAKNESS_LEVEL = BUILDER
            .comment("被狩猎标记给予的虚弱等级（0 = 虚弱I）")
            .defineInRange("hunted_mark_weakness_level", 0, 0, 5);
            
        WUZHONGSHOU_ENDER_PEARL_COUNT = BUILDER
            .comment("无终狩回响给予狩猎者的末影珍珠数量")
            .defineInRange("ender_pearl_count", 4, 0, 16);
            
        WUZHONGSHOU_HUNT_INSTINCT_MOVEMENT_SPEED_PER_LEVEL = BUILDER
            .comment("无终狩回响每级狩猎本能增加的移动速度（百分比）")
            .defineInRange("hunt_instinct_movement_speed_per_level", 3.0, 0.0, 50.0);
            
        WUZHONGSHOU_HUNT_INSTINCT_ARMOR_PER_LEVEL = BUILDER
            .comment("无终狩回响每级狩猎本能增加的护甲值")
            .defineInRange("hunt_instinct_armor_per_level", 0.5, 0.0, 5.0);
            
        WUZHONGSHOU_HUNT_INSTINCT_KNOCKBACK_RESISTANCE_PER_LEVEL = BUILDER
            .comment("无终狩回响每级狩猎本能增加的击退抗性（百分比）")
            .defineInRange("hunt_instinct_knockback_resistance_per_level", 5.0, 0.0, 50.0);
            
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

        // 爆闪回响配置
        BUILDER.push("爆闪回响");
        
        BAOSHAN_SANITY_COST = BUILDER
            .comment("爆闪回响施法消耗的理智值")
            .defineInRange("sanity_cost", 10, 0, 1000);
            
        BAOSHAN_COOLDOWN_TICKS = BUILDER
            .comment("爆闪回响的冷却时间（tick，20tick = 1秒）")
            .defineInRange("cooldown_ticks", 160, 0, 72000);
            
        BAOSHAN_MID_FAITH = BUILDER
            .comment("爆闪回响的中等信念要求")
            .defineInRange("mid_faith", 5, 0, 100);
            
        BAOSHAN_MIN_FAITH = BUILDER
            .comment("爆闪回响的最小信念要求（达到此信念值时可以免费释放）")
            .defineInRange("min_faith", 10, 0, 100);
            
        BAOSHAN_FREE_COST_THRESHOLD = BUILDER
            .comment("爆闪回响免费释放的理智值阈值")
            .defineInRange("free_cost_threshold", 300, 0, 1000);
            
        BAOSHAN_BASE_SPEED = BUILDER
            .comment("爆闪弹基础投掷速度")
            .defineInRange("base_speed", 1.5, 0.1, 10.0);
            
        BAOSHAN_MID_SPEED = BUILDER
            .comment("爆闪弹中等信念投掷速度")
            .defineInRange("mid_speed", 2.0, 0.1, 10.0);
            
        BAOSHAN_HIGH_SPEED = BUILDER
            .comment("爆闪弹高等信念投掷速度")
            .defineInRange("high_speed", 2.5, 0.1, 10.0);
            
        BAOSHAN_BASE_INACCURACY = BUILDER
            .comment("爆闪弹基础不精确度（越大越不精确）")
            .defineInRange("base_inaccuracy", 1.0, 0.0, 5.0);
            
        BAOSHAN_MID_INACCURACY = BUILDER
            .comment("爆闪弹中等信念不精确度")
            .defineInRange("mid_inaccuracy", 0.5, 0.0, 5.0);
            
        BAOSHAN_SANITY_COST_REDUCTION_RATIO = BUILDER
            .comment("爆闪回响理智消耗减少比例（中等信念时）")
            .defineInRange("sanity_cost_reduction_ratio", 0.5, 0.0, 1.0);
            
        BAOSHAN_BASE_ENHANCE_LEVEL = BUILDER
            .comment("爆闪弹基础强化等级")
            .defineInRange("base_enhance_level", 0, 0, 10);
            
        BAOSHAN_MID_ENHANCE_LEVEL = BUILDER
            .comment("爆闪弹中等信念强化等级")
            .defineInRange("mid_enhance_level", 1, 0, 10);
            
        BAOSHAN_HIGH_ENHANCE_LEVEL = BUILDER
            .comment("爆闪弹高等信念强化等级")
            .defineInRange("high_enhance_level", 2, 0, 10);
            
        BAOSHAN_COOLDOWN_MILLISECONDS_PER_TICK = BUILDER
            .comment("爆闪回响冷却时间tick转毫秒系数")
            .defineInRange("cooldown_milliseconds_per_tick", 50, 1, 1000);
            
        BUILDER.pop();

        // 劲风回响配置
        BUILDER.push("劲风回响");
        
        JINFENG_SANITY_COST = BUILDER
            .comment("劲风回响施法消耗的理智值")
            .defineInRange("sanity_cost", 100, 0, 1000);
            
        JINFENG_COOLDOWN_TICKS = BUILDER
            .comment("劲风回响的冷却时间（tick，20tick = 1秒）")
            .defineInRange("cooldown_ticks", 1200, 0, 72000);
            
        JINFENG_EFFECT_DURATION = BUILDER
            .comment("劲风效果持续时间（tick）")
            .defineInRange("effect_duration", 100, 20, 1200);
            
        JINFENG_BASE_PUSH_RANGE = BUILDER
            .comment("劲风基础推动范围（方块）")
            .defineInRange("base_push_range", 10.0, 1.0, 32.0);
            
        JINFENG_MID_PUSH_RANGE = BUILDER
            .comment("劲风中等信念推动范围（方块）")
            .defineInRange("mid_push_range", 15.0, 1.0, 32.0);
            
        JINFENG_HIGH_PUSH_RANGE = BUILDER
            .comment("劲风高等信念推动范围（方块）")
            .defineInRange("high_push_range", 20.0, 1.0, 32.0);
            
        JINFENG_BASE_PUSH_STRENGTH = BUILDER
            .comment("劲风基础推动力度")
            .defineInRange("base_push_strength", 1.0, 0.1, 5.0);
            
        JINFENG_MID_PUSH_STRENGTH = BUILDER
            .comment("劲风中等信念推动力度")
            .defineInRange("mid_push_strength", 1.5, 0.1, 5.0);
            
        JINFENG_HIGH_PUSH_STRENGTH = BUILDER
            .comment("劲风高等信念推动力度")
            .defineInRange("high_push_strength", 2.0, 0.1, 5.0);
            
        JINFENG_FREE_COST_THRESHOLD = BUILDER
            .comment("劲风免费释放的理智值阈值")
            .defineInRange("free_cost_threshold", 300, 0, 1000);
            
        JINFENG_MIN_FAITH = BUILDER
            .comment("劲风最小信念要求")
            .defineInRange("min_faith", 10, 0, 100);
            
        JINFENG_MID_FAITH = BUILDER
            .comment("劲风中等信念要求")
            .defineInRange("mid_faith", 5, 0, 100);
            
        BUILDER.pop();

        // 蛮力回响配置
        BUILDER.push("蛮力回响");
        
        MANLI_SANITY_COST = BUILDER
            .comment("蛮力回响施法消耗的理智值")
            .defineInRange("sanity_cost", 150, 0, 1000);
            
        MANLI_COOLDOWN_TICKS = BUILDER
            .comment("蛮力回响的冷却时间（tick，20tick = 1秒）")
            .defineInRange("cooldown_ticks", 2400, 0, 72000);
            
        MANLI_EFFECT_DURATION = BUILDER
            .comment("蛮力效果持续时间（tick）")
            .defineInRange("effect_duration", 1200, 200, 12000);
            
        MANLI_STRENGTH_AMPLIFIER = BUILDER
            .comment("蛮力效果力量等级")
            .defineInRange("strength_amplifier", 1, 0, 10);
            
        MANLI_RESISTANCE_AMPLIFIER = BUILDER
            .comment("蛮力效果抗性等级")
            .defineInRange("resistance_amplifier", 0, 0, 10);
            
        MANLI_INCREASE_MAX_HEALTH = BUILDER
            .comment("蛮力效果提升最大生命值")
            .defineInRange("increase_max_health", 40, 0, 100);
            
        MANLI_FREE_COST_THRESHOLD = BUILDER
            .comment("蛮力免费释放的理智值阈值")
            .defineInRange("free_cost_threshold", 300, 0, 1000);
            
        MANLI_MIN_FAITH = BUILDER
            .comment("蛮力最小信念要求")
            .defineInRange("min_faith", 10, 0, 100);
            
        MANLI_MID_FAITH = BUILDER
            .comment("蛮力中等信念要求")
            .defineInRange("mid_faith", 5, 0, 100);
            
        BUILDER.pop();

        // 隐匿回响配置
        BUILDER.push("隐匿回响");
        
        YINNI_TOGGLE_SANITY_COST = BUILDER
            .comment("隐匿回响开启消耗的理智值")
            .defineInRange("toggle_sanity_cost", 30, 0, 1000);
            
        YINNI_CONTINUOUS_SANITY_COST = BUILDER
            .comment("隐匿回响每秒持续消耗的理智值")
            .defineInRange("continuous_sanity_cost", 1, 0, 100);
            
        YINNI_FREE_COST_THRESHOLD = BUILDER
            .comment("隐匿免费释放的理智值阈值")
            .defineInRange("free_cost_threshold", 300, 0, 1000);
            
        YINNI_MIN_FAITH = BUILDER
            .comment("隐匿最小信念要求")
            .defineInRange("min_faith", 10, 0, 100);
            
        YINNI_INVISIBILITY_DURATION = BUILDER
            .comment("隐匿隐身持续时间（tick）")
            .defineInRange("invisibility_duration", 40, 20, 200);
            
        YINNI_HIGH_FAITH_DURATION = BUILDER
            .comment("隐匿高信念隐身持续时间（tick）")
            .defineInRange("high_faith_duration", 60, 20, 200);
            
        YINNI_GROUP_RANGE = BUILDER
            .comment("隐匿群体隐身范围（方块）")
            .defineInRange("group_range", 5.0, 1.0, 16.0);
            
        YINNI_MAX_TARGETS = BUILDER
            .comment("隐匿最大目标数量")
            .defineInRange("max_targets", 3, 1, 10);
            
        BUILDER.pop();

        // 夺心魄回响配置
        BUILDER.push("夺心魄回响");
        
        DUOXINPO_RANGE = BUILDER
            .comment("夺心魄影响范围（方块）")
            .defineInRange("range", 10.0, 1.0, 32.0);
            
        DUOXINPO_CONTROL_RANGE = BUILDER
            .comment("夺心魄控制距离（方块）")
            .defineInRange("control_range", 15.0, 1.0, 32.0);
            
        DUOXINPO_CONTROL_DURATION = BUILDER
            .comment("夺心魄控制持续时间（tick）")
            .defineInRange("control_duration", 1200, 200, 12000);
            
        DUOXINPO_SANITY_DRAIN = BUILDER
            .comment("夺心魄每秒降低的理智值")
            .defineInRange("sanity_drain", 1, 0, 10);
            
        DUOXINPO_MIN_SANITY = BUILDER
            .comment("夺心魄最低理智值限制")
            .defineInRange("min_sanity", 500, 100, 2000);
            
        DUOXINPO_BELIEF_THRESHOLD = BUILDER
            .comment("夺心魄信念阈值")
            .defineInRange("belief_threshold", 6, 0, 100);
            
        DUOXINPO_FREE_COST_THRESHOLD = BUILDER
            .comment("夺心魄免费释放的理智值阈值")
            .defineInRange("free_cost_threshold", 300, 0, 1000);
            
        DUOXINPO_CONTINUOUS_SANITY_COST = BUILDER
            .comment("夺心魄每秒持续消耗的理智值")
            .defineInRange("continuous_sanity_cost", 5, 0, 100);
            
        DUOXINPO_ACTIVE_SANITY_COST = BUILDER
            .comment("夺心魄主动使用消耗的理智值")
            .defineInRange("active_sanity_cost", 100, 0, 1000);
            
        DUOXINPO_TOGGLE_SANITY_COST = BUILDER
            .comment("夺心魄开启持续效果消耗的理智值")
            .defineInRange("toggle_sanity_cost", 30, 0, 1000);
            
        BUILDER.pop();

        // 祸水回响配置
        BUILDER.push("祸水回响");
        
        HUOSHUI_BASE_RANGE = BUILDER
            .comment("祸水基础影响范围（方块）")
            .defineInRange("base_range", 10.0, 1.0, 32.0);
            
        HUOSHUI_MID_RANGE = BUILDER
            .comment("祸水中等信念影响范围（方块）")
            .defineInRange("mid_range", 15.0, 1.0, 32.0);
            
        HUOSHUI_BASE_FIRE_COUNT = BUILDER
            .comment("祸水基础火焰数量")
            .defineInRange("base_fire_count", 20, 1, 100);
            
        HUOSHUI_MID_FIRE_COUNT = BUILDER
            .comment("祸水中等信念火焰数量")
            .defineInRange("mid_fire_count", 30, 1, 100);
            
        HUOSHUI_BASE_FIRE_DURATION = BUILDER
            .comment("祸水基础火焰持续时间（tick）")
            .defineInRange("base_fire_duration", 100, 20, 600);
            
        HUOSHUI_MID_FIRE_DURATION = BUILDER
            .comment("祸水中等信念火焰持续时间（tick）")
            .defineInRange("mid_fire_duration", 150, 20, 600);
            
        HUOSHUI_BASE_SANITY_REDUCTION = BUILDER
            .comment("祸水基础理智降低量")
            .defineInRange("base_sanity_reduction", 50, 0, 200);
            
        HUOSHUI_MID_SANITY_REDUCTION = BUILDER
            .comment("祸水中等信念理智降低量")
            .defineInRange("mid_sanity_reduction", 75, 0, 200);
            
        HUOSHUI_NORMAL_COOLDOWN = BUILDER
            .comment("祸水正常冷却时间（tick，72000tick = 60分钟）")
            .defineInRange("normal_cooldown", 72000, 1200, 144000);
            
        HUOSHUI_LOW_SANITY_COOLDOWN = BUILDER
            .comment("祸水低理智冷却时间（tick，24000tick = 20分钟）")
            .defineInRange("low_sanity_cooldown", 24000, 1200, 144000);
            
        HUOSHUI_LOW_SANITY_THRESHOLD = BUILDER
            .comment("祸水低理智阈值")
            .defineInRange("low_sanity_threshold", 200, 0, 1000);
            
        HUOSHUI_MID_FAITH = BUILDER
            .comment("祸水中等信念要求")
            .defineInRange("mid_faith", 5, 0, 100);
            
        HUOSHUI_MIN_FAITH = BUILDER
            .comment("祸水最小信念要求")
            .defineInRange("min_faith", 10, 0, 100);
            
        HUOSHUI_FREE_COST_THRESHOLD = BUILDER
            .comment("祸水免费释放理智阈值")
            .defineInRange("free_cost_threshold", 300, 0, 1000);
            
        BUILDER.pop();

        // 双生花回响配置
        BUILDER.push("双生花回响");
        
        SHUANGSHENGHUA_PASSIVE_RANGE = BUILDER
            .comment("双生花被动效果范围（方块）")
            .defineInRange("passive_range", 2.0, 1.0, 10.0);
            
        SHUANGSHENGHUA_PASSIVE_SYNC_CD = BUILDER
            .comment("双生花被动同步冷却（tick）")
            .defineInRange("passive_sync_cd", 20, 1, 100);
            
        SHUANGSHENGHUA_CONTINUOUS_SANITY_COST = BUILDER
            .comment("双生花每秒持续消耗理智值")
            .defineInRange("continuous_sanity_cost", 2, 0, 20);
            
        SHUANGSHENGHUA_TOGGLE_SANITY_COST = BUILDER
            .comment("双生花开启消耗理智值")
            .defineInRange("toggle_sanity_cost", 30, 0, 1000);
            
        SHUANGSHENGHUA_ACTIVE_SANITY_COST = BUILDER
            .comment("双生花主动技能消耗理智值")
            .defineInRange("active_sanity_cost", 50, 0, 1000);
            
        SHUANGSHENGHUA_ACTIVE_DURATION = BUILDER
            .comment("双生花主动效果持续时间（tick）")
            .defineInRange("active_duration", 12000, 1200, 72000);
            
        SHUANGSHENGHUA_ACTIVE_COOLDOWN = BUILDER
            .comment("双生花主动技能冷却时间（tick）")
            .defineInRange("active_cooldown", 12000, 1200, 72000);
            
        SHUANGSHENGHUA_DAMAGE_SHARE_CD = BUILDER
            .comment("双生花伤害分摊冷却（tick）")
            .defineInRange("damage_share_cd", 10, 1, 100);
            
        SHUANGSHENGHUA_LOW_SANITY_THRESHOLD = BUILDER
            .comment("双生花低理智阈值")
            .defineInRange("low_sanity_threshold", 200, 0, 1000);
            
        SHUANGSHENGHUA_FREE_COST_THRESHOLD = BUILDER
            .comment("双生花免费释放理智阈值")
            .defineInRange("free_cost_threshold", 300, 0, 1000);
            
        BUILDER.pop();

        // 惊雷回响配置
        BUILDER.push("惊雷回响");
        
        JINGLEI_SANITY_COST = BUILDER
            .comment("惊雷回响理智消耗")
            .defineInRange("sanity_cost", 10, 0, 1000);
            
        JINGLEI_MIN_FAITH = BUILDER
            .comment("惊雷最小信念要求")
            .defineInRange("min_faith", 10, 0, 100);
            
        JINGLEI_MID_FAITH = BUILDER
            .comment("惊雷中等信念要求")
            .defineInRange("mid_faith", 5, 0, 100);
            
        JINGLEI_FREE_COST_THRESHOLD = BUILDER
            .comment("惊雷免费释放理智阈值")
            .defineInRange("free_cost_threshold", 300, 0, 1000);
            
        JINGLEI_RANGE = BUILDER
            .comment("惊雷施法距离（方块）")
            .defineInRange("range", 64.0, 8.0, 128.0);
            
        JINGLEI_BASE_COOLDOWN = BUILDER
            .comment("惊雷基础冷却时间（tick）")
            .defineInRange("base_cooldown", 200, 20, 1200);
            
        JINGLEI_BASE_DAMAGE = BUILDER
            .comment("惊雷基础伤害")
            .defineInRange("base_damage", 12.0, 1.0, 100.0);
            
        JINGLEI_DAMAGE_PER_FAITH = BUILDER
            .comment("惊雷每点信念增加的伤害")
            .defineInRange("damage_per_faith", 1.5, 0.0, 10.0);
            
        BUILDER.pop();

        // 生生不息回响配置
        BUILDER.push("生生不息回响");
        
        SHENGSHENGBUXI_REGENERATION_RANGE = BUILDER
            .comment("生生不息再生范围（方块）")
            .defineInRange("regeneration_range", 5.0, 1.0, 20.0);
            
        SHENGSHENGBUXI_MIN_SANITY_REQUIREMENT = BUILDER
            .comment("生生不息最低理智要求")
            .defineInRange("min_sanity_requirement", 200, 0, 1000);
            
        SHENGSHENGBUXI_MIN_BELIEF_REQUIREMENT = BUILDER
            .comment("生生不息最低信念要求")
            .defineInRange("min_belief_requirement", 1, 0, 100);
            
        SHENGSHENGBUXI_FREE_COST_THRESHOLD = BUILDER
            .comment("生生不息免费释放理智阈值")
            .defineInRange("free_cost_threshold", 300, 0, 1000);
            
        BUILDER.pop();

        // 不灭回响配置
        BUILDER.push("不灭回响");
        
        BUMIE_SANITY_COST = BUILDER
            .comment("不灭回响理智消耗")
            .defineInRange("sanity_cost", 500, 0, 1000);
            
        BUMIE_MID_FAITH = BUILDER
            .comment("不灭中等信念要求")
            .defineInRange("mid_faith", 5, 0, 100);
            
        BUMIE_MIN_FAITH = BUILDER
            .comment("不灭最小信念要求")
            .defineInRange("min_faith", 10, 0, 100);
            
        BUMIE_FREE_COST_THRESHOLD = BUILDER
            .comment("不灭免费释放理智阈值")
            .defineInRange("free_cost_threshold", 300, 0, 1000);
            
        BUMIE_DURATION_TICKS = BUILDER
            .comment("不灭持续时间（tick）")
            .defineInRange("duration_ticks", 6000, 1200, 72000);
            
        BUMIE_COOLDOWN_TICKS = BUILDER
            .comment("不灭冷却时间（tick）")
            .defineInRange("cooldown_ticks", 36000, 1200, 144000);
            
        BUMIE_BASE_RETALIATION_DAMAGE = BUILDER
            .comment("不灭基础反噬伤害")
            .defineInRange("base_retaliation_damage", 19.0, 1.0, 50.0);
            
        BUMIE_MID_RETALIATION_DAMAGE = BUILDER
            .comment("不灭中等信念反噬伤害")
            .defineInRange("mid_retaliation_damage", 12.0, 1.0, 50.0);
            
        BUMIE_HIGH_RETALIATION_DAMAGE = BUILDER
            .comment("不灭高等信念反噬伤害")
            .defineInRange("high_retaliation_damage", 8.0, 1.0, 50.0);
            
        BUILDER.pop();

        // 跃迁回响配置
        BUILDER.push("跃迁回响");
        
        YUEQIAN_SANITY_COST = BUILDER
            .comment("跃迁理智消耗")
            .defineInRange("sanity_cost", 50, 0, 1000);
            
        YUEQIAN_MIN_FAITH = BUILDER
            .comment("跃迁最小信念要求")
            .defineInRange("min_faith", 10, 0, 100);
            
        YUEQIAN_FREE_COST_THRESHOLD = BUILDER
            .comment("跃迁免费释放理智阈值")
            .defineInRange("free_cost_threshold", 300, 0, 1000);
            
        YUEQIAN_MAX_RANGE = BUILDER
            .comment("跃迁最大范围（方块）")
            .defineInRange("max_range", 64.0, 8.0, 128.0);
            
        YUEQIAN_COOLDOWN_TICKS = BUILDER
            .comment("跃迁冷却时间（tick）")
            .defineInRange("cooldown_ticks", 600, 100, 1200);
            
        BUILDER.pop();

        // 治愈回响配置
        BUILDER.push("治愈回响");
        
        ZHIYU_SANITY_COST = BUILDER
            .comment("治愈理智消耗")
            .defineInRange("sanity_cost", 15, 0, 1000);
            
        ZHIYU_MIN_FAITH = BUILDER
            .comment("治愈最小信念要求")
            .defineInRange("min_faith", 8, 0, 100);
            
        ZHIYU_FREE_COST_THRESHOLD = BUILDER
            .comment("治愈免费释放理智阈值")
            .defineInRange("free_cost_threshold", 200, 0, 1000);
            
        ZHIYU_CONTINUOUS_SANITY_COST = BUILDER
            .comment("治愈每秒持续消耗理智值")
            .defineInRange("continuous_sanity_cost", 1, 0, 20);
            
        ZHIYU_HEAL_RANGE = BUILDER
            .comment("治愈范围（方块）")
            .defineInRange("heal_range", 8.0, 1.0, 32.0);
            
        ZHIYU_BASE_MAX_SANITY = BUILDER
            .comment("治愈基础最大理智值")
            .defineInRange("base_max_sanity", 500, 100, 2000);
            
        ZHIYU_HIGH_MAX_SANITY = BUILDER
            .comment("治愈高信念最大理智值")
            .defineInRange("high_max_sanity", 800, 100, 2000);
            
        ZHIYU_ACTIVE_DURATION = BUILDER
            .comment("治愈活跃时间（tick）")
            .defineInRange("active_duration", 2400, 600, 12000);
            
        ZHIYU_COOLDOWN_TICKS = BUILDER
            .comment("治愈冷却时间（tick）")
            .defineInRange("cooldown_ticks", 6000, 1200, 72000);
            
        BUILDER.pop();

        // 感知类回响共同配置
        BUILDER.push("感知类回响");
        
        LING_SANITY_COST = BUILDER
            .comment("感知类回响理智消耗")
            .defineInRange("sanity_cost", 20, 0, 1000);
            
        LING_FREE_COST_THRESHOLD = BUILDER
            .comment("感知类回响免费释放理智阈值")
            .defineInRange("free_cost_threshold", 300, 0, 1000);
            
        LING_MIN_FAITH = BUILDER
            .comment("感知类回响最小信念要求")
            .defineInRange("min_faith", 10, 0, 100);
            
        LING_MID_FAITH = BUILDER
            .comment("感知类回响中等信念要求")
            .defineInRange("mid_faith", 5, 0, 100);
            
        LING_BASE_RANGE = BUILDER
            .comment("感知类回响基础范围（方块）")
            .defineInRange("base_range", 10.0, 1.0, 50.0);
            
        LING_MID_RANGE = BUILDER
            .comment("感知类回响中等信念范围（方块）")
            .defineInRange("mid_range", 20.0, 1.0, 50.0);
            
        LING_HIGH_RANGE = BUILDER
            .comment("感知类回响高等信念范围（方块）")
            .defineInRange("high_range", 30.0, 1.0, 50.0);
            
        BUILDER.pop();

        // 寒冰回响配置
        BUILDER.push("寒冰回响");
        
        HANBING_SANITY_COST = BUILDER
            .comment("寒冰回响理智消耗")
            .defineInRange("sanity_cost", 10, 0, 1000);
            
        HANBING_COOLDOWN_TICKS = BUILDER
            .comment("寒冰回响冷却时间（tick）")
            .defineInRange("cooldown_ticks", 160, 20, 1200);
            
        HANBING_MID_FAITH = BUILDER
            .comment("寒冰回响中等信念要求")
            .defineInRange("mid_faith", 5, 0, 100);
            
        HANBING_MIN_FAITH = BUILDER
            .comment("寒冰回响最小信念要求")
            .defineInRange("min_faith", 10, 0, 100);
            
        HANBING_FREE_COST_THRESHOLD = BUILDER
            .comment("寒冰回响免费释放理智阈值")
            .defineInRange("free_cost_threshold", 300, 0, 1000);
            
        // 寒冰实体伤害配置
        HANBING_ICE_DAMAGE = BUILDER
            .comment("寒冰实体基础伤害")
            .defineInRange("ice_damage", 10.7, 1.0, 50.0);
            
        HANBING_ICE_ENHANCED_DAMAGE = BUILDER
            .comment("寒冰实体增强伤害")
            .defineInRange("ice_enhanced_damage", 16.0, 1.0, 50.0);
            
        HANBING_ICE_SPLIT_DAMAGE = BUILDER
            .comment("寒冰实体分裂伤害")
            .defineInRange("ice_split_damage", 6.7, 1.0, 50.0);
            
        HANBING_ICE_ENHANCED_SPLIT_DAMAGE = BUILDER
            .comment("寒冰实体增强分裂伤害")
            .defineInRange("ice_enhanced_split_damage", 10.0, 1.0, 50.0);
            
        HANBING_ICE_SPLIT_SPEED_MULTIPLIER = BUILDER
            .comment("寒冰实体分裂速度倍率")
            .defineInRange("ice_split_speed_multiplier", 0.8, 0.1, 2.0);
            
        HANBING_ICE_MAX_SPLIT = BUILDER
            .comment("寒冰实体最大分裂次数")
            .defineInRange("ice_max_split", 2, 0, 5);
            
        BUILDER.pop();

        // 破万法回响配置
        BUILDER.push("破万法回响");
        
        BREAKALL_RANGE = BUILDER
            .comment("破万法影响范围（方块）")
            .defineInRange("range", 10.0, 1.0, 32.0);
            
        BREAKALL_DISABLE_DURATION = BUILDER
            .comment("破万法禁用时间（tick）")
            .defineInRange("disable_duration", 6000, 1200, 72000);
            
        BREAKALL_COOLDOWN_TICKS = BUILDER
            .comment("破万法冷却时间（tick）")
            .defineInRange("cooldown_ticks", 36000, 1200, 144000);
            
        BREAKALL_SANITY_COST = BUILDER
            .comment("破万法理智消耗")
            .defineInRange("sanity_cost", 200, 0, 1000);
            
        BREAKALL_FREE_COST_THRESHOLD = BUILDER
            .comment("破万法免费释放理智阈值")
            .defineInRange("free_cost_threshold", 300, 0, 1000);
            
        BUILDER.pop();

        // 招灾回响配置
        BUILDER.push("招灾回响");
        
        ZHAOZAI_RANGE = BUILDER
            .comment("招灾影响范围（方块）")
            .defineInRange("range", 10.0, 1.0, 32.0);
            
        ZHAOZAI_WITHER_DURATION = BUILDER
            .comment("招灾凋零持续时间（tick）")
            .defineInRange("wither_duration", 300, 60, 1200);
            
        ZHAOZAI_WITHER_AMPLIFIER = BUILDER
            .comment("招灾凋零等级")
            .defineInRange("wither_amplifier", 0, 0, 9);
            
        ZHAOZAI_GLOWING_DURATION = BUILDER
            .comment("招灾发光持续时间（tick）")
            .defineInRange("glowing_duration", 600, 200, 2400);
            
        ZHAOZAI_CHECK_INTERVAL = BUILDER
            .comment("招灾检查间隔（tick）")
            .defineInRange("check_interval", 200, 20, 600);
            
        ZHAOZAI_BASE_SUCCESS_RATE = BUILDER
            .comment("招灾基础成功率（%）")
            .defineInRange("base_success_rate", 5.0, 0.0, 100.0);
            
        ZHAOZAI_HIGH_SUCCESS_RATE = BUILDER
            .comment("招灾高信念成功率（%）")
            .defineInRange("high_success_rate", 8.0, 0.0, 100.0);
            
        ZHAOZAI_HIGH_FAITH_RANGE = BUILDER
            .comment("招灾高信念影响范围（方块）")
            .defineInRange("high_faith_range", 15.0, 1.0, 32.0);
            
        ZHAOZAI_SUMMON_COST = BUILDER
            .comment("招灾召唤消耗理智值")
            .defineInRange("summon_cost", 100, 0, 1000);
            
        ZHAOZAI_COOLDOWN_TICKS = BUILDER
            .comment("招灾冷却时间（tick）")
            .defineInRange("cooldown_ticks", 36000, 1200, 144000);
            
        ZHAOZAI_MIN_FAITH = BUILDER
            .comment("招灾最小信念要求")
            .defineInRange("min_faith", 10, 0, 100);
            
        ZHAOZAI_FREE_COST_THRESHOLD = BUILDER
            .comment("招灾免费释放理智阈值")
            .defineInRange("free_cost_threshold", 300, 0, 1000);
            
        BUILDER.pop();

        // 挪移回响配置
        BUILDER.push("挪移回响");
        
        NUOYI_GIVE_COST = BUILDER
            .comment("挪移给予消耗理智值")
            .defineInRange("give_cost", 20, 0, 1000);
            
        NUOYI_TAKE_COST = BUILDER
            .comment("挪移获取消耗理智值")
            .defineInRange("take_cost", 40, 0, 1000);
            
        NUOYI_COOLDOWN_TICKS = BUILDER
            .comment("挪移冷却时间（tick）")
            .defineInRange("cooldown_ticks", 600, 100, 1200);
            
        NUOYI_FREE_COST_THRESHOLD = BUILDER
            .comment("挪移免费释放理智阈值")
            .defineInRange("free_cost_threshold", 300, 0, 1000);
            
        NUOYI_MIN_FAITH = BUILDER
            .comment("挪移最小信念要求")
            .defineInRange("min_faith", 10, 0, 100);
            
        BUILDER.pop();

        // 替罪回响配置
        BUILDER.push("替罪回响");
        
        TIZUI_SANITY_COST = BUILDER
            .comment("替罪理智消耗")
            .defineInRange("sanity_cost", 50, 0, 1000);
            
        TIZUI_FREE_COST_THRESHOLD = BUILDER
            .comment("替罪免费释放理智阈值")
            .defineInRange("free_cost_threshold", 300, 0, 1000);
            
        TIZUI_MIN_FAITH = BUILDER
            .comment("替罪最小信念要求")
            .defineInRange("min_faith", 10, 0, 100);
            
        TIZUI_SELECTION_RANGE = BUILDER
            .comment("替罪选择范围（方块）")
            .defineInRange("selection_range", 16.0, 1.0, 32.0);
            
        BUILDER.pop();

        // 缩千山回响配置
        BUILDER.push("缩千山回响");
        
        SUOQIANSHAN_TOGGLE_SANITY_COST = BUILDER
            .comment("缩千山开启消耗理智值")
            .defineInRange("toggle_sanity_cost", 50, 0, 1000);
            
        SUOQIANSHAN_CONTINUOUS_SANITY_COST = BUILDER
            .comment("缩千山每秒持续消耗理智值")
            .defineInRange("continuous_sanity_cost", 1, 0, 20);
            
        SUOQIANSHAN_ACTIVE_SANITY_COST = BUILDER
            .comment("缩千山主动使用消耗理智值")
            .defineInRange("active_sanity_cost", 100, 0, 1000);
            
        SUOQIANSHAN_FREE_COST_THRESHOLD = BUILDER
            .comment("缩千山免费释放理智阈值")
            .defineInRange("free_cost_threshold", 300, 0, 1000);
            
        SUOQIANSHAN_HIGH_FAITH = BUILDER
            .comment("缩千山高信念要求")
            .defineInRange("high_faith", 10, 0, 100);
            
        SUOQIANSHAN_COOLDOWN_TICKS = BUILDER
            .comment("缩千山冷却时间（tick）")
            .defineInRange("cooldown_ticks", 1200, 200, 12000);
            
        SUOQIANSHAN_SPEED_LEVEL = BUILDER
            .comment("缩千山速度效果等级")
            .defineInRange("speed_level", 99, 0, 255);
            
        SUOQIANSHAN_JUMP_LEVEL = BUILDER
            .comment("缩千山跳跃提升等级")
            .defineInRange("jump_level", 4, 0, 10);
            
        SUOQIANSHAN_SLOW_FALLING_LEVEL = BUILDER
            .comment("缩千山缓降等级")
            .defineInRange("slow_falling_level", 0, 0, 10);
            
        BUILDER.pop();

        // 天行健回响配置
        BUILDER.push("天行健回响");
        
        TIANXINGJIAN_COOLDOWN_TICKS = BUILDER
            .comment("天行健冷却时间（tick）")
            .defineInRange("cooldown_ticks", 2400, 600, 12000);
            
        TIANXINGJIAN_EFFECT_DURATION = BUILDER
            .comment("天行健效果持续时间（tick）")
            .defineInRange("effect_duration", 2400, 600, 12000);
            
        TIANXINGJIAN_LOW_SANITY_THRESHOLD = BUILDER
            .comment("天行健低理智阈值")
            .defineInRange("low_sanity_threshold", 300, 0, 1000);
            
        TIANXINGJIAN_FREE_COST_THRESHOLD = BUILDER
            .comment("天行健免费释放理智阈值")
            .defineInRange("free_cost_threshold", 300, 0, 1000);
            
        TIANXINGJIAN_PASSIVE_DURATION = BUILDER
            .comment("天行健被动效果持续时间（tick）")
            .defineInRange("passive_duration", 60, 20, 200);
            
        TIANXINGJIAN_MID_FAITH = BUILDER
            .comment("天行健中等信念要求")
            .defineInRange("mid_faith", 5, 0, 100);
            
        TIANXINGJIAN_HIGH_FAITH = BUILDER
            .comment("天行健高等信念要求")
            .defineInRange("high_faith", 10, 0, 100);
            
        TIANXINGJIAN_BASE_HEALTH_INCREASE = BUILDER
            .comment("天行健基础生命值增加")
            .defineInRange("base_health_increase", 10, 0, 100);
            
        TIANXINGJIAN_MID_HEALTH_INCREASE = BUILDER
            .comment("天行健中等信念生命值增加")
            .defineInRange("mid_health_increase", 15, 0, 100);
            
        TIANXINGJIAN_HIGH_HEALTH_INCREASE = BUILDER
            .comment("天行健高等信念生命值增加")
            .defineInRange("high_health_increase", 20, 0, 100);
            
        BUILDER.pop();

        // 忘忧回响配置
        BUILDER.push("忘忧回响");
        
        WANGYOU_SANITY_COST = BUILDER
            .comment("忘忧理智消耗")
            .defineInRange("sanity_cost", 30, 0, 1000);
            
        WANGYOU_MIN_FAITH = BUILDER
            .comment("忘忧最小信念要求")
            .defineInRange("min_faith", 10, 0, 100);
            
        WANGYOU_FREE_COST_THRESHOLD = BUILDER
            .comment("忘忧免费释放理智阈值")
            .defineInRange("free_cost_threshold", 300, 0, 1000);
            
        WANGYOU_CONTINUOUS_SANITY_COST = BUILDER
            .comment("忘忧每秒持续消耗理智值")
            .defineInRange("continuous_sanity_cost", 1, 0, 20);
            
        WANGYOU_BASE_DAMAGE_REDUCTION = BUILDER
            .comment("忘忧基础减伤比例（%）")
            .defineInRange("base_damage_reduction", 50.0, 0.0, 100.0);
            
        WANGYOU_FAITH_DAMAGE_REDUCTION = BUILDER
            .comment("忘忧每2点信念额外减伤比例（%）")
            .defineInRange("faith_damage_reduction", 10.0, 0.0, 50.0);
            
        BUILDER.pop();

        // 原物回响配置
        BUILDER.push("原物回响");
        
        YUANWU_SANITY_COST = BUILDER
            .comment("原物理智消耗")
            .defineInRange("sanity_cost", 10, 0, 1000);
            
        YUANWU_MIN_FAITH = BUILDER
            .comment("原物最小信念要求")
            .defineInRange("min_faith", 10, 0, 100);
            
        YUANWU_FREE_COST_THRESHOLD = BUILDER
            .comment("原物免费释放理智阈值")
            .defineInRange("free_cost_threshold", 300, 0, 1000);
            
        YUANWU_COOLDOWN_TICKS = BUILDER
            .comment("原物冷却时间（tick）")
            .defineInRange("cooldown_ticks", 12000, 1200, 72000);
            
        BUILDER.pop();

        // 御神君回响配置
        BUILDER.push("御神君回响");
        
        YUSHENJUN_RANGE = BUILDER
            .comment("御神君影响范围（方块）")
            .defineInRange("range", 10.0, 1.0, 32.0);
            
        YUSHENJUN_EFFECT_DURATION = BUILDER
            .comment("御神君效果持续时间（tick）")
            .defineInRange("effect_duration", 400, 200, 1200);
            
        YUSHENJUN_SUMMON_DURATION = BUILDER
            .comment("御神君召唤持续时间（tick）")
            .defineInRange("summon_duration", 400, 200, 1200);
            
        YUSHENJUN_COOLDOWN_TICKS = BUILDER
            .comment("御神君冷却时间（tick）")
            .defineInRange("cooldown_ticks", 1200, 600, 12000);
            
        YUSHENJUN_ACTIVE_SANITY_COST = BUILDER
            .comment("御神君主动使用消耗理智值")
            .defineInRange("active_sanity_cost", 200, 0, 1000);
            
        YUSHENJUN_TOGGLE_SANITY_COST = BUILDER
            .comment("御神君开启消耗理智值")
            .defineInRange("toggle_sanity_cost", 30, 0, 1000);
            
        YUSHENJUN_CONTINUOUS_SANITY_COST = BUILDER
            .comment("御神君每秒持续消耗理智值")
            .defineInRange("continuous_sanity_cost", 1, 0, 20);
            
        BUILDER.pop();

        // 滞空回响配置
        BUILDER.push("滞空回响");
        
        ZHIKONG_TOGGLE_SANITY_COST = BUILDER
            .comment("滞空开启消耗理智值")
            .defineInRange("toggle_sanity_cost", 30, 0, 1000);
            
        ZHIKONG_CONTINUOUS_SANITY_COST = BUILDER
            .comment("滞空每秒持续消耗理智值")
            .defineInRange("continuous_sanity_cost", 1, 0, 20);
            
        ZHIKONG_FREE_COST_THRESHOLD = BUILDER
            .comment("滞空免费释放理智阈值")
            .defineInRange("free_cost_threshold", 300, 0, 1000);
            
        ZHIKONG_MIN_FAITH = BUILDER
            .comment("滞空最小信念要求")
            .defineInRange("min_faith", 10, 0, 100);
            
        ZHIKONG_EFFECT_DURATION = BUILDER
            .comment("滞空效果持续时间（tick）")
            .defineInRange("effect_duration", 10, 5, 100);
            
        ZHIKONG_EFFECT_LEVEL = BUILDER
            .comment("滞空缓降效果等级")
            .defineInRange("effect_level", 2, 0, 10);
            
        BUILDER.pop();

        // 傀儡回响配置
        BUILDER.push("傀儡回响");
        
        KUILEI_SANITY_COST = BUILDER
            .comment("傀儡理智消耗")
            .defineInRange("sanity_cost", 100, 0, 1000);
            
        KUILEI_COOLDOWN_TICKS = BUILDER
            .comment("傀儡冷却时间（tick）")
            .defineInRange("cooldown_ticks", 1200, 600, 12000);
            
        KUILEI_FREE_COST_THRESHOLD = BUILDER
            .comment("傀儡免费释放理智阈值")
            .defineInRange("free_cost_threshold", 300, 0, 1000);
            
        KUILEI_MIN_FAITH_REQUIREMENT = BUILDER
            .comment("傀儡最低信念要求")
            .defineInRange("min_faith_requirement", 10, 0, 100);
            
        KUILEI_MID_FAITH = BUILDER
            .comment("傀儡中等信念要求")
            .defineInRange("mid_faith", 5, 0, 100);
            
        KUILEI_PUPPET_SCALE = BUILDER
            .comment("傀儡大小缩放比例")
            .defineInRange("puppet_scale", 1.0, 0.1, 5.0);
            
        BUILDER.pop();

        // 乾坤回响配置
        BUILDER.push("乾坤回响");
        
        QIANKUN_TOGGLE_SANITY_COST = BUILDER
            .comment("乾坤开启消耗理智值")
            .defineInRange("toggle_sanity_cost", 0, 0, 1000);
            
        QIANKUN_CONTINUOUS_SANITY_COST = BUILDER
            .comment("乾坤每5秒持续消耗理智值")
            .defineInRange("continuous_sanity_cost", 1, 0, 20);
            
        QIANKUN_ACTIVE_SANITY_INCREASE = BUILDER
            .comment("乾坤主动增加理智值")
            .defineInRange("active_sanity_increase", 500, 100, 2000);
            
        QIANKUN_FAITH_GAIN = BUILDER
            .comment("乾坤获得信念点数")
            .defineInRange("faith_gain", 10, 1, 50);
            
        QIANKUN_SANITY_THRESHOLD = BUILDER
            .comment("乾坤理智阈值")
            .defineInRange("sanity_threshold", 200, 0, 1000);
            
        QIANKUN_FREE_COST_THRESHOLD = BUILDER
            .comment("乾坤免费释放理智阈值")
            .defineInRange("free_cost_threshold", 300, 0, 1000);
            
        QIANKUN_COOLDOWN_TICKS = BUILDER
            .comment("乾坤冷却时间（tick，144000tick = 120分钟）")
            .defineInRange("cooldown_ticks", 144000, 12000, 288000);
            
        BUILDER.pop();

        SPEC = BUILDER.build();
    }

    public static void register() {
        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, SPEC, "doomsday-echoes.toml");
    }
} 