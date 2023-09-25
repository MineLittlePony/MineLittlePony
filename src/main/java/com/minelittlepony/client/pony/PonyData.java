package com.minelittlepony.client.pony;

import net.minecraft.client.MinecraftClient;
import net.minecraft.util.Identifier;
import com.minelittlepony.api.pony.IPonyData;
import com.minelittlepony.api.pony.TriggerPixelType;
import com.minelittlepony.api.pony.meta.*;
import com.minelittlepony.client.MineLittlePony;
import com.minelittlepony.client.util.render.NativeUtil;

import java.io.IOException;
import java.util.*;

import org.jetbrains.annotations.Nullable;

/**
 * Implementation for IPonyData.
 */
public record PonyData (
        Race race,
        TailLength tailLength,
        TailShape tailShape,
        Gender gender,
        Size size,
        int glowColor,
        boolean[] wearables,
        Map<String, TriggerPixelType<?>> attributes
    ) implements IPonyData {

    private static final PonyDataSerialiser SERIALISER = new PonyDataSerialiser();

    public static final IPonyData NULL = new PonyData(Race.HUMAN);
    public static final Memoize<IPonyData> MEM_NULL = Memoize.of(NULL);

    /**
     * Parses the given resource into a new IPonyData.
     * This may either come from an attached json file or the image itself.
     */
    public static Memoize<IPonyData> parse(@Nullable Identifier identifier) {
        if (identifier == null) {
            return MEM_NULL;
        }

        return MinecraftClient.getInstance().getResourceManager().getResource(identifier).flatMap(res -> {
            try {
                return res.getMetadata().decode(SERIALISER);
            } catch (IOException e) {
                MineLittlePony.logger.warn("Unable to read {} metadata", identifier, e);
            }
            return null;
        }).map(Memoize::of).orElseGet(() -> {
            return Memoize.load(callback -> {
                NativeUtil.parseImage(identifier, image -> {
                    callback.accept(new PonyData(
                            TriggerPixel.RACE.<Race>readValue(image),
                            TriggerPixel.TAIL.<TailLength>readValue(image),
                            TriggerPixel.TAIL_SHAPE.<TailShape>readValue(image),
                            TriggerPixel.GENDER.<Gender>readValue(image),
                            TriggerPixel.SIZE.<Size>readValue(image),
                            TriggerPixel.GLOW.readColor(image),
                            TriggerPixel.WEARABLES.readFlags(image)
                    ));
                }, e -> {
                    MineLittlePony.logger.fatal("Unable to read {} metadata", identifier, e);
                    callback.accept(NULL);
                });
            });
        });
    }

    public PonyData(Race race) {
        this(race, TailLength.FULL, TailShape.STRAIGHT, Gender.MARE, Sizes.NORMAL, 0x4444aa, new boolean[Wearable.values().length], new TreeMap<>());
        attributes.put("race", race);
        attributes.put("tailLength", tailLength);
        attributes.put("tailShape", tailShape);
        attributes.put("gender", gender);
        attributes.put("size", size);
        attributes.put("magic", TriggerPixelType.of(glowColor));
        attributes.put("gear", TriggerPixelType.of(0));
    }

    PonyData(TriggerPixelType.Value<Race> race, TriggerPixelType.Value<TailLength> tailLength, TriggerPixelType.Value<TailShape> tailShape,
            TriggerPixelType.Value<Gender> gender, TriggerPixelType.Value<Size> size, int glowColor, TriggerPixelType.Flags<Wearable> wearables) {
        this(race.value(), tailLength.value(), tailShape.value(), gender.value(), size.value(), glowColor, wearables.value(), new TreeMap<>());
        attributes.put("race", race);
        attributes.put("tailLength", tailLength);
        attributes.put("tailShape", tailShape);
        attributes.put("gender", gender);
        attributes.put("size", size);
        attributes.put("magic", TriggerPixelType.of(glowColor));
        attributes.put("gear", wearables);
    }

    @Override
    public Size size() {
        Sizes sz = MineLittlePony.getInstance().getConfig().sizeOverride.get();

        if (sz != Sizes.UNSET) {
            return sz;
        }

        if (size == Sizes.UNSET || !MineLittlePony.getInstance().getConfig().sizes.get()) {
            return Sizes.NORMAL;
        }

        return size;
    }

    @Override
    public Wearable[] gear() {
        return Wearable.flags(wearables);
    }

    @Override
    public boolean isWearing(Wearable wearable) {
        return wearables[wearable.ordinal()];
    }
}
