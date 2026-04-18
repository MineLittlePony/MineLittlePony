package com.minelittlepony.api.state;

import net.minecraft.world.entity.EntityType;

import com.minelittlepony.api.model.PonyModel;

import java.util.Optional;

/**
 * Interface applied to any render states that use a pony renderer.
 * <p>
 * This includes non-pony ponified entities, such as the Allay (Breezie), Vex (Parasprite), Strider (Dragon), and the Armor Stand.
 * To check for actual ponies use {@link com.minelittlepony.api.model.PonyModel.AttributedHolder}
 */
public interface PonifiedRenderState {
    /**
     * Gets the pony render state wrapped by this one (if one exists)
     */
    default Optional<PonyModel.AttributedHolder> getPonyState() {
        return Optional.empty();
    }

    boolean isOf(EntityType<?> entityType);
}
