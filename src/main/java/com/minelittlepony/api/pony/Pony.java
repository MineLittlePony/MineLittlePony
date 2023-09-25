package com.minelittlepony.api.pony;

import net.minecraft.util.Identifier;

import org.jetbrains.annotations.Nullable;

import com.google.common.collect.ComparisonChain;
import com.minelittlepony.api.config.PonyConfig;
import com.minelittlepony.api.pony.meta.Race;
import com.minelittlepony.api.pony.meta.Size;

import java.util.Optional;
import java.util.function.Supplier;

public record Pony (
        /**
         * Gets the texture used for rendering this pony.
         */
        Identifier texture,
        Supplier<Optional<PonyData>> metadataGetter
    ) implements Comparable<Pony> {
    /**
     * Gets the global pony manager instance.
     */
    public static PonyManager getManager() {
        return PonyManager.Instance.instance;
    }

    /**
     * Gets the metadata associated with this pony's model texture.
     */
    public PonyData metadata() {
        return metadataGetter().get().orElse(PonyData.NULL);
    }

    /**
     * Returns whether this pony's metadata block has been initialized.
     */
    public boolean hasMetadata() {
        return metadataGetter().get().isPresent();
    }

    public Pony immutableCopy() {
        final Optional<PonyData> metadata = metadataGetter().get();
        return new Pony(texture(), () -> metadata);
    }

    /**
     * Gets the race associated with this pony.
     */
    public Race race() {
        return PonyConfig.getEffectiveRace(metadata().race());
    }

    public Size size() {
        return PonyConfig.getEffectiveSize(metadata().size());
    }

    /**
     * Returns true if and only if this metadata represents a pony that can cast magic.
     */
    public boolean hasMagic() {
        return race().hasHorn() && metadata().glowColor() != 0;
    }

    @Override
    public int compareTo(@Nullable Pony o) {
        return o == this ? 0 : o == null ? 1 : ComparisonChain.start()
                .compare(texture(), o.texture())
                .compare(metadata(), o.metadata())
                .result();
    }
}
