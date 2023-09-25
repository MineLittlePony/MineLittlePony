package com.minelittlepony.client;

import net.minecraft.client.MinecraftClient;
import net.minecraft.resource.metadata.ResourceMetadataReader;
import net.minecraft.util.Identifier;

import com.google.gson.*;
import com.minelittlepony.api.pony.PonyData;
import com.minelittlepony.api.pony.meta.*;
import com.minelittlepony.client.util.render.NativeUtil;

import java.io.IOException;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Supplier;

import org.jetbrains.annotations.Nullable;

class PonyDataLoader {
    static final Supplier<Optional<PonyData>> NULL = loaded(PonyData.NULL);
    private static final ResourceMetadataReader<PonyData> SERIALIZER = new ResourceMetadataReader<PonyData>() {
        private static final Gson GSON = new GsonBuilder()
                .excludeFieldsWithoutExposeAnnotation()
                .create();

        @Override
        public String getKey() {
            return "pony";
        }

        @Override
        public PonyData fromJson(JsonObject json) {
            return GSON.fromJson(json, PonyData.class);
        }
    };

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
                MineLittlePony.logger.warn("Unable to read {} metadata", identifier, e);
            }
            return Optional.empty();
        }).map(PonyDataLoader::loaded).orElseGet(() -> {
            return load(callback -> {
                NativeUtil.parseImage(identifier, image -> {
                    callback.accept(new PonyData(
                            TriggerPixel.RACE.readValue(image),
                            TriggerPixel.TAIL.readValue(image),
                            TriggerPixel.TAIL_SHAPE.readValue(image),
                            TriggerPixel.GENDER.readValue(image),
                            TriggerPixel.SIZE.readValue(image),
                            TriggerPixel.GLOW.readColor(image),
                            TriggerPixel.WEARABLES.readFlags(image),
                            noSkin
                    ));
                }, e -> {
                    MineLittlePony.logger.fatal("Unable to read {} metadata", identifier, e);
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
