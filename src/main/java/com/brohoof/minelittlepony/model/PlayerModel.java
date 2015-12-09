package com.brohoof.minelittlepony.model;

import com.brohoof.minelittlepony.PonyData;

public class PlayerModel {

    private final ModelPony model;
    private ModelArmor armor;
    private float shadowsize = 0.5F;

    public PlayerModel(ModelPony model) {
        this.model = model;
    }

    public PlayerModel setTextureHeight(int height) {
        getModel().textureHeight = height;
        return this;
    }

    public ModelPony getModel() {
        return model;
    }

    public PlayerModel setArmor(ModelArmor armor) {
        this.armor = armor;
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

    public ModelArmor getArmor() {
        return armor;
    }
    
    public void apply(PonyData meta) {
        model.metadata = meta;
        armor.apply(meta);
    }
}
