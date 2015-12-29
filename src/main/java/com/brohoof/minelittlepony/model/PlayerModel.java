package com.brohoof.minelittlepony.model;

import com.brohoof.minelittlepony.PonyData;

public class PlayerModel {

    private final AbstractPonyModel model;
    private AbstractArmor armor;
    private float shadowsize = 0.5F;

    public PlayerModel(AbstractPonyModel model) {
        this.model = model;
    }

    public PlayerModel setTextureHeight(int height) {
        getModel().textureHeight = height;
        return this;
    }

    public AbstractPonyModel getModel() {
        return model;
    }

    public PlayerModel setArmor(AbstractArmor armor) {
        this.armor = armor;
        this.armor.apply(model.metadata);
        return this;
    }

    public PlayerModel setShadowsize(float shadowsize) {
        this.shadowsize = shadowsize;
        return this;
    }

    public float getShadowsize() {
        return shadowsize;
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
