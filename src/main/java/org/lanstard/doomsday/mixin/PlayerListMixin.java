package org.lanstard.doomsday.mixin;

import net.minecraft.network.chat.Component;
import net.minecraft.server.players.PlayerList;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

//@Mixin(PlayerList.class)
public class PlayerListMixin {
//    @Redirect(method = {"placeNewPlayer", "remove"}, at = @At(value = "INVOKE",
//        target = "Lnet/minecraft/server/players/PlayerList;broadcastSystemToAllPlayers(Lnet/minecraft/network/chat/Component;)V"))
//    private void cancelJoinLeaveMessage(PlayerList instance, Component component) {
//        // 不执行任何操作，从而取消消息广播
//    }
} 