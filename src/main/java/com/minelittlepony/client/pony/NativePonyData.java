package com.minelittlepony.client.pony;

import net.minecraft.client.texture.NativeImage;

import com.google.common.base.MoreObjects;
import com.minelittlepony.api.pony.IPonyData;
import com.minelittlepony.api.pony.TriggerPixelSet;
import com.minelittlepony.api.pony.TriggerPixelType;
import com.minelittlepony.api.pony.TriggerPixelValue;
import com.minelittlepony.api.pony.meta.*;
import com.minelittlepony.client.MineLittlePony;
import com.minelittlepony.common.util.animation.Interpolator;

import java.util.Map;
import java.util.TreeMap;
import java.util.UUID;

import org.jetbrains.annotations.Unmodifiable;

/**
 * Implementation for IPonyData.
 */
@Unmodifiable
class NativePonyData implements IPonyData {
    private final TriggerPixelValue<Race> race;
    private final TriggerPixelValue<TailLength> tailLength;
    private final TriggerPixelValue<TailShape> tailShape;
    private final TriggerPixelValue<Gender> gender;
    private final TriggerPixelValue<Size> size;
    private final int glowColor;

    private final TriggerPixelSet<Wearable> wearables;

    private final Map<String, TriggerPixelType<?>> attributes = new TreeMap<>();

    NativePonyData(NativeImage image) {
        race = TriggerPixel.RACE.readValue(image);
        tailLength = TriggerPixel.TAIL.readValue(image);
        tailShape = TriggerPixel.TAIL_SHAPE.readValue(image);
        size = TriggerPixel.SIZE.readValue(image);
        gender = TriggerPixel.GENDER.readValue(image);
        glowColor = TriggerPixel.GLOW.readColor(image);
        wearables = TriggerPixel.WEARABLES.readFlags(image);

        attributes.put("race", race);
        attributes.put("tailLength", tailLength);
        attributes.put("tailShape", tailShape);
        attributes.put("gender", gender);
        attributes.put("size", size);
        attributes.put("magic", TriggerPixelType.of(glowColor));
        attributes.put("gear", wearables);
    }

    @Override
    public Race getRace() {
        return race.getValue();
    }

    @Override
    public TailLength getTailLength() {
        return tailLength.getValue();
    }

    @Override
    public TailShape getTailShape() {
        return tailShape.getValue();
    }

    @Override
    public Gender getGender() {
        return gender.getValue();
    }

    @Override
    public Sizes getSize() {
        Sizes sz = MineLittlePony.getInstance().getConfig().sizeOverride.get();

        if (sz != Sizes.UNSET) {
            return sz;
        }

        if (size.getValue() == Sizes.UNSET || !MineLittlePony.getInstance().getConfig().sizes.get()) {
            return Sizes.NORMAL;
        }

        return (Sizes)size.getValue();
    }

    @Override
    public int getGlowColor() {
        return glowColor;
    }

    @Override
    public Wearable[] getGear() {
        return Wearable.flags(wearables.getValue());
    }

    @Override
    public boolean isWearing(Wearable wearable) {
        return wearables.matches(wearable);
    }

    @Override
    public Interpolator getInterpolator(UUID interpolatorId) {
        return Interpolator.linear(interpolatorId);
    }

    @Override
    public Map<String, TriggerPixelType<?>> getTriggerPixels() {
        return attributes;
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("race", race.getValue())
                .add("tailLength", tailLength.getValue())
                .add("tailShape", tailShape.getValue())
                .add("gender", gender.getValue())
                .add("size", size.getValue())
                .add("wearables", getGear())
                .add("glowColor", TriggerPixelType.toHex(glowColor))
                .toString();
    }
}
