package com.minelittlepony.model;

import com.minelittlepony.model.armour.PonyArmor;
import com.minelittlepony.pony.data.IPonyData;

/**
 * Container class for the various models and their associated piece of armour.
 */
public class ModelWrapper {

    private final AbstractPonyModel model;
    private final PonyArmor armor;

    /**
     * Created a new model wrapper to contain the given pony.
     */
    public ModelWrapper(AbstractPonyModel model) {
        this.model = model;
        this.armor = model.createArmour();
        this.armor.apply(model.metadata);
    }

    public AbstractPonyModel getModel() {
        return model;
    }

    /**
     * Returns the contained armour model.
     * @return
     */
    public PonyArmor getArmor() {
        return armor;
    }

    /**
     * Updates metadata values on this wrapper's armour and model.
     */
    public void apply(IPonyData meta) {
        model.metadata = meta;
        armor.apply(meta);
    }
    
    /**
     * Called at startup to configure a model's needed components.
     */
    public void init() {
        model.init(0, 0);
        armor.init();
    }
}
