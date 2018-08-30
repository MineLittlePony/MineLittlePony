package com.minelittlepony.pony.data;

import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.ResourceLocation;

import com.minelittlepony.MineLittlePony;

public interface IPony {
    /**
     * Gets or creates a new pony associated with the provided player.
     * The results of this method should not be cached.
     */
    static IPony forPlayer(AbstractClientPlayer player) {
        return MineLittlePony.getInstance().getManager().getPony(player);
    }

    /**
     * Returns true if the provided entity is flying like a pegasus.
     * True if the entity is off the ground, has race with wings.
     * Creative flight counts only if the entity is <i>not</i> on the ground.
     *
     * Entities that are riding, climbing a ladder, or swimming are <i>not</i> flying.
     */
    boolean isPegasusFlying(EntityLivingBase entity);

    /**
     * Returns true if the provided antity is actively wimming.
     * That is, it should be fully submerged (isFullySubmerged returns true)
     * and is not standing on the (river) bed or riding a ladder or any other entity.
     */
    boolean isSwimming(EntityLivingBase entity);

    /**
     * Returns true if the provided entity is fully submerged with water reaching the entity's eyeheight or above.
     */
    boolean isFullySubmerged(EntityLivingBase entity);

    /**
     * Returns true if an entity is wearing any headgear. This is used to hide things like the snout when wearing items
     * such as the jack-o-lantern, helmet, or player's head.
     */
    boolean isWearingHeadgear(EntityLivingBase entity);

    /**
     * Gets the race associated with this pony.
     *
     * @param ignorePony    True to ignore the client's current pony level setting.
     */
    PonyRace getRace(boolean ignorePony);

    /**
     * Gets the texture used for rendering this pony.
     * @return
     */
    ResourceLocation getTexture();

    /**
     * Gets the metadata associated with this pony's model texture.
     */
    IPonyData getMetadata();
}
