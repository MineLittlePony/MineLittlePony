package com.minelittlepony.client.model;

import net.minecraft.entity.LivingEntity;

import com.minelittlepony.api.model.IModel;
import com.minelittlepony.api.model.IModelWrapper;
import com.minelittlepony.api.model.armour.IArmourModel;
import com.minelittlepony.api.model.armour.IArmour;
import com.minelittlepony.api.pony.IPonyData;
import com.minelittlepony.mson.api.ModelKey;

/**
 * Container class for the various models and their associated piece of armour.
 */
public class ModelWrapper<T extends LivingEntity, M extends IModel> implements IModelWrapper {

    private final M body;

    private final IArmour<?> armor;

    /**
     * Creates a new model wrapper to contain the given pony.
     */
    @SuppressWarnings("unchecked")
    public ModelWrapper(ModelKey<?> key) {
        body = (M)key.createModel();
        armor = body.createArmour();
        armor.applyMetadata(body.getMetadata());
    }

    public M getBody() {
        return body;
    }

    /**
     * Returns the contained armour models.
     */
    @SuppressWarnings("unchecked")
    public <V extends IArmourModel> IArmour<V> getArmor() {
        return (IArmour<V>)armor;
    }

    @Override
    public ModelWrapper<T, M> applyMetadata(IPonyData meta) {
        body.setMetadata(meta);
        armor.applyMetadata(meta);
        return this;
    }
}
