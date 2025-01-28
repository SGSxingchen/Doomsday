package org.lanstard.doomsday.common.echo.preset;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.crafting.CraftingRecipe;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.network.chat.Component;
import org.lanstard.doomsday.common.echo.Echo;
import org.lanstard.doomsday.common.echo.EchoPreset;
import org.lanstard.doomsday.common.sanity.SanityManager;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class YuanWuEcho extends Echo {
    private static final int SANITY_COST = 10;
    private static final int MIN_FAITH = 10;
    private static final int FREE_SANITY_THRESHOLD = 300;
    private static final int SKILL1_COOL_DOWN_TICKS = 12000; // 10分钟 = 10 * 60 * 20 ticks
    private long lastUseTime = 0;
    private static final EchoPreset PRESET = EchoPreset.YUANWU;

    public YuanWuEcho() {
        super(
            PRESET.name().toLowerCase(),
            PRESET.getDisplayName(),
            PRESET.getType(),
            PRESET.getActivationType(),
            SANITY_COST,
            0
        );
    }

    @Override
    public void onActivate(ServerPlayer player) {
        // 激活时不需要特殊处理
    }

    @Override
    public void onUpdate(ServerPlayer player) {
        // 更新时不需要特殊处理
    }

    @Override
    public void onDeactivate(ServerPlayer player) {
        // 停用时不需要特殊处理
    }

    @Override
    public boolean doCanUse(ServerPlayer player) {
        // 检查冷却时间
        long currentTime = player.level().getGameTime();
        int cooldown = SanityManager.getFaith(player) >= 5 ? SKILL1_COOL_DOWN_TICKS / 10 : SKILL1_COOL_DOWN_TICKS;
        
        if (currentTime - lastUseTime < cooldown) {
            int remainingSeconds = (int)((cooldown - (currentTime - lastUseTime)) / 20);
            player.sendSystemMessage(Component.literal("§c[十日终焉] §f...原物之力尚需" + remainingSeconds + "秒恢复..."));
            return false;
        }
        
        // 检查信仰和理智
        int faith = SanityManager.getFaith(player);
        int sanity = SanityManager.getSanity(player);
        
        // 当信仰大于等于10且理智小于300时，不消耗理智
        if (faith >= MIN_FAITH && sanity < FREE_SANITY_THRESHOLD) {
            return true;
        }
        
        // 其他情况需要消耗理智
        if (sanity < SANITY_COST) {
            player.sendSystemMessage(Component.literal("§b[十日终焉] §f...理智不足，无法使用原物回响"));
            return false;
        }
        
        return true;
    }

    @Override
    protected void doUse(ServerPlayer player) {
        // 检查是否需要消耗理智值
        int currentSanity = SanityManager.getSanity(player);
        int faith = SanityManager.getFaith(player);
        boolean freeCast = faith >= MIN_FAITH && currentSanity < FREE_SANITY_THRESHOLD;
        
        // 只有在不满足免费释放条件时才消耗理智
        if (!freeCast) {
            SanityManager.modifySanity(player, -SANITY_COST);
        }

        if(player.isShiftKeyDown()){
            ItemStack result = new ItemStack(Items.STONE, 1);
            if(!player.getInventory().add(result.copy())) {
                player.sendSystemMessage(Component.literal("§c[十日终焉] §f...物品栏已满，无法获得物品..."));
                return;
            }
            player.sendSystemMessage(Component.literal("§b[十日终焉] §f...原物之法显现，获得了 ")
                    .append(result.getHoverName())
                    .append(Component.literal(" ×" + result.getCount())));
        }
        else{
            // 获取所有包含石头或圆石的配方
            List<Recipe<?>> allRecipes = new ArrayList<>(player.level().getRecipeManager().getAllRecipesFor(RecipeType.CRAFTING));
            List<Recipe<?>> stoneRecipes = new ArrayList<>();

            for (Recipe<?> recipe : allRecipes) {
                if (recipe instanceof CraftingRecipe) {
                    for (Ingredient ingredient : recipe.getIngredients()) {
                        for (ItemStack stack : ingredient.getItems()) {
                            if (stack.getItem() == Items.STONE || stack.getItem() == Items.COBBLESTONE) {
                                stoneRecipes.add(recipe);
                                break;
                            }
                        }
                    }
                }
            }

            // 随机选择一个配方并给予玩家
            if (!stoneRecipes.isEmpty()) {
                Recipe<?> selectedRecipe = stoneRecipes.get(new Random().nextInt(stoneRecipes.size()));
                ItemStack result = selectedRecipe.getResultItem(player.level().registryAccess());
                if(!player.getInventory().add(result.copy())) {
                    player.sendSystemMessage(Component.literal("§c[十日终焉] §f...物品栏已满，无法获得物品..."));
                    return;
                }
                player.sendSystemMessage(Component.literal("§b[十日终焉] §f...原物之法显现，获得了 ")
                        .append(result.getHoverName())
                        .append(Component.literal(" ×" + result.getCount())));
            }
        }

        // 更新使用时间
        lastUseTime = player.level().getGameTime();
        updateState(player);
        notifyEchoClocks(player);
    }

    @Override
    public void toggleContinuous(ServerPlayer player) {
        player.sendSystemMessage(Component.literal("§b[十日终焉] §f...此法不可持续施展..."));
    }

    @Override
    public CompoundTag toNBT() {
        CompoundTag tag = super.toNBT();
        tag.putLong("lastUseTime", lastUseTime);
        return tag;
    }

    public static YuanWuEcho fromNBT(CompoundTag tag) {
        YuanWuEcho echo = new YuanWuEcho();
        echo.setActive(tag.getBoolean("isActive"));
        echo.lastUseTime = tag.getLong("lastUseTime");
        return echo;
    }
} 