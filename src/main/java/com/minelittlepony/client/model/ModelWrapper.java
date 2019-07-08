package com.minelittlepony.client.model;

import net.minecraft.entity.LivingEntity;

import com.minelittlepony.client.MineLittlePony;
import com.minelittlepony.model.IModel;
import com.minelittlepony.model.armour.IArmour;
import com.minelittlepony.model.armour.IEquestrianArmour;
import com.minelittlepony.model.capabilities.IModelWrapper;
import com.minelittlepony.pony.IPonyData;

/**
 * Container class for the various models and their associated piece of armour.
 */
public class ModelWrapper<T extends LivingEntity, M extends IModel> implements IModelWrapper {

    private final M body;

    private final IEquestrianArmour<?> armor;

    private int lastModelUpdate = 0;

    /**
     * Creates a new model wrapper to contain the given pony.
     */
    public ModelWrapper(M model) {
        body = model;
        armor = model.createArmour();
        armor.apply(model.getMetadata());

        try {
            init();
        } catch (Exception e) {
            e.printStackTrace();
        }
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
    public void apply(IPonyData meta) {
        int modelRevision = MineLittlePony.getInstance().getModelRevisionNumber();

        if (modelRevision != lastModelUpdate) {
            lastModelUpdate = modelRevision;
            init();
        }

        body.apply(meta);
        armor.apply(meta);
    }

    @Override
    public void init() {
        body.init(0, 0);
        armor.init();
    }
}
