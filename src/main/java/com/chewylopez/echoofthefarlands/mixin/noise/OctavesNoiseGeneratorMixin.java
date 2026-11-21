package com.chewylopez.echoofthefarlands.mixin.noise;

import net.minecraft.world.gen.OctavesNoiseGenerator;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(OctavesNoiseGenerator.class)
public class OctavesNoiseGeneratorMixin {

    @Unique
    private static final double NOISE_SCALAR_CONSTANT = 171.103;

    @Unique
    private static final double FARLANDS_ORIGINAL = 12550824;

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

    @Unique
    private static double expScaleFactorProportional(double value){
        return 1 + (2*(getApproxFarlandsProgress(Math.abs(value))/(FARLANDS_LOCATION)));
    }

    @Unique
    private static double getApproxFarlandsProgress(double value){
        return (Math.abs(value) - getScaledFarlandsLocation()) / NOISE_SCALAR_CONSTANT;
    }

    /**

     */

    @Inject(method = "wrap", at = @At("HEAD"), cancellable = true)
    private static void wrapFarlands(double value, CallbackInfoReturnable<Double> cir) {

        double returnable = value;

        if (Math.abs(value) > getScaledFarlandsLocation()) {
            returnable = exponentialScalingProportional(value);
        }

        cir.setReturnValue(returnable);
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

    @Unique
    private static double exponentialScalingProportional(double value) {
        double returnable = value;
        if(value >= 0) {
            returnable = Math.pow(Integer.MAX_VALUE, expScaleFactorProportional(value));
        }
        if(value < 0) {
            returnable = -Math.pow(Integer.MAX_VALUE, expScaleFactorProportional(value));
        }
        return returnable;
    }

}
