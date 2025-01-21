package org.lanstard.doomsday.network.packet;

import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.network.NetworkEvent;
import org.lanstard.doomsday.client.gui.screen.EchoStatusScreen;

import java.util.function.Supplier;

public class OpenEchoScreenPacket {
    public OpenEchoScreenPacket() {}
    
    public static void encode(OpenEchoScreenPacket msg, FriendlyByteBuf buf) {}
    
    public static OpenEchoScreenPacket decode(FriendlyByteBuf buf) {
        return new OpenEchoScreenPacket();
    }
    
    public static void handle(OpenEchoScreenPacket msg, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> 
            DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> 
                Minecraft.getInstance().setScreen(new EchoStatusScreen())
            )
        );
        ctx.get().setPacketHandled(true);
    }
} 