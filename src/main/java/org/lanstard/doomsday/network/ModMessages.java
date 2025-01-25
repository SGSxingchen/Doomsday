package org.lanstard.doomsday.network;

import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.simple.SimpleChannel;
import org.lanstard.doomsday.Doomsday;
import org.lanstard.doomsday.network.packet.ClientInfoPacket;

public class ModMessages {
    private static SimpleChannel INSTANCE;
    private static int packetId = 0;
    private static int id() {
        return packetId++;
    }

    public static void register() {
        SimpleChannel net = NetworkRegistry.ChannelBuilder
            .named(new ResourceLocation(Doomsday.MODID, "mod_messages"))
            .networkProtocolVersion(() -> "1.0")
            .clientAcceptedVersions(s -> true)
            .serverAcceptedVersions(s -> true)
            .simpleChannel();

        INSTANCE = net;

        // 注册客户端信息包
        net.messageBuilder(ClientInfoPacket.class, id(), NetworkDirection.PLAY_TO_SERVER)
            .decoder(ClientInfoPacket::decode)
            .encoder(ClientInfoPacket::encode)
            .consumerMainThread(ClientInfoPacket::handle)
            .add();
    }

    public static <MSG> void sendToServer(MSG message) {
        INSTANCE.sendToServer(message);
    }
} 