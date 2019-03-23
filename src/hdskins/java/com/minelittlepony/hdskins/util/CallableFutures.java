package com.minelittlepony.hdskins.util;

import com.google.common.util.concurrent.Runnables;
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

    public static <T> BiFunction<? super T, Throwable, Void> callback(Runnable c) {
        return (o, t) -> {
            if (t != null) {
                t.printStackTrace();
            } else {
                c.run();
            }
            return null;
        };
    }

    public static CompletableFuture<Void> scheduleTask(Runnable task) {
        // schedule a task for next tick.
        return CompletableFuture.runAsync(Runnables.doNothing(), delayed(50, TimeUnit.MILLISECONDS))
                .handleAsync(callback(task), Minecraft.getMinecraft()::addScheduledTask);
    }

    private static Executor delayed(long time, TimeUnit unit) {
        return (task) -> executor.schedule(task, time, unit);
    }
}
