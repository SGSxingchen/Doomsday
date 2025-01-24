package org.lanstard.doomsday.network;

import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.simple.SimpleChannel;
import org.lanstard.doomsday.Doomsday;
import net.minecraftforge.network.NetworkDirection;
import org.lanstard.doomsday.client.manage.ClientTimeManager;
import org.lanstard.doomsday.client.ClientPermissionManager;
import org.lanstard.doomsday.client.animation.AnimationManage;
import org.lanstard.doomsday.common.echo.EchoUpdatePacket;
import org.lanstard.doomsday.network.packet.OpPermissionPacket;
import org.lanstard.doomsday.network.packet.PlayAnimationPacket;
import org.lanstard.doomsday.network.packet.SpawnScreenTextPacket;
import org.lanstard.doomsday.network.packet.SpawnWorld3DTextPacket;
import org.lanstard.doomsday.network.packet.DisplaySettingsPacket;
import org.lanstard.doomsday.client.data.ClientDisplayData;
import org.lanstard.doomsday.network.packet.TimeUpdatePacket;
import org.lanstard.doomsday.common.sanity.SanityUpdatePacket;
import org.lanstard.doomsday.network.packet.OpenEchoScreenPacket;
import org.lanstard.doomsday.network.packet.UseEchoPacket;
import org.lanstard.doomsday.network.packet.ToggleContinuousEchoPacket;
import org.lanstard.doomsday.network.packet.DuoXinPoStatusPacket;

public class NetworkManager {
    
    private static SimpleChannel INSTANCE;
    private static int packetId = 0;
    
    private static final String PROTOCOL_VERSION = "1";
    
    private static int id() {
        return packetId++;
    }
    
    public static void register() {
        SimpleChannel net = NetworkRegistry.ChannelBuilder
            .named(new ResourceLocation(Doomsday.MODID, "messages"))
            .networkProtocolVersion(() -> PROTOCOL_VERSION)
            .clientAcceptedVersions(version -> version.equals(PROTOCOL_VERSION))
            .serverAcceptedVersions(version -> version.equals(PROTOCOL_VERSION))
            .simpleChannel();
            
        INSTANCE = net;

        registerPackets(net);
    }

    private static void registerPackets(SimpleChannel net) {
        net.messageBuilder(UseEchoPacket.class, id())
            .encoder(UseEchoPacket::encode)
            .decoder(UseEchoPacket::decode)
            .consumerMainThread(UseEchoPacket::handle)
            .add();

        net.messageBuilder(OpPermissionPacket.class, id())
                .decoder(OpPermissionPacket::decode)
                .encoder(OpPermissionPacket::encode)
                .consumerMainThread((msg, ctx) -> {
                    if (ctx.get().getDirection() == NetworkDirection.PLAY_TO_SERVER) {
                        OpPermissionPacket.handleOpPermissionCheck(msg, ctx);
                    }
                    else if (ctx.get().getDirection() == NetworkDirection.PLAY_TO_CLIENT) {
                        ClientPermissionManager.handleOpPermissionResponse(msg);
                        ctx.get().setPacketHandled(true);
                    }
                })
                .add();

        net.messageBuilder(PlayAnimationPacket.class, id())
                .decoder(PlayAnimationPacket::decode)
                .encoder(PlayAnimationPacket::encode)
                .consumerMainThread((msg, ctx) -> {
                    if (ctx.get().getDirection() == NetworkDirection.PLAY_TO_CLIENT) {
                        AnimationManage.handlePlayAnimation(msg,ctx);
                        ctx.get().setPacketHandled(true);
                    }
                })
                .add();


        net.messageBuilder(DisplaySettingsPacket.class, id())
            .decoder(DisplaySettingsPacket::decode)
            .encoder(DisplaySettingsPacket::encode)
            .consumerMainThread((msg, ctx) -> {
                if (ctx.get().getDirection() == NetworkDirection.PLAY_TO_CLIENT) {
                    ClientDisplayData.updateSettings(
                        msg.getShowHealth(),
                        msg.isOp()
                    );
                    ctx.get().setPacketHandled(true);
                }
            })
            .add();

        net.messageBuilder(TimeUpdatePacket.class, id())
            .decoder(TimeUpdatePacket::decode)
            .encoder(TimeUpdatePacket::encode)
            .consumerMainThread((msg, ctx) -> {
                if (ctx.get().getDirection() == NetworkDirection.PLAY_TO_CLIENT) {
                    ClientTimeManager.handleTimeUpdate(msg.getDays(), msg.getWorldTime());
                    ctx.get().setPacketHandled(true);
                }
            })
            .add();

        net.messageBuilder(SpawnScreenTextPacket.class, id())
            .encoder(SpawnScreenTextPacket::encode)
            .decoder(SpawnScreenTextPacket::decode)
            .consumerMainThread(SpawnScreenTextPacket::handle)
            .add();

        net.messageBuilder(SpawnWorld3DTextPacket.class, id())
            .encoder(SpawnWorld3DTextPacket::encode)
            .decoder(SpawnWorld3DTextPacket::decode)
            .consumerMainThread(SpawnWorld3DTextPacket::handle)
            .add();

        net.messageBuilder(EchoUpdatePacket.class, id())
            .encoder(EchoUpdatePacket::encode)
            .decoder(EchoUpdatePacket::decode)
            .consumerMainThread(EchoUpdatePacket::handle)
            .add();

        net.messageBuilder(SanityUpdatePacket.class, id())
            .encoder(SanityUpdatePacket::encode)
            .decoder(SanityUpdatePacket::decode)
            .consumerMainThread(SanityUpdatePacket::handle)
            .add();

        // net.messageBuilder(OpenEchoScreenPacket.class, id())
        //     .encoder(OpenEchoScreenPacket::encode)
        //     .decoder(OpenEchoScreenPacket::decode)
        //     .consumerMainThread((msg, ctx) -> {
        //         if (ctx.get().getDirection() == NetworkDirection.PLAY_TO_CLIENT) {
        //             OpenEchoScreenPacket.handle(msg, ctx);
        //             ctx.get().setPacketHandled(true);
        //         }
        //     })
        //     .add();

        net.messageBuilder(ToggleContinuousEchoPacket.class, id())
            .encoder(ToggleContinuousEchoPacket::encode)
            .decoder(ToggleContinuousEchoPacket::decode)
            .consumerMainThread(ToggleContinuousEchoPacket::handle)
            .add();

        net.messageBuilder(DuoXinPoStatusPacket.class, id())
            .encoder(DuoXinPoStatusPacket::encode)
            .decoder(DuoXinPoStatusPacket::decode)
            .consumerMainThread(DuoXinPoStatusPacket::handle)
            .add();
    }
    
    public static void sendToServer(Object packet) {
        if (INSTANCE != null) {
            INSTANCE.sendToServer(packet);
        }
    }
    
    public static SimpleChannel getChannel() {
        return INSTANCE;
    }
} 