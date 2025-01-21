package org.lanstard.doomsday.server.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.lanstard.doomsday.Doomsday;
import org.lanstard.doomsday.server.manage.TimeManager;

@Mod.EventBusSubscriber(modid = Doomsday.MODID)
public class TimeCommand {
    @SubscribeEvent
    public static void onRegisterCommands(RegisterCommandsEvent event) {
        CommandDispatcher<CommandSourceStack> dispatcher = event.getDispatcher();
        dispatcher.register(Commands.literal("day")
                .requires(source -> source.hasPermission(2))
                .then(Commands.literal("set")
                        .then(Commands.argument("days", IntegerArgumentType.integer(0, 0xFFFFFF))
                                .executes(context -> {
                                    int days = IntegerArgumentType.getInteger(context, "days");
                                    TimeManager.setDays(days);
                                    context.getSource().sendSuccess(() ->
                                            Component.literal("游戏天数已设置为: " + TimeManager.getTimeString()), true);
                                    return 1;
                                }))));
    }
} 