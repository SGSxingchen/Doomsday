package org.lanstard.doomsday.network.packet;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;
import org.lanstard.doomsday.client.manage.DuoXinPoStatusManager;

import java.util.function.Supplier;

public class DuoXinPoStatusPacket {
    private final String type;      // "controller", "controlled", "clear"
    private final String name;      // 目标/控制者名称
    private final long remainingTime; // 剩余时间（秒）
    
    public DuoXinPoStatusPacket(String type, String name, long remainingTime) {
        this.type = type;
        this.name = name;
        this.remainingTime = remainingTime;
    }
    
    public static void encode(DuoXinPoStatusPacket msg, FriendlyByteBuf buf) {
        buf.writeUtf(msg.type);
        buf.writeUtf(msg.name);
        buf.writeLong(msg.remainingTime);
    }
    
    public static DuoXinPoStatusPacket decode(FriendlyByteBuf buf) {
        return new DuoXinPoStatusPacket(
            buf.readUtf(),
            buf.readUtf(),
            buf.readLong()
        );
    }
    
    public static void handle(DuoXinPoStatusPacket msg, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            switch (msg.type) {
                case "controller":
                    DuoXinPoStatusManager.showControllerStatus(msg.name, msg.remainingTime);
                    break;
                case "controlled":
                    DuoXinPoStatusManager.showControlledStatus(msg.name, msg.remainingTime);
                    break;
                case "clear":
                    DuoXinPoStatusManager.clearStatus();
                    break;
            }
        });
        ctx.get().setPacketHandled(true);
    }
} 