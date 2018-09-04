package com.voxelmodpack.hdskins.util;

import net.minecraft.client.Minecraft;

import java.util.concurrent.Callable;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.function.BiFunction;

public class CallableFutures {

    private static final ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();

    public static <T> CompletableFuture<T> asyncFailableFuture(Callable<T> call, Executor exec) {
        CompletableFuture<T> ret = new CompletableFuture<>();
        exec.execute(() -> {
            try {
                ret.complete(call.call());
            } catch (Throwable e) {
                ret.completeExceptionally(e);
            }
        });
        return ret;
    }

    public static <T> CompletableFuture<T> failedFuture(Exception e) {
        CompletableFuture<T> ret = new CompletableFuture<>();
        ret.completeExceptionally(e);
        return ret;
    }

    public static <U, T> BiFunction<? super T, Throwable, ? extends U> callback(Runnable c) {
        return (o, t) -> {
            c.run();
            return null;
        };
    }

    public static void scheduleTask(Runnable task) {
        // schedule a task for next tick.
        executor.schedule(() -> {
            Minecraft.getMinecraft().addScheduledTask(task);
        }, 50, TimeUnit.MILLISECONDS);
    }
}
