package org.lanstard.doomsday.echo;

import net.minecraft.server.level.ServerPlayer;

public class BasicEcho extends Echo {
    public BasicEcho(String id, String name, EchoType type, ActivationType activationType, int sanityConsumption, int continuousSanityConsumption) {
        super(id, name, type, activationType, sanityConsumption, continuousSanityConsumption);
    }
    
    public BasicEcho(String id, String name, EchoType type, ActivationType activationType, int sanityConsumption) {
        super(id, name, type, activationType, sanityConsumption, 0);
    }
    @Override
    public void toggleContinuous(ServerPlayer player) {}
    
    @Override
    public void onActivate(ServerPlayer player) {}
    
    @Override
    public void onUpdate(ServerPlayer player) {}
    
    @Override
    public void onDeactivate(ServerPlayer player) {}
    
    @Override
    protected boolean doCanUse(ServerPlayer player) {
        return true;
    }
    
    @Override
    protected void doUse(ServerPlayer player) {}
} 