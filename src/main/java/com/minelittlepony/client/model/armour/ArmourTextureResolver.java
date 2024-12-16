package com.minelittlepony.client.model.armour;

import net.fabricmc.fabric.api.resource.IdentifiableResourceReloadListener;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.CustomModelDataComponent;
import net.minecraft.item.*;
import net.minecraft.item.equipment.EquipmentModel;
import net.minecraft.resource.ResourceManager;
import net.minecraft.resource.ResourceReloader;
import net.minecraft.util.Identifier;

import com.google.common.cache.*;
import com.minelittlepony.client.MineLittlePony;

import java.util.*;
import java.util.concurrent.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * The default texture resolver used by Mine Little Pony.
 * <p>
 * Textures are resolved by taking the original path and replacing "humanoid" with "ponified".
 * <p>
 * For example:
 *
 * assets/minecraft/textures/entity/equipment/humanoid/iron.png
 * Becomes: assets/minecraft/textures/entity/equipment/ponified/iron.png
 *
 * assets/minecraft/textures/entity/equipment/humanoid_leggings/iron.png
 * Becomes: assets/minecraft/textures/entity/equipment/ponified_leggings/iron.png
 * <p>
 * In addition to the above, unlike in vanilla, all pony armour pieces make use of both the regular and leggings textures to show in two different layers.
 * In general, the textures are distributed as follows:
 * Helmet = ponified_leggings
 * Chestplate = ponified (banner) + ponified_leggings (body plates)
 * Leggings = ponified_leggings (leg chainmail)
 * Boots = ponified (knee guards and boots)
 */
public class ArmourTextureResolver implements ArmourTextureLookup, IdentifiableResourceReloadListener {
    public static final Identifier ID = MineLittlePony.id("armor_textures");
    public static final ArmourTextureResolver INSTANCE = new ArmourTextureResolver();

    private final LoadingCache<ArmourParameters, ArmourTexture> layerCache = CacheBuilder.newBuilder()
            .expireAfterAccess(30, TimeUnit.SECONDS)
            .build(CacheLoader.from(parameters -> {
                return Stream.of(ArmourTexture.legacy(parameters.textureId())).flatMap(i -> {
                    if (parameters.customModelId() != 0) {
                        return Stream.of(ArmourTexture.legacy(i.texture().withPath(p -> p.replace(".png", parameters.customModelId() + ".png"))), i);
                    }
                    return Stream.of(i);
                }).flatMap(this::performLookup).findFirst().orElse(ArmourTexture.UNKNOWN);
            }));

    private Stream<ArmourTexture> performLookup(ArmourTexture id) {
        List<ArmourTexture> options = Stream.of(id).flatMap(ArmourTexture::ponify).toList();
        return options.stream().distinct()
                .filter(ArmourTexture::validate)
                .findFirst()
                .or(() -> {
            MineLittlePony.LOGGER.warn("Could not identify correct texture to use for {}. Was none of: [" + System.lineSeparator() + "{}" + System.lineSeparator() + "]", id, options.stream()
                    .map(ArmourTexture::texture)
                    .map(Identifier::toString)
                    .collect(Collectors.joining("," + System.lineSeparator())));
            return Optional.empty();
        }).stream();
    }

    public void invalidate() {
        layerCache.invalidateAll();
    }

    @Override
    public CompletableFuture<Void> reload(ResourceReloader.Synchronizer synchronizer, ResourceManager manager, Executor prepareExecutor, Executor applyExecutor) {
        return CompletableFuture.runAsync(this::invalidate, prepareExecutor).thenCompose(synchronizer::whenPrepared);
    }

    @Override
    public Identifier getFabricId() {
        return ID;
    }

    @Override
    public ArmourTexture getTexture(ItemStack stack, EquipmentModel.LayerType layerType, EquipmentModel.Layer layer) {
        layerCache.invalidateAll();
        return layerCache.getUnchecked(new ArmourParameters(layer, layerType, getCustom(stack)));
    }

    private int getCustom(ItemStack stack) {
        return stack.getOrDefault(DataComponentTypes.CUSTOM_MODEL_DATA, CustomModelDataComponent.DEFAULT).value();
    }

    private record ArmourParameters(EquipmentModel.Layer layer, EquipmentModel.LayerType layerType, int customModelId) {
        public Identifier textureId() {
            return layer.getFullTextureId(layerType);
        }
    }
}
