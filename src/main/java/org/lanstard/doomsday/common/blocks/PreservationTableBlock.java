package org.lanstard.doomsday.common.blocks;

import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BucketItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraftforge.fluids.FluidStack;
import org.jetbrains.annotations.Nullable;
import java.util.Optional;
import org.lanstard.doomsday.common.recipe.PreservationRecipe;
import org.lanstard.doomsday.common.fluids.PreservativeFluid;
import org.lanstard.doomsday.common.recipe.ModRecipes;

public class PreservationTableBlock extends Block implements EntityBlock {
    public PreservationTableBlock(Properties properties) {
        super(properties);
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new PreservationTableBlockEntity(pos, state);
    }

    @Override
    public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player,
                               InteractionHand hand, BlockHitResult hit) {
        if (level.isClientSide) return InteractionResult.SUCCESS;

        BlockEntity be = level.getBlockEntity(pos);
        if (!(be instanceof PreservationTableBlockEntity table)) return InteractionResult.PASS;

        ItemStack heldItem = player.getItemInHand(hand);
        
        if (heldItem.getItem() instanceof BucketItem) {
            return handleFluid(table, player, heldItem);
        } else if (!heldItem.isEmpty()) {
            if (table.addItem(heldItem, player)) {
                // 尝试合成
                tryProcessRecipe(level, table);
                return InteractionResult.SUCCESS;
            }
        } else {
            ItemStack extracted = table.removeItem(player);
            if (!extracted.isEmpty()) {
                if (!player.getInventory().add(extracted)) {
                    player.drop(extracted, false);
                }
                return InteractionResult.SUCCESS;
            }
        }
        
        return InteractionResult.PASS;
    }

    private void tryProcessRecipe(Level level, PreservationTableBlockEntity table) {
        Optional<PreservationRecipe> recipe = level.getRecipeManager()
            .getRecipeFor(ModRecipes.PRESERVATION.get(), table, level);
            
        recipe.ifPresent(r -> r.craft(table));
    }

    private InteractionResult handleFluid(PreservationTableBlockEntity table, Player player, ItemStack bucket) {
        if (bucket.getItem() instanceof BucketItem bucketItem) {
            if (table.getFluidStack().isEmpty()) {
                // 添加流体
                if (bucketItem.getFluid() == PreservativeFluid.getFluid().get()) {
                    table.addFluid(new FluidStack(bucketItem.getFluid(), 1000));
                    if (!player.isCreative()) {
                        player.setItemInHand(InteractionHand.MAIN_HAND, new ItemStack(Items.BUCKET));
                    }
                    return InteractionResult.SUCCESS;
                }
            } else if (bucket.getItem() == Items.BUCKET) {
                // 取出流体
                FluidStack extracted = table.removeFluid(1000);
                if (!extracted.isEmpty()) {
                    ItemStack filledBucket = new ItemStack(PreservativeFluid.getFluid().get().getBucket());
                    if (!player.isCreative()) {
                        player.setItemInHand(InteractionHand.MAIN_HAND, filledBucket);
                    }
                    return InteractionResult.SUCCESS;
                }
            }
        }
        return InteractionResult.PASS;
    }
}