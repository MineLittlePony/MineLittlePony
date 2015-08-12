package com.minelittlepony.minelp.model;

import com.minelittlepony.minelp.model.ModelArmor;
import com.minelittlepony.minelp.model.ModelPony;
import java.text.DecimalFormat;

public class PlayerModel {
    public final int id;
    public String name;
    public String url;
    public ModelPony model;
    public ModelArmor armor;
    public float width = 0.6F;
    public float height = 1.8F;
    public float shadowsize = 0.5F;
    public float thirdpersondistance = 4.0F;
    public float yoffset = 1.62F;
    public float globalscale = 1.0F;

    public PlayerModel(String name, ModelPony model, int manual_id) {
        this.name = name;
        this.model = model;
        this.id = manual_id;
    }

    public PlayerModel setTextureHeight(int height) {
        model.textureHeight = height;
        return this;
    }

    public PlayerModel setArmor(ModelArmor armor) {
        this.armor = armor;
        return this;
    }

    public PlayerModel setURL(String url) {
        this.url = url;
        return this;
    }

    public PlayerModel setShadow(float size) {
        this.shadowsize = size;
        return this;
    }

    public PlayerModel setSize(float width, float height) {
        this.width = width;
        this.height = height;
        return this;
    }

    public PlayerModel setOffset(float offset) {
        this.yoffset = offset;
        return this;
    }

    public PlayerModel setDistance(float distance) {
        this.thirdpersondistance = distance;
        return this;
    }

    public PlayerModel setScale(float scale) {
        this.globalscale = scale;
        return this;
    }

    public boolean hasArmor() {
        return this.armor != null && this.armor.base != null && this.armor.path != null;
    }

    public String getSize(DecimalFormat df) {
        return df.format(this.width) + " * " + df.format(this.height) + " * " + df.format(this.width);
    }

    public void init() {
        model.init(0, 0);
        armor.modelArmorChestplate.init(0.0F, 1.0F);
        armor.modelArmor.init(0.0F, 0.5F);
    }
}
