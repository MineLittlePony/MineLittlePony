package com.minelittlepony.client.model;

import com.minelittlepony.common.MineLittlePony;
import com.minelittlepony.common.model.armour.IEquestrianArmour;
import com.minelittlepony.common.pony.IPonyData;
import com.minelittlepony.model.capabilities.IModelWrapper;

/**
 * Container class for the various models and their associated piece of armour.
 */
public class ModelWrapper implements IModelWrapper {

    private final AbstractPonyModel body;

    private final IEquestrianArmour<?> armor;

    private int lastModelUpdate = 0;

    /**
     * Creates a new model wrapper to contain the given pony.
     */
    public ModelWrapper(AbstractPonyModel model) {
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
    public IEquestrianArmour<?> getArmor() {
        return armor;
    }

    @Override
    public void apply(IPonyData meta) {
        int modelRevision = MineLittlePony.getInstance().getModelRevisionNumber();

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
