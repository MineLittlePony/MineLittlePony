package com.voxelmodpack.hdskins.util;

import java.util.concurrent.Callable;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.function.BiFunction;

public class CallableFutures {

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
}
