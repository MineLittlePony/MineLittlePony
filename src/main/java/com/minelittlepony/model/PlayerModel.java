package com.minelittlepony.model;

import com.minelittlepony.PonyData;

public class PlayerModel {

    private final AbstractPonyModel model;
    private AbstractArmor armor;

    public PlayerModel(AbstractPonyModel model) {
        this.model = model;
    }

    public AbstractPonyModel getModel() {
        return model;
    }

    public PlayerModel setArmor(AbstractArmor armor) {
        this.armor = armor;
        this.armor.apply(model.metadata);
        return this;
    }

    public void init() {
        getModel().init(0, 0);
        getArmor().modelArmorChestplate.init(0.0F, 1.0F);
        getArmor().modelArmor.init(0.0F, 0.5F);
    }

    public AbstractArmor getArmor() {
        return armor;
    }

    public void apply(PonyData meta) {
        model.metadata = meta;
        armor.apply(meta);
    }
}
