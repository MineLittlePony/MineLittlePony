package com.minelittlepony.api.model;

import net.minecraft.client.resources.model.EquipmentClientInfo;
import net.minecraft.util.Util;
import net.minecraft.world.item.ItemStack;

import com.minelittlepony.api.model.armour.ArmorModelRegistry;
import com.minelittlepony.api.model.armour.ArmourVariant;
import com.minelittlepony.client.model.ClientPonyModel;
import com.minelittlepony.mson.api.ModelKey;
import com.minelittlepony.mson.api.MsonModel;

import java.util.function.Function;

/**
 * Container class for the various models and their associated piece of armour.
 */
public record Models<M extends PonyModel<?>> (
        Function<ModelKey<ClientPonyModel<?>>, ClientPonyModel<?>> armor,
        M body
    ) {

    public Models(ModelKey<? super M> modelKey, MsonModel.Factory<ClientPonyModel<?>> armorFactory) {
        this(Util.memoize(key -> key.createModel(armorFactory)), modelKey.createModel());
    }

    public Models(ModelKey<? super M> key) {
        this(Util.memoize(k -> k.createModel()), key.createModel());
    }

    public ClientPonyModel<?> getArmourModel(ItemStack stack, EquipmentClientInfo.LayerType layerType, ArmourVariant variant) {
        return armor.apply(ArmorModelRegistry.getModelKey(stack.getItem(), layerType, variant));
    }

    public Models<M> withArmorFactory(MsonModel.Factory<ClientPonyModel<?>> armorFactory) {
        return new Models<>(Util.memoize(key -> key.createModel(armorFactory)), body);
    }
}
