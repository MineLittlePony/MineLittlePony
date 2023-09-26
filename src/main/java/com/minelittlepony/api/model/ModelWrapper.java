package com.minelittlepony.api.model;

import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.ItemStack;

import org.jetbrains.annotations.Nullable;

import com.minelittlepony.api.pony.PonyData;
import com.minelittlepony.client.model.PlayerModelKey;
import com.minelittlepony.client.model.armour.*;
import com.minelittlepony.mson.api.ModelKey;
import com.minelittlepony.mson.api.MsonModel;

import java.util.*;
import java.util.function.Consumer;

/**
 * Container class for the various models and their associated piece of armour.
 */
public class ModelWrapper<T extends LivingEntity, M extends PonyModel<?>> {
    @Nullable
    private final MsonModel.Factory<PonyArmourModel<T>> armorFactory;
    private final Map<ModelKey<PonyArmourModel<?>>, PonyArmourModel<T>> armor = new HashMap<>();

    private final M body;

    public ModelWrapper(PlayerModelKey<T, ? super M> playerModelKey, boolean slimArms, @Nullable Consumer<M> initializer) {
        this.armorFactory = playerModelKey.armorFactory();
        this.body = playerModelKey.getKey(slimArms).createModel();
        if (initializer != null) {
            initializer.accept(this.body);
        }
    }

    public ModelWrapper(ModelKey<M> key) {
        this.armorFactory = null;
        this.body = key.createModel();
    }

    public M body() {
        return body;
    }

    public Optional<PonyArmourModel<T>> getArmourModel(ItemStack stack, ArmourLayer layer, ArmourVariant variant) {
        return ArmorModelRegistry.getModelKey(stack.getItem(), layer).or(() -> variant.getDefaultModel(layer).filter(l -> stack.getItem() instanceof ArmorItem))
                .map(key -> armor.computeIfAbsent(key, k -> {
            return armorFactory == null ? k.createModel() : k.createModel(armorFactory);
        }));
    }

    public ModelWrapper<T, M> applyMetadata(PonyData meta) {
        body.setMetadata(meta);
        armor.values().forEach(a -> a.setMetadata(meta));
        return this;
    }
}
