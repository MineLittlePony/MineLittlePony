package com.minelittlepony.client.model.armour;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.texture.TextureManager;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtElement;
import net.minecraft.util.Identifier;
import com.google.common.base.Strings;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.minelittlepony.api.model.armour.ArmourLayer;
import com.minelittlepony.api.model.armour.ArmourVariant;
import com.minelittlepony.api.model.armour.IArmourTextureResolver;
import com.minelittlepony.util.ResourceUtil;

import org.jetbrains.annotations.Nullable;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

/**
 * The default texture resolver used by Mine Little Pony.
 * <p>
 * The search order is as follows:
 * <p>
 * namespace:textures/models/armor/material_layer_[outer|inner](_overlay)(_custom_[0-9]+)_pony.png
 * namespace:textures/models/armor/material_layer_[outer|inner](_overlay)(_custom_[0-9]+).png
 * namespace:textures/models/armor/material_layer_[1|2](_overlay)(_custom_[0-9]+)_pony.png
 * namespace:textures/models/armor/material_layer_[1|2](_overlay)(_custom_[0-9]+).png
 * namespace:textures/models/armor/material_layer_[outer|inner](_overlay)_pony.png
 * namespace:textures/models/armor/material_layer_[outer|inner](_overlay).png
 * namespace:textures/models/armor/material_layer_[1|2](_overlay)_pony.png
 * namespace:textures/models/armor/material_layer_[1|2](_overlay).png
 * <p>
 * - if namespace is "minecraft" will be rewritten to "minelittlepony"
 * <p>
 * For how modders can customise both the model and texture please refer to {@link IArmour} and {@link IArmourTextureResolver}.
 *
 * @see IArmour
 * @see IArmourTextureResolver
 */
public class DefaultArmourTextureResolver implements IArmourTextureResolver {

    private final Cache<String, Identifier> cache = CacheBuilder.newBuilder()
            .expireAfterAccess(30, TimeUnit.SECONDS)
            .<String, Identifier>build();

    @Override
    public Identifier getTexture(LivingEntity entity, ItemStack stack, EquipmentSlot slot, ArmourLayer layer,  @Nullable String type) {
        Identifier material = new Identifier(((ArmorItem) stack.getItem()).getMaterial().getName());
        String custom = getCustom(stack);

        try {
            return cache.get(String.format("%s#%s#%s#%s", material, layer, type, custom), () -> {
                String typed = Strings.nullToEmpty(type);
                String extra = typed.isEmpty() ? "" : "_" + typed;

                Identifier texture;

                if (!"none".equals(custom)) {
                    texture = resolveNewOrOld(material, layer, custom + extra, typed);
                    if (texture != null) {
                        return texture;
                    }
                }

                texture = resolveNewOrOld(material, layer, extra, typed);
                if (texture != null) {
                    return texture;
                }

                return TextureManager.MISSING_IDENTIFIER;
            });
        } catch (ExecutionException ignored) {
            return TextureManager.MISSING_IDENTIFIER;
        }
    }

    private String getCustom(ItemStack stack) {
        if (stack.hasNbt() && stack.getNbt().contains("CustomModelData", NbtElement.NUMBER_TYPE)) {
            return "custom_" + stack.getNbt().getInt("CustomModelData");
        }
        return "none";
    }

    @Nullable
    private Identifier resolveNewOrOld(Identifier material, ArmourLayer layer, String extra, String type) {
        Identifier texture = resolveHumanOrPony(ResourceUtil.format("%s:textures/models/armor/%s_layer_%s%s.png", material.getNamespace(), material.getPath(), layer, extra), type);

        if (texture != null) {
            return texture;
        }

        return resolveHumanOrPony(ResourceUtil.format("%s:textures/models/armor/%s_layer_%d%s.png", material.getNamespace(), material.getPath(), layer.getLegacyId(), extra), type);
    }

    @Nullable
    private Identifier resolveHumanOrPony(String res, String type) {
        Identifier human = new Identifier(res);

        String domain = human.getNamespace();
        if ("minecraft".equals(domain)) {
            domain = "minelittlepony"; // it's a vanilla armor. I provide these.
        }

        Identifier pony = new Identifier(domain, human.getPath().replace(".png", "_pony.png"));

        if (isValid(pony)) {
            return pony;
        }

        if (isValid(human)) {
            return human;
        }

        return null;
    }

    private final boolean isValid(Identifier texture) {
        return MinecraftClient.getInstance().getResourceManager().getResource(texture).isPresent();
    }

    @Override
    public ArmourVariant getVariant(ArmourLayer layer, Identifier resolvedTexture) {
        if (resolvedTexture.getPath().endsWith("_pony.png")) {
            return ArmourVariant.NORMAL;
        }
        return ArmourVariant.LEGACY;
    }
}
