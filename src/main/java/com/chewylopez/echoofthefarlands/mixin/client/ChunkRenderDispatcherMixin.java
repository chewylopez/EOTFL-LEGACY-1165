package com.chewylopez.echoofthefarlands.mixin.client;

import net.minecraft.client.renderer.chunk.ChunkRenderDispatcher;
import net.minecraft.util.concurrent.DelegatedTaskExecutor;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.PriorityQueue;

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
