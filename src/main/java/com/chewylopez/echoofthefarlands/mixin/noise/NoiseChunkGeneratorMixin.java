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

/**
 * Publishes the chunk currently being generated so the static noise hook can see it.
 *
 * <p>On 1.21.1 this work happens around {@code doFill}, which returns a CompletableFuture.
 * 1.16.5's {@code fillFromNoise} is plain synchronous void, so setting and clearing a
 * ThreadLocal around it is safe without any extra care.
 *
 * <p>The radius is not consulted by gen type 2, so this hook is currently inert -- it is
 * here because types 0 and 1 gate the moat on it, and because it is the piece of the
 * architecture that genuinely differs between the two versions. Keeping it in place now
 * means the remaining gen types are a pure addition later.
 */
@Mixin(NoiseChunkGenerator.class)
public class NoiseChunkGeneratorMixin {

    @Inject(method = "fillFromNoise", at = @At("HEAD"))
    private void setChunkContext(IWorld level, StructureManager structureManager, IChunk chunk, CallbackInfo ci) {
        ChunkPos pos = chunk.getPos();
        // Chunk centre rather than corner, matching the 1.21.1 build.
        double radius = Math.max(Math.abs((pos.x << 4) + 8), Math.abs((pos.z << 4) + 8));
        FarlandsContext.CHUNK_RADIUS.set(radius);
    }

    @Inject(method = "fillFromNoise", at = @At("RETURN"))
    private void clearChunkContext(IWorld level, StructureManager structureManager, IChunk chunk, CallbackInfo ci) {
        FarlandsContext.CHUNK_RADIUS.remove();
    }
}
