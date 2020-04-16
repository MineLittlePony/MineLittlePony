package com.minelittlepony.client.pony;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.texture.NativeImage;
import net.minecraft.resource.Resource;
import net.minecraft.util.Identifier;

import com.google.common.base.MoreObjects;
import com.google.gson.annotations.Expose;
import com.minelittlepony.api.pony.IPonyData;
import com.minelittlepony.api.pony.meta.Gender;
import com.minelittlepony.api.pony.meta.Race;
import com.minelittlepony.api.pony.meta.Size;
import com.minelittlepony.api.pony.meta.TailLength;
import com.minelittlepony.api.pony.meta.TriggerPixels;
import com.minelittlepony.api.pony.meta.Wearable;
import com.minelittlepony.client.MineLittlePony;
import com.minelittlepony.client.util.render.NativeUtil;
import com.minelittlepony.common.util.animation.Interpolator;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.UUID;

import javax.annotation.Nullable;
import javax.annotation.concurrent.Immutable;

/**
 * Implementation for IPonyData.
 *
 */
@Immutable
public class PonyData implements IPonyData {

    private static final PonyDataSerialiser SERIALISER = new PonyDataSerialiser();

    public static final IPonyData NULL = new PonyData(Race.HUMAN);

    /**
     * Parses the given resource into a new IPonyData.
     * This may either come from an attached json file or the image itself.
     */
    public static IPonyData parse(@Nullable Identifier identifier) {
        if (identifier == null) {
            return NULL;
        }

        try (Resource res = MinecraftClient.getInstance().getResourceManager().getResource(identifier)) {
            PonyData data = res.getMetadata(SERIALISER);

            if (data != null) {
                return data;
            }
        } catch (FileNotFoundException e) {
            // Ignore uploaded texture
        } catch (IOException e) {
            MineLittlePony.logger.warn("Unable to read {} metadata", identifier, e);
        }

        try {
            return NativeUtil.parseImage(identifier, PonyData::new);
        } catch (IllegalStateException e) {
            MineLittlePony.logger.fatal("Unable to read {} metadata", identifier, e);
            return NULL;
        }
    }

    @Expose
    private final Race race;

    @Expose
    private final TailLength tailSize;

    @Expose
    private final Gender gender;

    @Expose
    private final Size size;

    @Expose
    private final int glowColor;

    @Expose
    private final boolean[] wearables;

    public PonyData(Race race) {
        this.race = race;
        tailSize = TailLength.FULL;
        gender = Gender.MARE;
        size = Size.NORMAL;
        glowColor = 0x4444aa;

        wearables = new boolean[Wearable.values().length];
    }

    private PonyData(NativeImage image) {
        race = TriggerPixels.RACE.readValue(image);
        tailSize = TriggerPixels.TAIL.readValue(image);
        size = TriggerPixels.SIZE.readValue(image);
        gender = TriggerPixels.GENDER.readValue(image);
        glowColor = TriggerPixels.GLOW.readColor(image);

        wearables = TriggerPixels.WEARABLES.readFlags(image);
    }

    @Override
    public Race getRace() {
        return race;
    }

    @Override
    public TailLength getTail() {
        return tailSize;
    }

    @Override
    public Gender getGender() {
        return gender;
    }

    @Override
    public Size getSize() {
        return size.getEffectiveSize();
    }

    @Override
    public int getGlowColor() {
        return glowColor;
    }

    @Override
    public boolean hasHorn() {
        return getRace() != null && getRace().getEffectiveRace(false).hasHorn();
    }

    @Override
    public boolean hasMagic() {
        return hasHorn() && getGlowColor() != 0;
    }

    @Override
    public boolean isWearing(Wearable wearable) {
        return wearables[wearable.ordinal()];
    }

    @Override
    public Interpolator getInterpolator(UUID interpolatorId) {
        return Interpolator.linear(interpolatorId);
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("race", race)
                .add("tailSize", tailSize)
                .add("gender", gender)
                .add("size", size)
                .add("wearables", Wearable.flags(wearables))
                .add("glowColor", "#" + Integer.toHexString(glowColor))
                .toString();
    }
}
