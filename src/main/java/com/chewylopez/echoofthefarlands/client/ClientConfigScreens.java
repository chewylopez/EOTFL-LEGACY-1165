package com.chewylopez.echoofthefarlands.client;

import net.minecraftforge.fml.ExtensionPoint;
import net.minecraftforge.fml.ModLoadingContext;

public final class ClientConfigScreens {

    private ClientConfigScreens() {}

    public static void register() {
        ModLoadingContext.get().registerExtensionPoint(
                ExtensionPoint.CONFIGGUIFACTORY,
                () -> (minecraft, parent) -> new ConfigScreen(parent));
    }
}
