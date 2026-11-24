package com.chewylopez.echoofthefarlands.event;

import com.chewylopez.echoofthefarlands.Config;
import com.chewylopez.echoofthefarlands.EchoOfTheFarlands;
import com.chewylopez.echoofthefarlands.datagen.WorldFarlandsSettings;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod.EventBusSubscriber(modid = EchoOfTheFarlands.MODID)
public class WorldGenEventHandler {

    private static final Logger LOGGER = LogManager.getLogger();

    @SubscribeEvent
    public static void onWorldOpen(WorldEvent.Load event) {

        if (!(event.getWorld() instanceof ServerWorld)) {
            return;
        }

        ServerWorld level = (ServerWorld) event.getWorld();
        WorldFarlandsSettings settings = WorldFarlandsSettings.getSettings(level);

        if (settings.isNewWorld()) {
            settings.setFarlandsPosition(Config.FARLANDS_LOCATION_WORLD.get());
            settings.setFarlandsGenType(Config.FARLANDS_GEN_TYPE.get());

            LOGGER.info("new world {} seeded from global config -- farlands position: {}, gen type: {}",
                    level.dimension().location(),
                    settings.getFarlandsPosition(),
                    settings.getFarlandsGenType());
        }

        // every time on world open
        Config.FARLANDS_LOCATION_WORLD.set(settings.getFarlandsPosition());
        Config.FARLANDS_GEN_TYPE.set(settings.getFarlandsGenType());
        settings.setDirty();
    }
}
