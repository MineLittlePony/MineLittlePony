package com.minelittlepony.model;

import com.minelittlepony.model.armour.PonyArmor;
import com.minelittlepony.pony.data.IPonyData;

public class ModelWrapper {

    private final AbstractPonyModel model;
    private final PonyArmor armor;

    public ModelWrapper(AbstractPonyModel model) {
        this.model = model;
        this.armor = model.createArmour();
        this.armor.apply(model.metadata);
    }

    public AbstractPonyModel getModel() {
        return model;
    }

    public void init() {
        model.init(0, 0);
        armor.init();
    }

    public PonyArmor getArmor() {
        return armor;
    }

    public void apply(IPonyData meta) {
        model.metadata = meta;
        armor.apply(meta);
    }
}
