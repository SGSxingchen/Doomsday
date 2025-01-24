package org.lanstard.doomsday.network.packet;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkEvent;
import org.lanstard.doomsday.common.echo.Echo;
import org.lanstard.doomsday.common.echo.EchoManager;
import org.lanstard.doomsday.common.echo.EchoType;

import java.util.List;
import java.util.function.Supplier;

public class ToggleContinuousEchoPacket {
    private final String echoId;

    public ToggleContinuousEchoPacket(String echoId) {
        this.echoId = echoId;
    }

    public static void encode(ToggleContinuousEchoPacket msg, FriendlyByteBuf buf) {
        buf.writeUtf(msg.echoId);
    }

    public static ToggleContinuousEchoPacket decode(FriendlyByteBuf buf) {
        return new ToggleContinuousEchoPacket(buf.readUtf());
    }

    public static void handle(ToggleContinuousEchoPacket msg, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            ServerPlayer player = ctx.get().getSender();
            if (player == null) return;

            // 获取玩家的持续性回响
            List<Echo> echoes = EchoManager.getPlayerEchoes(player);
            for (Echo echo : echoes) {
                if (echo.getId().equals(msg.echoId)) {
                    // 只处理持续性回响的开关
                    if (echo.getType() == EchoType.CONTINUOUS && EchoManager.canUseEcho(player, echo)) {
                        echo.toggleContinuous(player);
                        // 同步到客户端
                        EchoManager.syncToClient(player);
                    }
                    break;
                }
            }
        });
        ctx.get().setPacketHandled(true);
    }
} 