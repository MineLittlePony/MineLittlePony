package com.minelittlepony.client.pony;

import java.util.function.Consumer;

public interface Memoize<T> {
    T get();

    default T get(T fallback) {
        T value = get();
        return value == null ? fallback : value;
    }

    default boolean isPresent() {
        return true;
    }

    static <T> Memoize<T> of(T value) {
        return () -> value;
    }

    static <T> Memoize<T> load(Consumer<Consumer<T>> factory) {
        return new Memoize<>() {
            T value;
            boolean loadRequested;
            @Override
            public T get() {
                synchronized (this) {
                    if (!loadRequested) {
                        loadRequested = true;
                        factory.accept(value -> {
                            this.value = value;
                        });
                    }
                }
                return value;
            }
            @Override
            public boolean isPresent() {
                return value != null;
            }
        };
    }
}
