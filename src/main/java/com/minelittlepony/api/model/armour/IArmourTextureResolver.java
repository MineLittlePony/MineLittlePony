package com.minelittlepony.api.model.armour;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;

import org.jetbrains.annotations.Nullable;

import com.minelittlepony.client.model.armour.DefaultArmourTextureResolver;

/**
 * A resolver for looking up the texture for a piece of armour.
 * <p>
 * This is for modders who want to override the default implementation found in {@link DefaultArmourTextureResolver}.
 */
public interface IArmourTextureResolver {
    /**
     * Gets the armour texture to be used for the given entity, armour piece, slot, and render layer.
     */
    Identifier getTexture(LivingEntity entity, ItemStack itemstack, EquipmentSlot slot, ArmourLayer layer, @Nullable String type);

    /**
     * Gets the armour variant for the identified texture.
     * Either normal for pony-style textures, or legacy for other textures.
     */
    default ArmourVariant getVariant(ArmourLayer layer, Identifier resolvedTexture) {
        return ArmourVariant.NORMAL;
    }
}
