package com.chewylopez.echoofthefarlands.world.farlands;

public final class FarlandsContext {
    public static final ThreadLocal<Double> CHUNK_RADIUS = ThreadLocal.withInitial(() -> 0.0);
    private FarlandsContext() {}
}