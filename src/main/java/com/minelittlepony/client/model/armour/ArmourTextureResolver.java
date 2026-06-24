package com.minelittlepony.client.model.armour;

import net.minecraft.client.resources.model.EquipmentClientInfo;
import net.minecraft.resources.Identifier;
import net.minecraft.server.packs.resources.PreparableReloadListener;
import net.minecraft.world.item.ItemStack;

import com.google.common.cache.*;
import com.minelittlepony.api.model.armour.ArmourTexture;
import com.minelittlepony.api.model.armour.ArmourTextureLookup;
import com.minelittlepony.client.MineLittlePony;

import java.time.Duration;
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
 * Becomes: assets/minecraft/textures/entity/equipment/ponified_humanoid/iron.png
 *
 * assets/minecraft/textures/entity/equipment/humanoid_leggings/iron.png
 * Becomes: assets/minecraft/textures/entity/equipment/ponified_humanoid_leggings/iron.png
 * <p>
 * In addition to the above, unlike in vanilla, all pony armour pieces make use of both the regular and leggings textures to show in two different layers.
 * In general, the textures are distributed as follows:
 * Helmet = ponified_leggings
 * Chestplate = ponified (banner) + ponified_leggings (body plates)
 * Leggings = ponified_leggings (leg chainmail)
 * Boots = ponified (knee guards and boots)
 */
public class ArmourTextureResolver implements ArmourTextureLookup, PreparableReloadListener {
    public static final Identifier ID = MineLittlePony.id("armor_textures");
    public static final ArmourTextureResolver INSTANCE = new ArmourTextureResolver();

    private final LoadingCache<ArmourParameters, ArmourTexture> layerCache = CacheBuilder.newBuilder()
            .expireAfterAccess(Duration.ofSeconds(30))
            .build(CacheLoader.from(parameters -> Stream.of(ArmourTexture.legacy(parameters.layerType(), parameters.textureId()))
                    .flatMap(this::performLookup)
                    .findFirst()
                    .orElse(ArmourTexture.unknown(parameters.layerType()))));

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
    public CompletableFuture<Void> reload(SharedState state, Executor prepareExecutor, PreparationBarrier sync, Executor applyExecutor) {
        return CompletableFuture.runAsync(this::invalidate, prepareExecutor).thenCompose(sync::wait);
    }

    @Override
    public ArmourTexture getTexture(ItemStack stack, EquipmentClientInfo.LayerType layerType, EquipmentClientInfo.Layer layer) {
        layerCache.invalidateAll();
        return layerCache.getUnchecked(new ArmourParameters(layer, layerType));
    }

    private record ArmourParameters(EquipmentClientInfo.Layer layer, EquipmentClientInfo.LayerType layerType) {
        public Identifier textureId() {
            return layer.getTextureLocation(layerType);
        }
    }
}
