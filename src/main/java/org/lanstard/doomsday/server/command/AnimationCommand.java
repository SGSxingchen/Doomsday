package org.lanstard.doomsday.server.command;

import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.network.NetworkDirection;
import org.lanstard.doomsday.Doomsday;
import org.lanstard.doomsday.network.NetworkManager;
import org.lanstard.doomsday.network.packet.PlayAnimationPacket;

@Mod.EventBusSubscriber(modid = Doomsday.MODID)
public class AnimationCommand {
    
    @SubscribeEvent
    public static void onRegisterCommands(RegisterCommandsEvent event) {
        CommandDispatcher<CommandSourceStack> dispatcher = event.getDispatcher();
        
        dispatcher.register(Commands.literal("testAnim")
            .requires(source -> source.hasPermission(0)) // 设置命令权限级别
            .executes(context -> {
                ServerPlayer player = context.getSource().getPlayer();
                if (player != null) {
                    NetworkManager.getChannel().sendTo(
                        new PlayAnimationPacket("test_animation"),
                            player.connection.connection, NetworkDirection.PLAY_TO_CLIENT
                    );
                }
                return 1;
            }));
        dispatcher.register(Commands.literal("castHead")
                .requires(source -> source.hasPermission(0)) // 设置命令权限级别
                .executes(context -> {
                    ServerPlayer player = context.getSource().getPlayer();
                    if (player != null) {
                        NetworkManager.getChannel().sendTo(
                                new PlayAnimationPacket("cast_head"),
                                player.connection.connection, NetworkDirection.PLAY_TO_CLIENT
                        );
                    }
                    return 1;
                }));
    }
} 