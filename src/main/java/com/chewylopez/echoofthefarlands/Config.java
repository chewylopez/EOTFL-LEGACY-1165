package com.chewylopez.echoofthefarlands;

import net.minecraftforge.common.ForgeConfigSpec;

public class Config {

    private static final ForgeConfigSpec.Builder BUILDER = new ForgeConfigSpec.Builder();

    public static final ForgeConfigSpec.IntValue FARLANDS_LOCATION_CUSTOM =
            BUILDER.comment(
                            "this is where the custom value you can change from the in-game menu is stored, this is not referenced by the server",
                            "FARLANDS CUSTOM LOCATION")
                    .defineInRange("farlandsCustomLocation", 1000000, 1, Integer.MAX_VALUE);

    public static final ForgeConfigSpec.IntValue FARLANDS_LOCATION_WORLD =
            BUILDER.comment(
                            "",
                            "these are the internal values, only change these manually if you are setting up a server",
                            "FARLANDS LOCATION")
                    .defineInRange("farlandsLocation", 1000000, 1, Integer.MAX_VALUE);

    public static final ForgeConfigSpec.IntValue FARLANDS_GEN_TYPE =
            BUILDER.comment(
                            "",
                            "FARLANDS GEN TYPE",
                            "types are as follows:",
                            "0. piecewise",
                            "1. proportional piecewise",
                            "2. Unscaled",
                            "3. exponential",
                            "4. proportional exponential",
                            "5. Linear",
                            "6. Sine")
                    .defineInRange("farlandsGenType", 0, 0, 7);

    public static final ForgeConfigSpec SPEC = BUILDER.build();
}
