package com.minelittlepony.client.model;

import net.minecraft.entity.LivingEntity;

import com.minelittlepony.api.pony.IPonyData;
import com.minelittlepony.model.IModel;
import com.minelittlepony.model.IModelWrapper;
import com.minelittlepony.model.armour.IArmour;
import com.minelittlepony.model.armour.IEquestrianArmour;
import com.minelittlepony.mson.api.ModelKey;

/**
 * Container class for the various models and their associated piece of armour.
 */
public class ModelWrapper<T extends LivingEntity, M extends IModel> implements IModelWrapper {

    private final M body;

    private final IEquestrianArmour<?> armor;

    /**
     * Creates a new model wrapper to contain the given pony.
     */
    @SuppressWarnings("unchecked")
    public ModelWrapper(ModelKey<?> key) {
        body = (M)key.createModel();
        armor = body.createArmour();
        armor.apply(body.getMetadata());
    }

    public M getBody() {
        return body;
    }

    /**
     * Returns the contained armour model.
     */
    @SuppressWarnings("unchecked")
    public <V extends IArmour> IEquestrianArmour<V> getArmor() {
        return (IEquestrianArmour<V>)armor;
    }

    @Override
    public ModelWrapper<T, M> apply(IPonyData meta) {
        body.apply(meta);
        armor.apply(meta);
        return this;
    }
}
