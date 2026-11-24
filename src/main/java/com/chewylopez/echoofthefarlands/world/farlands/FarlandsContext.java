package com.chewylopez.echoofthefarlands.world.farlands;

/**
 * Per-thread handoff from the chunk generator to the noise hook.
 *
 * <p>{@code OctavesNoiseGenerator.wrap} is a static utility with no access to the chunk
 * being generated, so the generator records which chunk it is working on just before
 * filling it and the noise hook reads it back. 1.16.5 fills chunks synchronously on the
 * worldgen thread -- unlike 1.18+, where the equivalent returns a CompletableFuture --
 * so a ThreadLocal is sufficient here.
 *
 * <p>Only the radius is tracked for now. The 1.21.1 build also carries a DimensionProfile
 * and an APPLY_FARLANDS gate derived from the generator's noise settings; neither is
 * ported yet, so the hook currently applies in every noise-generated dimension.
 */
public final class FarlandsContext {

    /** Horizontal distance from world origin, in blocks, of the chunk being generated. */
    public static final ThreadLocal<Double> CHUNK_RADIUS = ThreadLocal.withInitial(() -> 0.0);

    private FarlandsContext() {}
}