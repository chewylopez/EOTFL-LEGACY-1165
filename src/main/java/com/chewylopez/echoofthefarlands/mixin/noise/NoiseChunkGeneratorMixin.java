package com.chewylopez.echoofthefarlands.mixin.noise;

import com.chewylopez.echoofthefarlands.world.farlands.FarlandsContext;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.IWorld;
import net.minecraft.world.chunk.IChunk;
import net.minecraft.world.gen.NoiseChunkGenerator;
import net.minecraft.world.gen.feature.structure.StructureManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(NoiseChunkGenerator.class)
public class NoiseChunkGeneratorMixin {

    @Inject(method = "fillFromNoise", at = @At("HEAD"))
    private void setChunkContext(IWorld level, StructureManager structureManager, IChunk chunk, CallbackInfo ci) {
        ChunkPos pos = chunk.getPos();
        double radius = Math.max(Math.abs((pos.x << 4) + 8), Math.abs((pos.z << 4) + 8));
        FarlandsContext.CHUNK_RADIUS.set(radius);
    }

    @Inject(method = "fillFromNoise", at = @At("RETURN"))
    private void clearChunkContext(IWorld level, StructureManager structureManager, IChunk chunk, CallbackInfo ci) {
        FarlandsContext.CHUNK_RADIUS.remove();
    }
}