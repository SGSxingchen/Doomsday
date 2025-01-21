package org.lanstard.doomsday.network.packet;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkEvent;
import org.lanstard.doomsday.echo.Echo;
import org.lanstard.doomsday.echo.EchoManager;
import org.lanstard.doomsday.echo.EchoType;

import java.util.List;
import java.util.function.Supplier;

public class UseEchoPacket {
    private final String echoId;
    
    public UseEchoPacket(String echoId) {
        this.echoId = echoId;
    }
    
    public static void encode(UseEchoPacket msg, FriendlyByteBuf buf) {
        buf.writeUtf(msg.echoId);
    }
    
    public static UseEchoPacket decode(FriendlyByteBuf buf) {
        return new UseEchoPacket(buf.readUtf());
    }
    
    public static void handle(UseEchoPacket msg, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            ServerPlayer player = ctx.get().getSender();
            if (player != null) {
                List<Echo> echoes = EchoManager.getPlayerEchoes(player);
                for (Echo echo : echoes) {
                    if (echo.getId().equals(msg.echoId)) {
                        if (EchoManager.canUseEcho(player, echo)) {
                            echo.use(player);
                        }
                        break;
                    }
                }
            }
        });
        ctx.get().setPacketHandled(true);
    }
} 