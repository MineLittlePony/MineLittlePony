package com.minelittlepony.client.model.armour;

import net.fabricmc.fabric.api.resource.IdentifiableResourceReloadListener;
import net.minecraft.client.render.entity.equipment.EquipmentModel;
import net.minecraft.item.*;
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
 * Textures found are of the format:
 * <p>
 * namespace:textures/models/armor/material_layer_[outer|1|inner|2](_overlay)(_custom_#)(_pony).png
 * <p>
 * <p>
 * - Textures ending _pony are returned first if found
 * - _custom_# corresponds to a CustomModelData NBT integer value on the item passed, if available
 * - _overlay is used for the second layer of leather armour, or mods if they make use of it. Can be anything! Check your mod's documentation for values supported.
 * - outer|1|inner|2 is the layer. outer is an alias for 1 and inner is an alias for 2. Named versions are used instead of numbers if available.
 * - the "minecraft" namespace is always replaced with "minelittlepony"
 * <p>
 */
public class ArmourTextureResolver implements ArmourTextureLookup, IdentifiableResourceReloadListener {
    public static final Identifier ID = MineLittlePony.id("armor_textures");
    public static final ArmourTextureResolver INSTANCE = new ArmourTextureResolver();

    private final LoadingCache<ArmourParameters, ArmourTexture> layerCache = CacheBuilder.newBuilder()
            .expireAfterAccess(30, TimeUnit.SECONDS)
            .build(CacheLoader.from(parameters -> Stream.of(ArmourTexture.legacy(parameters.textureId())).flatMap(this::performLookup).findFirst().orElse(ArmourTexture.UNKNOWN)));

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
        return layerCache.getUnchecked(new ArmourParameters(layer, layerType));
    }

    private record ArmourParameters(EquipmentModel.Layer layer, EquipmentModel.LayerType layerType) {
        public Identifier textureId() {
            return layer.getFullTextureId(layerType);
        }
    }
}
