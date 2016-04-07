package com.brohoof.minelittlepony.renderer;

import com.brohoof.minelittlepony.model.ModelHornGlow;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.client.model.TextureOffset;

public class HornGlowRenderer extends ModelRenderer {

    private int textureOffsetX;
    private int textureOffsetY;
    private ModelBase baseModel;

    public HornGlowRenderer(ModelBase par1ModelBase, String par2Str) {
        super(par1ModelBase, par2Str);
        this.baseModel = par1ModelBase;
    }

    public HornGlowRenderer(ModelBase par1ModelBase) {
        this(par1ModelBase, null);
    }

    public HornGlowRenderer(ModelBase par1ModelBase, int par2, int par3) {
        this(par1ModelBase);
        this.setTextureSize(par2, par3);

    }

    @Override
    public HornGlowRenderer addBox(String par1Str, float par2, float par3, float par4, int par5, int par6, int par7) {
        par1Str = this.boxName + "." + par1Str;
        TextureOffset var8 = this.baseModel.getTextureOffset(par1Str);
        this.setTextureOffset(var8.textureOffsetX, var8.textureOffsetY);
        this.cubeList.add((new ModelHornGlow(this, this.textureOffsetX, this.textureOffsetY, par2, par3, par4, par5, par6, par7, 0.0F)).setBoxName(par1Str));
        return this;
    }

    @Override
    public HornGlowRenderer addBox(float par1, float par2, float par3, int par4, int par5, int par6) {
        this.cubeList.add(new ModelHornGlow(this, this.textureOffsetX, this.textureOffsetY, par1, par2, par3, par4, par5, par6, 0.0F));
        return this;
    }

    @Override
    public void addBox(float par1, float par2, float par3, int par4, int par5, int par6, float par7) {
        this.cubeList.add(new ModelHornGlow(this, this.textureOffsetX, this.textureOffsetY, par1, par2, par3, par4, par5, par6, par7));
    }

    @Override
    public ModelRenderer setTextureOffset(int x, int y) {
        this.textureOffsetX = x;
        this.textureOffsetY = y;
        return this;
    }
}
