package org.lanstard.doomsday.utils;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.saveddata.SavedData;

public class TimeSavedData extends SavedData {
    private int days;
    
    public TimeSavedData() {
        this.days = 0;
    }
    
    public TimeSavedData(CompoundTag tag) {
        this.loadFromNBT(tag);
    }
    
    private void loadFromNBT(CompoundTag tag) {
        this.days = tag.getInt("days");
    }
    
    @Override
    public CompoundTag save(CompoundTag tag) {
        tag.putInt("days", days);
        return tag;
    }
    
    public void setDays(int days) {
        this.days = days;
        this.setDirty();
    }
    
    public int getDays() { 
        return days; 
    }
}