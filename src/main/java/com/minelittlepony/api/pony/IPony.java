package com.minelittlepony.api.pony;

import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;

import com.minelittlepony.api.pony.meta.Race;
import com.minelittlepony.client.MineLittlePony;

public interface IPony {

    /**
     * Gets or creates a new pony associated with the provided resource location.
     * The results of this method should not be cached.
     */
    static IPony forResource(Identifier texture) {
        return MineLittlePony.getInstance().getManager().getPony(texture);
    }

    /**
     * Triggers state updates on the passed entity.
     */
    void updateForEntity(Entity entity);

    /**
     * Returns true if this pony has wings and the will to use them.
     */
    default boolean canFly() {
        return getRace().hasWings();
    }

    /**
     * Checks the required conditions for whether the given entity can perform a sonic rainboom.
     */
    boolean isPerformingRainboom(LivingEntity entity);

    /**
     * Returns whether this is one of the default ponies assigned to a player without a custom skin.
     */
    boolean isDefault();

    /**
     * Unlike sneaking, crouching is a more specific animation parameter that controls whether the player is visible
     * nose to the ground, crouching the sand.
     *
     * You cannot crouch whilst flying or swimming.
     */
    boolean isCrouching(LivingEntity entity);

    /**
     * Returns true if the provided entity is flying like a pegasus.
     * True if the entity is off the ground.
     * Creative flight counts only if the entity is <i>not</i> on the ground.
     *
     * Entities that are riding, climbing a ladder, or swimming are <i>not</i> flying.
     */
    boolean isFlying(LivingEntity entity);

    /**
     * Returns true if the provided entity is actively swimming.
     * That is, it should be fully submerged (isFullySubmerged returns true)
     * and is not standing on the (river) bed or riding a ladder or any other entity.
     */
    boolean isSwimming(LivingEntity entity);

    /**
     * Returns true if the provided entity is fully submerged with water reaching the entity's eyeheight or above.
     */
    boolean isFullySubmerged(LivingEntity entity);

    /**
     * Returns true if the provided entity is partially submerged. That is if any part of it is in contact with water.
     */
    boolean isPartiallySubmerged(LivingEntity entity);

    @Deprecated
    default Race getRace(boolean ignorePony) {
        return com.minelittlepony.client.pony.Pony.getEffectiveRace(getMetadata().getRace(), ignorePony);
    }

    /**
     * Gets the race associated with this pony.
     */
    Race getRace();

    /**
     * Returns true if an entity is sitting as when riding a vehicle or
     * a customized habitually actuated indoors rester (CHAIR)
     */
    boolean isSitting(LivingEntity entity);

    /**
     * Returns true if an entity is riding a pony or other sentient life-form.
     *
     * Boats do not count.
     */
    boolean isRidingInteractive(LivingEntity entity);

    /**
     * Returns the pony this entity is currently riding if any.
     */
    IPony getMountedPony(LivingEntity entity);

    /**
     * Gets the texture used for rendering this pony.
     */
    Identifier getTexture();

    /**
     * Gets the metadata associated with this pony's model texture.
     */
    IPonyData getMetadata();

    /**
     * Gets the riding offset of this entity relative to its lowermost mount.
     */
    Vec3d getAbsoluteRidingOffset(LivingEntity entity);

    /**
     * Gets the actual bounding box of this entity as a pony.
     */
    Box getComputedBoundingBox(LivingEntity entity);
}
