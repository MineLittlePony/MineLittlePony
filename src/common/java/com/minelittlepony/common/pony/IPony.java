package com.minelittlepony.common.pony;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.Vec3d;

import com.minelittlepony.common.MineLittlePony;
import com.minelittlepony.common.pony.meta.Race;

public interface IPony {

    /**
     * Gets or creates a new pony associated with the provided resource location.
     * The results of this method should not be cached.
     */
    static IPony forResource(ResourceLocation texture) {
        return MineLittlePony.getInstance().getManager().getPony(texture);
    }

    /**
     * Returns true if this pony has wings and the will to use them.
     */
    default boolean canFly() {
        return getMetadata().getRace().hasWings();
    }

    /**
     * Checks the required conditions for whether the given entity can perform a sonic rainboom.
     */
    boolean isPerformingRainboom(EntityLivingBase entity);

    /**
     * Unlike sneaking, crouching is a more specific animation parameter that controls whether the player is visible
     * nose to the ground, crouching the sand.
     *
     * You cannot crouch whilst flying or swimming.
     */
    boolean isCrouching(EntityLivingBase entity);

    /**
     * Returns true if the provided entity is flying like a pegasus.
     * True if the entity is off the ground.
     * Creative flight counts only if the entity is <i>not</i> on the ground.
     *
     * Entities that are riding, climbing a ladder, or swimming are <i>not</i> flying.
     */
    boolean isFlying(EntityLivingBase entity);

    /**
     * Returns true if the provided entity is actively swimming.
     * That is, it should be fully submerged (isFullySubmerged returns true)
     * and is not standing on the (river) bed or riding a ladder or any other entity.
     */
    boolean isSwimming(EntityLivingBase entity);

    /**
     * Returns true if the provided entity is fully submerged with water reaching the entity's eyeheight or above.
     */
    boolean isFullySubmerged(EntityLivingBase entity);

    /**
     * Returns true if the provided entity is partially submerged. That is if any part of it is in contact with water.
     */
    boolean isPartiallySubmerged(EntityLivingBase entity);

    /**
     * Returns true if an entity is wearing any headgear. This is used to hide things like the snout when wearing items
     * such as the pumpkin, a player's head, or any other types of blocks.
     *
     * In this case the helmet does <i>not</i> count as headgear because those generally don't interfere with snuzzle rendering.
     */
    boolean isWearingHeadgear(EntityLivingBase entity);

    /**
     * Gets the race associated with this pony.
     *
     * @param ignorePony    True to ignore the client's current pony level setting.
     */
    Race getRace(boolean ignorePony);

    /**
     * Returns true if an entity is riding a pony or other sentient life-form.
     *
     * Boats do not count.
     */
    boolean isRidingInteractive(EntityLivingBase entity);

    /**
     * Returns the pony this entity is currently riding if any.
     */
    IPony getMountedPony(EntityLivingBase entity);

    /**
     * Gets the texture used for rendering this pony.
     */
    ResourceLocation getTexture();

    /**
     * Gets the metadata associated with this pony's model texture.
     */
    IPonyData getMetadata();

    /**
     * Gets the riding offset of this entity relative to its lowermost mount.
     */
    Vec3d getAbsoluteRidingOffset(EntityLivingBase entity);

    /**
     * Gets the actual bounding box of this entity as a pony.
     */
    AxisAlignedBB getComputedBoundingBox(EntityLivingBase entity);
}
