package org.lanstard.doomsday.sanity.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimpleJsonResourceReloadListener;
import net.minecraft.util.profiling.ProfilerFiller;
import org.lanstard.doomsday.Doomsday;
import net.minecraftforge.event.AddReloadListenerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.Map;

@Mod.EventBusSubscriber(modid = Doomsday.MODID)
public class SanityConfig extends SimpleJsonResourceReloadListener {
    private static final Gson GSON = new GsonBuilder().create();
    private static SanityConfigData configData;

    public SanityConfig() {
        super(GSON, "sanity");
    }

    @Override
    protected void apply(Map<ResourceLocation, JsonElement> pObject, ResourceManager pResourceManager, ProfilerFiller pProfiler) {
        pObject.forEach((location, json) -> {
            if (location.getPath().equals("config")) {
                configData = GSON.fromJson(json, SanityConfigData.class);
                Doomsday.LOGGER.info("成功加载理智值配置文件");
            }
        });
    }

    @SubscribeEvent
    public static void onAddReloadListeners(AddReloadListenerEvent event) {
        event.addListener(new SanityConfig());
        Doomsday.LOGGER.info("注册理智值配置重载监听器");
    }

    public static SanityConfigData getConfig() {
        return configData != null ? configData : new SanityConfigData();
    }
} 