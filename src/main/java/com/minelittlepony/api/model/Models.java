package com.minelittlepony.api.model;

import net.minecraft.item.ArmorItem;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Util;

import org.jetbrains.annotations.Nullable;

import com.minelittlepony.client.model.PlayerModelKey;
import com.minelittlepony.client.model.armour.*;
import com.minelittlepony.mson.api.ModelKey;

import java.util.*;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * Container class for the various models and their associated piece of armour.
 */
public record Models<M extends PonyModel<?>> (
        Function<ModelKey<PonyArmourModel<?>>, PonyArmourModel<?>> armor,
        M body
    ) {

    public Models(PlayerModelKey<? super M> playerModelKey, boolean slimArms, @Nullable Consumer<M> initializer) {
        this(Util.memoize(key -> key.createModel(playerModelKey.armorFactory())), playerModelKey.getKey(slimArms).createModel());
        if (initializer != null) {
            initializer.accept(body);
        }
    }

    public Models(ModelKey<M> key) {
        this(Util.memoize(k -> k.createModel()), key.createModel());
    }

    public Optional<PonyArmourModel<?>> getArmourModel(ItemStack stack, ArmourLayer layer, ArmourVariant variant) {
        return ArmorModelRegistry.getModelKey(stack.getItem(), layer).or(() -> variant.getDefaultModel(layer).filter(l -> stack.getItem() instanceof ArmorItem))
                .map(armor);
    }
}
