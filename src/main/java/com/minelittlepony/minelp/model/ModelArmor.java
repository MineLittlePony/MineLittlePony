package com.minelittlepony.minelp.model;

public abstract class ModelArmor {
    public final String path;
    public ModelPony base;
    public ModelPony modelArmorChestplate;
    public ModelPony modelArmor;

    public ModelArmor(String path) {
        this.path = path;
    }

    public float layer() {
        return 1;
    };

    public int subimage(int slot) {
        return slot == 2 ? 2 : 1;
    }

}
