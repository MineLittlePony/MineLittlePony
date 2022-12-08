package com.minelittlepony.api.pony;

import net.minecraft.util.Identifier;
import com.minelittlepony.api.pony.meta.Race;
import com.minelittlepony.client.MineLittlePony;

public interface IPony {

    /**
     * Gets the global pony manager instance.
     */
    static IPonyManager getManager() {
        return MineLittlePony.getInstance().getManager();
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
    Race race();

    /**
     * Gets the texture used for rendering this pony.
     */
    Identifier texture();

    /**
     * Gets the metadata associated with this pony's model texture.
     */
    IPonyData metadata();
}
