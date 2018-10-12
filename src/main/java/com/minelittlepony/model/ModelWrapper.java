package com.minelittlepony.model;

import com.minelittlepony.MineLittlePony;
import com.minelittlepony.model.armour.IEquestrianArmor;
import com.minelittlepony.model.capabilities.IModelWrapper;
import com.minelittlepony.pony.data.IPonyData;

/**
 * Container class for the various models and their associated piece of armour.
 */
public class ModelWrapper implements IModelWrapper {

    private final AbstractPonyModel body;

    private final IEquestrianArmor<?> armor;

    private int lastModelUpdate = 0;

    /**
     * Creates a new model wrapper to contain the given pony.
     */
    ModelWrapper(AbstractPonyModel model) {
        body = model;
        armor = model.createArmour();
        armor.apply(model.getMetadata());

        try {
            init();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public AbstractPonyModel getBody() {
        return body;
    }

    /**
     * Returns the contained armour model.
     */
    public IEquestrianArmor<?> getArmor() {
        return armor;
    }

    @Override
    public void apply(IPonyData meta) {
        int modelRevision = MineLittlePony.getModelRevisionNumber();

        if (modelRevision != lastModelUpdate) {
            lastModelUpdate = modelRevision;
            init();
        }

        body.metadata = meta;
        armor.apply(meta);
    }

    @Override
    public void init() {
        body.init(0, 0);
        armor.init();
    }
}
