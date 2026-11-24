package com.chewylopez.echoofthefarlands;

import com.chewylopez.echoofthefarlands.client.ClientConfigScreens;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

// The value here should match an entry in the META-INF/mods.toml file
@Mod(EchoOfTheFarlands.MODID)
public class EchoOfTheFarlands
{
    public static final String MODID = "echoofthefarlands";

    private static final Logger LOGGER = LogManager.getLogger();

    public EchoOfTheFarlands() {
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::setup);
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::doClientStuff);

        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, Config.SPEC);

        MinecraftForge.EVENT_BUS.register(this);
    }

    private void setup(final FMLCommonSetupEvent event)
    {
        LOGGER.info("{} common setup", MODID);
    }

    private void doClientStuff(final FMLClientSetupEvent event) {
        LOGGER.info("{} client setup", MODID);
        ClientConfigScreens.register();
    }
}
