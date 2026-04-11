package com.minelittlepony.api.pony.meta;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;

/**
 * Represents the different model sizes that are possible.
 *
 * For a list of possible presets, look at {@link SizePreset}.
 * This interface exists for servers so they can work with this information even though they might not have access to the client config.
 *
 */
public interface Size extends TValue<Size> {
    public static final StreamCodec<FriendlyByteBuf, Size> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.INT, Size::ordinal,
            ByteBufCodecs.STRING_UTF8, Size::name,
            ByteBufCodecs.FLOAT, Size::shadowSize,
            ByteBufCodecs.FLOAT, Size::scaleFactor,
            ByteBufCodecs.FLOAT, Size::eyeHeightFactor,
            ByteBufCodecs.FLOAT, Size::eyeDistanceFactor,
            ByteBufCodecs.INT, Size::colorCode,
            MsgSize::new
    );

    /**
     * The Enum index of this size. May be used on the client to convert to an instance of Sizes or use {@link SizePreset#of}
     *
     * Made to be compatible with the enum variant.
     */
    int ordinal();

    /**
     * Name of the size.
     *
     * Made to be compatible with the enum variant.
     */
    String name();

    /**
     * A scale factor that controls the size of the shadow that appears under the entity.
     */
    float shadowSize();

    /**
     * The global scale factor applied to all physical dimensions.
     */
    float scaleFactor();

    /**
     * A scale factor used to alter the vertical eye position.
     */
    float eyeHeightFactor();

    /**
     * A scale factor used to alter the camera's distance.
     */
    float eyeDistanceFactor();

    /**
     * The trigger pixel colour corresponding to this size.
     */
    int colorCode();

    public record MsgSize (
            int ordinal,
            String name,
            float shadowSize,
            float scaleFactor,
            float eyeHeightFactor,
            float eyeDistanceFactor,
            int colorCode) implements Size {
        @Override
        public String toString() {
            return name;
        }
    }
}
