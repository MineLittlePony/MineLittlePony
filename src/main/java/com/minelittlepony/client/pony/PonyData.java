package com.minelittlepony.client.pony;

import net.minecraft.client.MinecraftClient;
import net.minecraft.util.Identifier;
import com.google.common.base.MoreObjects;
import com.google.gson.annotations.Expose;
import com.minelittlepony.api.pony.IPonyData;
import com.minelittlepony.api.pony.TriggerPixelType;
import com.minelittlepony.api.pony.meta.*;
import com.minelittlepony.client.MineLittlePony;
import com.minelittlepony.client.util.render.NativeUtil;
import com.minelittlepony.common.util.animation.Interpolator;

import java.io.IOException;
import java.util.*;

import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Unmodifiable;

/**
 * Implementation for IPonyData.
 */
@Unmodifiable
public class PonyData implements IPonyData {

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
                NativeUtil.parseImage(identifier, img -> {
                    callback.accept(new NativePonyData(img));
                }, e -> {
                    MineLittlePony.logger.fatal("Unable to read {} metadata", identifier, e);
                    callback.accept(NULL);
                });
            });
        });
    }

    @Expose
    private final Race race;

    @Expose
    private final TailLength tailLength = TailLength.FULL;

    @Expose
    private final TailShape tailShape = TailShape.STRAIGHT;

    @Expose
    private final Gender gender = Gender.MARE;

    @Expose
    private final Sizes size = Sizes.NORMAL;

    @Expose
    private final int glowColor = 0x4444aa;

    @Expose
    private final boolean[] wearables = new boolean[Wearable.values().length];

    private final Map<String, TriggerPixelType<?>> attributes = new TreeMap<>();

    public PonyData(Race race) {
        this.race = race;
        attributes.put("race", race);
        attributes.put("tailLength", tailLength);
        attributes.put("tailShape", tailShape);
        attributes.put("gender", gender);
        attributes.put("size", size);
        attributes.put("magic", TriggerPixelType.of(glowColor));
        attributes.put("gear", TriggerPixelType.of(0));
    }

    @Override
    public Race getRace() {
        return race;
    }

    @Override
    public TailLength getTailLength() {
        return tailLength;
    }

    @Override
    public TailShape getTailShape() {
        return tailShape;
    }

    @Override
    public Gender getGender() {
        return gender;
    }

    @Override
    public Sizes getSize() {
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
    public int getGlowColor() {
        return glowColor;
    }

    @Override
    public Wearable[] getGear() {
        return Wearable.flags(wearables);
    }

    @Override
    public boolean isWearing(Wearable wearable) {
        return wearables[wearable.ordinal()];
    }

    @Override
    public Interpolator getInterpolator(UUID interpolatorId) {
        return Interpolator.linear(interpolatorId);
    }

    public Map<String, TriggerPixelType<?>> getTriggerPixels() {
        return attributes;
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("race", race)
                .add("tailLength", tailLength)
                .add("tailShape", tailShape)
                .add("gender", gender)
                .add("size", size)
                .add("wearables", getGear())
                .add("glowColor", TriggerPixelType.toHex(glowColor))
                .toString();
    }
}
