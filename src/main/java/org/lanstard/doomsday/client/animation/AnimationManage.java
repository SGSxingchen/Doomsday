package org.lanstard.doomsday.client.animation;

import dev.kosmx.playerAnim.api.layered.IAnimation;
import dev.kosmx.playerAnim.api.layered.KeyframeAnimationPlayer;
import dev.kosmx.playerAnim.api.layered.ModifierLayer;
import dev.kosmx.playerAnim.minecraftApi.PlayerAnimationAccess;
import dev.kosmx.playerAnim.minecraftApi.PlayerAnimationFactory;
import dev.kosmx.playerAnim.minecraftApi.PlayerAnimationRegistry;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.network.NetworkEvent;
import org.lanstard.doomsday.Doomsday;
import org.lanstard.doomsday.network.packet.PlayAnimationPacket;

import java.util.function.Supplier;
@Mod.EventBusSubscriber(modid = Doomsday.MODID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
@OnlyIn(Dist.CLIENT)
public class AnimationManage {
    @SubscribeEvent
    public static void onClientSetup(FMLClientSetupEvent event) {
        PlayerAnimationFactory.ANIMATION_DATA_FACTORY.registerFactory(
            new ResourceLocation(Doomsday.MODID, "animation"),
            42,
            player -> new ModifierLayer<>()
        );
    }

    public static void handlePlayAnimation(PlayAnimationPacket msg,
                                           Supplier<NetworkEvent.Context> ctx) {
        if (ctx.get().getDirection().getReceptionSide().isClient()) {
            ctx.get().enqueueWork(() -> {
                var player = Minecraft.getInstance().player;
                if (player != null) {
                    playAnimation(player, msg.getAnimationId());
                }
            });
        }
        ctx.get().setPacketHandled(true);
    }

    public static void playAnimation(AbstractClientPlayer player, String animationId) {
        var animation = (ModifierLayer<IAnimation>) PlayerAnimationAccess
                .getPlayerAssociatedData(player)
                .get(new ResourceLocation(Doomsday.MODID, "animation"));
        if (animation != null) {
            var anim = PlayerAnimationRegistry.getAnimation(
                    new ResourceLocation(Doomsday.MODID, animationId)
            );
            if (anim != null) {
                animation.setAnimation(new KeyframeAnimationPlayer(anim));
            }
        }
    }
}
