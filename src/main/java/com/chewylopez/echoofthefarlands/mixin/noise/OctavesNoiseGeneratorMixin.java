package com.chewylopez.echoofthefarlands.mixin.noise;

import net.minecraft.world.gen.OctavesNoiseGenerator;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * Undoes Minecraft's Far Lands fix and substitutes our own noise coordinate scaling.
 *
 * <p>{@code OctavesNoiseGenerator.wrap} is the fix: it folds noise coordinates back into
 * +/-2^25, which is what stops the floating point breakdown that produced the Far Lands in
 * Beta 1.7.3 and earlier. Cancelling it and returning a different coordinate reintroduces
 * the breakdown wherever we choose.
 *
 * <p>This is the 1.16.5 counterpart of {@code PerlinNoiseMixin} on 1.21.1, which targets
 * {@code PerlinNoise.wrap}. Same method name, byte-identical body, different owning class
 * -- 1.16.5 predates Mojang's package restructure, so the octave-based Perlin generator is
 * called {@code OctavesNoiseGenerator} here. Note that 1.16.5 also has a class literally
 * named {@code PerlinNoiseGenerator}, but it is simplex-based and is not the terrain
 * generator; targeting it would compile and silently do nothing.
 *
 * <p>Only gen type 2 ("Unscaled") is ported so far, and the three config values the 1.21.1
 * version reads are hardcoded below pending a ForgeConfigSpec port.
 */
@Mixin(OctavesNoiseGenerator.class)
public class OctavesNoiseGeneratorMixin {

    /**
     * Converts block coordinates to the noise coordinates {@code wrap} actually receives.
     * Empirical, carried over unchanged from the 1.21.1 build.
     */
    @Unique
    private static final double NOISE_SCALAR_CONSTANT = 171.103;

    /** Where vanilla float precision breaks down on its own, in blocks. */
    @Unique
    private static final double FARLANDS_ORIGINAL = 12550823;

    // --- hardcoded stand-ins for Config, which is not ported yet ---------------------
    // Values match the 1.21.1 defaults, except gen type, which is forced to 2 ("Unscaled")
    // and the wall texture patch, which is off so the porosity branch stays out of play.

    /** Config.FARLANDS_LOCATION_WORLD -- 1.21.1 default is 1000000, not FARLANDS_ORIGINAL. */
    @Unique
    private static final double FARLANDS_LOCATION = 100000;

    @Unique
    private static double getStartPosDelta() {
        return FARLANDS_ORIGINAL - FARLANDS_LOCATION;
    }

    /** The threshold in noise-space past which we stop letting vanilla wrap. */
    @Unique
    private static double getScaledFarlandsLocation() {
        return FARLANDS_LOCATION * NOISE_SCALAR_CONSTANT;
    }

    @Inject(method = "wrap", at = @At("HEAD"), cancellable = true)
    private static void wrapFarlands(double value, CallbackInfoReturnable<Double> cir) {

        double returnable = value;

        if (Math.abs(value) > getScaledFarlandsLocation()) {
            returnable = offsetUnscaledGeneration(value);
        }

        if (returnable != value) {
            cir.setReturnValue(returnable);
        }
    }

    @Unique
    private static double offsetUnscaledGeneration(double value){
        double returnable = value;

        if(value >= 0) {
            returnable = value + (getStartPosDelta() * NOISE_SCALAR_CONSTANT);
        }
        if(value < 0) {
            returnable = value - (getStartPosDelta() * NOISE_SCALAR_CONSTANT);
        }
        return returnable;
    }
}
