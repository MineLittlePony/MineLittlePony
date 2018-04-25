package com.minelittlepony.render;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.client.model.TextureOffset;

@SuppressWarnings("unchecked")
public class BasePonyRenderer<T extends BasePonyRenderer<T>> extends ModelRenderer {

    protected final ModelBase baseModel;

    protected int textureOffsetX, textureOffsetY;

    public BasePonyRenderer(ModelBase model) {
        super(model);
        baseModel = model;
    }

    public BasePonyRenderer(ModelBase model, int x, int y) {
        super(model, x, y);
        baseModel = model;
    }

    @Override
    public T setTextureOffset(int x, int y) {
        this.textureOffsetX = x;
        this.textureOffsetY = y;
        super.setTextureOffset(x, y);
        return (T) this;
    }
    
    public T at(float x, float y, float z) {
        offsetX = x;
        offsetY = y;
        offsetZ = z;
        return (T) this;
    }

    public T around(float x, float y, float z) {
        setRotationPoint(x, y, z);
        return (T) this;
    }
    
    @Override
    public T addBox(String partName, float offX, float offY, float offZ, int width, int height, int depth) {
        partName = boxName + "." + partName;

        TextureOffset tex = this.baseModel.getTextureOffset(partName);

        setTextureOffset(tex.textureOffsetX, tex.textureOffsetY).addBox(offX, offY, offZ, width, height, depth);
        cubeList.get(cubeList.size() - 1).setBoxName(partName);

        return (T) this;
    }

    @Override
    public T addBox(float offX, float offY, float offZ, int width, int height, int depth) {
        addBox(offX, offY, offZ, width, height, depth, 0);
        return (T) this;
    }
}
