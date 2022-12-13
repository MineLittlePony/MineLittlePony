package com.minelittlepony.api.pony;

import net.minecraft.util.Identifier;

import org.jetbrains.annotations.Nullable;

import com.google.common.collect.ComparisonChain;
import com.minelittlepony.api.config.PonyConfig;
import com.minelittlepony.api.pony.meta.Race;

public interface IPony extends Comparable<IPony> {

    /**
     * Gets the global pony manager instance.
     */
    static IPonyManager getManager() {
        return IPonyManager.Instance.instance;
    }

    /**
     * Gets or creates a new pony associated with the provided resource location.
     * The results of this method should not be cached.
     *
     * @deprecated User IPony.getManager().getPony(texture) instead
     */
    @Deprecated
    static IPony forResource(Identifier texture) {
        return getManager().getPony(texture);
    }

    /**
     * Returns whether this is one of the default ponies assigned to a player without a custom skin.
     */
    boolean defaulted();

    /**
     * Returns whether this pony's metadata block has been initialized.
     */
    boolean hasMetadata();

    /**
     * Gets the race associated with this pony.
     */
    default Race race() {
        return PonyConfig.getEffectiveRace(metadata().getRace());
    }

    /**
     * Returns true if and only if this metadata represents a pony that can cast magic.
     */
    default boolean hasMagic() {
        return race().hasHorn() && metadata().getGlowColor() != 0;
    }

    /**
     * Gets the texture used for rendering this pony.
     */
    Identifier texture();

    /**
     * Gets the metadata associated with this pony's model texture.
     */
    IPonyData metadata();


    @Override
    default int compareTo(@Nullable IPony o) {
        return o == this ? 0 : o == null ? 1 : ComparisonChain.start()
                .compare(texture(), o.texture())
                .compare(metadata(), o.metadata())
                .result();
    }
}
