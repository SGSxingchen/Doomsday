package org.lanstard.doomsday.server.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.*;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.PacketDistributor;
import net.minecraftforge.server.command.EnumArgument;
import org.lanstard.doomsday.Doomsday;
import org.lanstard.doomsday.client.renderer.world3DText.World3DTextPreset;
import org.lanstard.doomsday.network.NetworkManager;
import org.lanstard.doomsday.network.packet.SpawnWorld3DTextPacket;

import java.util.Collection;

@Mod.EventBusSubscriber(modid = Doomsday.MODID)
public class World3DTextCommand {
    
    @SubscribeEvent
    public static void onRegisterCommands(RegisterCommandsEvent event) {
        CommandDispatcher<CommandSourceStack> dispatcher = event.getDispatcher();
        
        LiteralArgumentBuilder<CommandSourceStack> command = Commands.literal("world3dtext")
            .requires(source -> source.hasPermission(2))
            .then(Commands.literal("spawn_custom")
                .then(Commands.argument("targets", EntityArgument.players())
                .then(Commands.argument("text", StringArgumentType.string())
                .then(Commands.argument("color", IntegerArgumentType.integer(0, 0xFFFFFF))
                .then(Commands.argument("scale", FloatArgumentType.floatArg(0.1f, 100.0f))
                .then(Commands.argument("alpha", FloatArgumentType.floatArg(0, 1.0f))
                .then(Commands.argument("glowing", BoolArgumentType.bool())
                .then(Commands.argument("duration", IntegerArgumentType.integer(100, 100000))
                .then(Commands.argument("fadeInTime", FloatArgumentType.floatArg(0, 5000))
                .then(Commands.argument("fadeOutTime", FloatArgumentType.floatArg(0, 5000))
                .then(Commands.argument("facingPlayer", BoolArgumentType.bool())
                .then(Commands.argument("radius", DoubleArgumentType.doubleArg(0.1, 50.0))
                .then(Commands.argument("rotationX", FloatArgumentType.floatArg(-180f, 180f))
                .then(Commands.argument("rotationY", FloatArgumentType.floatArg(-180f, 180f))
                .then(Commands.argument("rotationZ", FloatArgumentType.floatArg(-180f, 180f))
                .executes(context -> spawnCustomWorld3DText(
                    context.getSource(),
                    EntityArgument.getPlayers(context, "targets"),
                    StringArgumentType.getString(context, "text"),
                    IntegerArgumentType.getInteger(context, "color"),
                    FloatArgumentType.getFloat(context, "scale"),
                    FloatArgumentType.getFloat(context, "alpha"),
                    BoolArgumentType.getBool(context, "glowing"),
                    IntegerArgumentType.getInteger(context, "duration"),
                    FloatArgumentType.getFloat(context, "fadeInTime"),
                    FloatArgumentType.getFloat(context, "fadeOutTime"),
                    BoolArgumentType.getBool(context, "facingPlayer"),
                    DoubleArgumentType.getDouble(context, "radius"),
                    FloatArgumentType.getFloat(context, "rotationX"),
                    FloatArgumentType.getFloat(context, "rotationY"),
                    FloatArgumentType.getFloat(context, "rotationZ")
                )))))))))))))))))
            .then(Commands.literal("spawn")
                .then(Commands.argument("targets", EntityArgument.players())
                .then(Commands.argument("preset", EnumArgument.enumArgument(World3DTextPreset.class))
                .then(Commands.argument("radius", DoubleArgumentType.doubleArg(0.1, 50.0))
                .then(Commands.argument("text", StringArgumentType.greedyString())
                .executes(context -> spawnWorld3DText(
                    context.getSource(),
                    EntityArgument.getPlayers(context, "targets"),
                    StringArgumentType.getString(context, "text"),
                    context.getArgument("preset", World3DTextPreset.class),
                    DoubleArgumentType.getDouble(context, "radius")
                )))))))
            .then(Commands.literal("clear")
                .then(Commands.argument("targets", EntityArgument.players())
                .executes(context -> clearWorld3DTexts(
                    context.getSource(),
                    EntityArgument.getPlayers(context, "targets")
                ))))
            .then(Commands.literal("list")
                .executes(context -> listPresets(context.getSource())));
        
        dispatcher.register(command);
    }
    
    private static int spawnCustomWorld3DText(
            CommandSourceStack source,
            Collection<ServerPlayer> targets,
            String text,
            int color,
            float scale,
            float alpha,
            boolean glowing,
            int duration,
            float fadeInTime,
            float fadeOutTime,
            boolean facingPlayer,
            double radius,
            float rotationX,
            float rotationY,
            float rotationZ) {
            
        for (ServerPlayer target : targets) {
            Vec3 pos = target.position();
            NetworkManager.getChannel().send(
                PacketDistributor.PLAYER.with(() -> target),
                new SpawnWorld3DTextPacket(
                    text, pos, radius, target.getUUID(),
                    color, scale, alpha, glowing,
                    duration, fadeInTime, fadeOutTime,
                    facingPlayer, rotationX, rotationY, rotationZ
                )
            );
        }
        
        source.sendSuccess(() -> Component.literal(
            String.format("已为 %d 个玩家生成自定义3D文本", targets.size())
        ), true);
        
        return targets.size();
    }
    
    private static int spawnWorld3DText(CommandSourceStack source, Collection<ServerPlayer> targets,
                                      String text, World3DTextPreset preset, double radius) {
        for (ServerPlayer target : targets) {
            Vec3 pos = target.position();
            NetworkManager.getChannel().sendTo(
                new SpawnWorld3DTextPacket(text, preset, radius, pos, target.getUUID()),
                target.connection.connection,
                NetworkDirection.PLAY_TO_CLIENT
            );
        }
        source.sendSuccess(() -> Component.literal("已为 " + targets.size() + " 个玩家生成3D文本"), true);
        return targets.size();
    }
    
    private static int clearWorld3DTexts(CommandSourceStack source, Collection<ServerPlayer> targets) {
        for (ServerPlayer target : targets) {
            NetworkManager.getChannel().sendTo(
                new SpawnWorld3DTextPacket(target.getUUID(), true),
                target.connection.connection,
                NetworkDirection.PLAY_TO_CLIENT
            );
        }
        source.sendSuccess(() -> Component.literal("已清除 " + targets.size() + " 个玩家的3D文本"), true);
        return targets.size();
    }
    
    private static int listPresets(CommandSourceStack source) {
        StringBuilder message = new StringBuilder("可用的3D文本预设：\n");
        for (World3DTextPreset preset : World3DTextPreset.values()) {
            message.append("- ").append(preset.name()).append("\n");
        }
        source.sendSuccess(() -> Component.literal(message.toString()), false);
        return World3DTextPreset.values().length;
    }
}