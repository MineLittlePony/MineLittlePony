package com.minelittlepony.api.model;

import net.minecraft.client.render.entity.equipment.EquipmentModel;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Util;

import com.minelittlepony.client.model.AbstractPonyModel;
import com.minelittlepony.client.model.armour.*;
import com.minelittlepony.mson.api.ModelKey;
import com.minelittlepony.mson.api.MsonModel;

import java.util.function.Function;

/**
 * Container class for the various models and their associated piece of armour.
 */
public record Models<M extends PonyModel<?>> (
        Function<ModelKey<AbstractPonyModel<?>>, AbstractPonyModel<?>> armor,
        M body
    ) {

    public Models(ModelKey<? super M> modelKey, MsonModel.Factory<AbstractPonyModel<?>> armorFactory) {
        this(Util.memoize(key -> key.createModel(armorFactory)), modelKey.createModel());
    }

    public Models(ModelKey<? super M> key) {
        this(Util.memoize(k -> k.createModel()), key.createModel());
    }

    public AbstractPonyModel<?> getArmourModel(ItemStack stack, EquipmentModel.LayerType layerType, ArmourVariant variant) {
        return armor.apply(ArmorModelRegistry.getModelKey(stack.getItem(), layerType, variant));
    }

    public Models<M> withArmorFactory(MsonModel.Factory<AbstractPonyModel<?>> armorFactory) {
        return new Models<>(Util.memoize(key -> key.createModel(armorFactory)), body);
    }
}
