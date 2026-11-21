package com.chewylopez.echoofthefarlands.mixin.client;

import net.minecraft.client.renderer.chunk.ChunkRenderDispatcher;
import net.minecraft.util.concurrent.DelegatedTaskExecutor;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.PriorityQueue;

/**
 * Serialises access to {@code ChunkRenderDispatcher.toBatch}, fixing a vanilla 1.16.5
 * race that the Far Lands reliably trigger.
 *
 * <p>{@code toBatch} is a plain {@link PriorityQueue}, which is not thread safe, yet it is
 * mutated from two threads: {@code schedule} offers to it from the "Chunk Renderer" mailbox
 * thread, while {@code clearBatchQueue} polls it from the render thread. Concurrent
 * structural modification corrupts the heap array, after which
 * {@code PriorityQueue.siftDownComparable} can dereference a null child and throw:
 *
 * <pre>
 * java.lang.NullPointerException
 *   at java.util.PriorityQueue.siftDownComparable(PriorityQueue.java:703)
 *   at java.util.PriorityQueue.poll(PriorityQueue.java:595)
 *   at ChunkRenderDispatcher.clearBatchQueue(ChunkRenderDispatcher.java:192)
 *   at WorldRenderer.allChanged -> setupRender
 * </pre>
 *
 * <p>The render thread only reaches {@code clearBatchQueue} via
 * {@code WorldRenderer.setupRender}, which calls {@code allChanged()} when render distance
 * changes (also F3+A). That is a rare coincidence normally, but Far Lands chunks average
 * 14.8 non-air sections against 5.7 for ordinary terrain, so the queue is far larger and
 * churns constantly -- and changing render distance is exactly what you do when the frame
 * rate collapses out there.
 *
 * <p>Only the two mutators need the lock; {@code size}/{@code isEmpty} just read an int
 * field and cannot corrupt anything. {@code offer} lives inside a lambda in
 * {@code schedule}, and synthetic lambda names are absent from the SRG mappings, so
 * targeting it by name would work in dev and fail in a reobfuscated jar. Redirecting the
 * {@code mailbox.tell} call instead wraps the whole lambda body in the monitor using only
 * mapped members. Java monitors are reentrant, so {@code runTask} being invoked from
 * inside that body is fine.
 */
@Mixin(ChunkRenderDispatcher.class)
public class ChunkRenderDispatcherMixin {

    @Shadow
    @Final
    private PriorityQueue<?> toBatch;

    @Redirect(
            method = {"runTask", "clearBatchQueue"},
            at = @At(value = "INVOKE", target = "Ljava/util/PriorityQueue;poll()Ljava/lang/Object;"))
    private Object eotfl$syncPoll(PriorityQueue<?> queue) {
        synchronized (queue) {
            return queue.poll();
        }
    }

    @Redirect(
            method = "schedule",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/util/concurrent/DelegatedTaskExecutor;tell(Ljava/lang/Object;)V"))
    private void eotfl$syncTell(DelegatedTaskExecutor<Runnable> mailbox, Object task) {
        final Runnable body = (Runnable) task;
        final Object lock = this.toBatch;
        mailbox.tell(() -> {
            synchronized (lock) {
                body.run();
            }
        });
    }
}
