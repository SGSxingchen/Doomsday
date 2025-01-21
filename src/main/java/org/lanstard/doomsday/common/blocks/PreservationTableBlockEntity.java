package org.lanstard.doomsday.common.blocks;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.items.ItemStackHandler;
import org.lanstard.doomsday.common.blocks.entity.ModBlockEntities;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.Container;

public class PreservationTableBlockEntity extends BlockEntity implements Container {
    private final ItemStackHandler itemHandler = new ItemStackHandler(2) {
        @Override
        protected void onContentsChanged(int slot) {
            setChanged();
            if (!level.isClientSide()) {
                level.sendBlockUpdated(getBlockPos(), getBlockState(), getBlockState(), 3);
            }
        }
    };

    private FluidStack fluidStack = FluidStack.EMPTY;

    public PreservationTableBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.PRESERVATION_TABLE.get(), pos, state);
    }

    public ItemStack getItem(int slot) {
        return itemHandler.getStackInSlot(slot);
    }

    public FluidStack getFluidStack() {
        return fluidStack;
    }

    public boolean addItem(ItemStack stack, Player player) {
        if (itemHandler.getStackInSlot(0).isEmpty()) {
            ItemStack remaining = itemHandler.insertItem(0, stack.copy(), false);
            if (remaining.isEmpty()) {
                stack.shrink(1);
                return true;
            }
        }
        return false;
    }

    public boolean addFluid(FluidStack stack) {
        if (fluidStack.isEmpty()) {
            fluidStack = stack.copy();
            setChanged();
            return true;
        }
        return false;
    }

    public ItemStack removeItem(Player player) {
        ItemStack stack = itemHandler.extractItem(0, 64, false);
        if (!stack.isEmpty()) {
            return stack;
        }
        stack = itemHandler.extractItem(1, 64, false);
        return stack;
    }

    public FluidStack removeFluid(int amount) {
        if (!fluidStack.isEmpty() && fluidStack.getAmount() >= amount) {
            FluidStack extracted = new FluidStack(fluidStack.getFluid(), amount);
            fluidStack.shrink(amount);
            if (fluidStack.getAmount() <= 0) {
                fluidStack = FluidStack.EMPTY;
            }
            setChanged();
            return extracted;
        }
        return FluidStack.EMPTY;
    }

    @Override
    protected void saveAdditional(CompoundTag tag) {
        tag.put("inventory", itemHandler.serializeNBT());
        if (!fluidStack.isEmpty()) {
            tag.put("fluid", fluidStack.writeToNBT(new CompoundTag()));
        }
        super.saveAdditional(tag);
    }

    @Override
    public void load(CompoundTag tag) {
        super.load(tag);
        itemHandler.deserializeNBT(tag.getCompound("inventory"));
        if (tag.contains("fluid")) {
            fluidStack = FluidStack.loadFluidStackFromNBT(tag.getCompound("fluid"));
        }
    }

    @Override
    public int getContainerSize() {
        return 2;
    }

    @Override
    public boolean isEmpty() {
        for (int i = 0; i < itemHandler.getSlots(); i++) {
            if (!itemHandler.getStackInSlot(i).isEmpty()) return false;
        }
        return true;
    }

    @Override
    public ItemStack removeItem(int slot, int amount) {
        return itemHandler.extractItem(slot, amount, false);
    }

    @Override
    public ItemStack removeItemNoUpdate(int slot) {
        return itemHandler.extractItem(slot, 64, false);
    }

    @Override
    public void setItem(int slot, ItemStack stack) {
        itemHandler.setStackInSlot(slot, stack);
    }

    @Override
    public boolean stillValid(Player player) {
        return true;
    }

    @Override
    public void clearContent() {
        for (int i = 0; i < itemHandler.getSlots(); i++) {
            itemHandler.setStackInSlot(i, ItemStack.EMPTY);
        }
    }
} 