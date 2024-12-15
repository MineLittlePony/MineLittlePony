package com.minelittlepony.client;

import net.minecraft.client.MinecraftClient;
import net.minecraft.resource.metadata.ResourceMetadataSerializer;
import net.minecraft.util.Identifier;

import com.minelittlepony.api.pony.PonyData;
import com.minelittlepony.client.util.render.NativeUtil;

import java.io.IOException;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Supplier;

import org.jetbrains.annotations.Nullable;

public class PonyDataLoader {
    public static final Supplier<Optional<PonyData>> NULL = loaded(PonyData.NULL);
    private static final ResourceMetadataSerializer<PonyData> SERIALIZER = new ResourceMetadataSerializer<PonyData>("pony", PonyData.CODEC);

    /**
     * Parses the given resource into a new IPonyData.
     * This may either come from an attached json file or the image itself.
     */
    public static Supplier<Optional<PonyData>> parse(@Nullable Identifier identifier, boolean noSkin) {
        if (identifier == null) {
            return NULL;
        }

        return MinecraftClient.getInstance().getResourceManager().getResource(identifier).flatMap(res -> {
            try {
                return res.getMetadata().decode(SERIALIZER);
            } catch (IOException e) {
                MineLittlePony.LOGGER.warn("Unable to read {} metadata", identifier, e);
            }
            return Optional.empty();
        }).map(PonyDataLoader::loaded).orElseGet(() -> {
            return load(callback -> {
                NativeUtil.parseImage(identifier, image -> {
                    callback.accept(new PonyData(image, noSkin));
                }, e -> {
                    MineLittlePony.LOGGER.fatal("Unable to read {} metadata", identifier, e);
                    callback.accept(PonyData.NULL);
                });
            });
        });
    }

    private static <T> Supplier<Optional<T>> loaded(T t) {
        final Optional<T> value = Optional.of(t);
        return () -> value;
    }

    private static <T> Supplier<Optional<T>> load(Consumer<Consumer<T>> factory) {
        return new Supplier<Optional<T>>() {
            Optional<T> value = Optional.empty();
            boolean loadRequested;
            @Override
            public Optional<T> get() {
                synchronized (this) {
                    if (!loadRequested) {
                        loadRequested = true;
                        factory.accept(value -> {
                            this.value = Optional.ofNullable(value);
                        });
                    }
                }
                return value;
            }
        };
    }
}
