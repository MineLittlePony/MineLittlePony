package com.minelittlepony.model;

import com.minelittlepony.renderer.HornGlowRenderer;
import net.minecraft.client.model.ModelBox;
import net.minecraft.client.model.PositionTextureVertex;
import net.minecraft.client.model.TexturedQuad;
import net.minecraft.client.renderer.BufferBuilder;

import javax.annotation.Nonnull;

public class ModelHornGlow extends ModelBox {

    private TexturedQuad[] quadList;

    public ModelHornGlow(HornGlowRenderer par1ModelRenderer, int par2, int par3, float par4, float par5, float par6, int par7, int par8, int par9, float par10) {
        super(par1ModelRenderer, par2, par3, par4, par5, par6, par7, par8, par9, par10);

        this.quadList = new TexturedQuad[6];
        float var11 = par4 + par7;
        float var12 = par5 + par8;
        float var13 = par6 + par9;
        float halfpar4 = par4 + par7 * 0.05F;
        float halfpar6 = par6 + par9 * 0.05F;
        float halfvar11 = par4 + par7 * 0.95F;
        float halfvar13 = par6 + par9 * 0.95F;
        par4 -= par10;
        par5 -= par10;
        par6 -= par10;
        var11 += par10;
        var12 += par10;
        var13 += par10;
        if (par1ModelRenderer.mirror) {
            float var26 = var11;
            var11 = par4;
            par4 = var26;
        }

        PositionTextureVertex var32 = new PositionTextureVertex(halfpar4, par5, halfpar6, 0.0F, 0.0F);
        PositionTextureVertex var15 = new PositionTextureVertex(halfvar11, par5, halfpar6, 0.0F, 8.0F);
        PositionTextureVertex var16 = new PositionTextureVertex(var11, var12, par6, 8.0F, 8.0F);
        PositionTextureVertex var17 = new PositionTextureVertex(par4, var12, par6, 8.0F, 0.0F);
        PositionTextureVertex var18 = new PositionTextureVertex(halfpar4, par5, halfvar13, 0.0F, 0.0F);
        PositionTextureVertex var19 = new PositionTextureVertex(halfvar11, par5, halfvar13, 0.0F, 8.0F);
        PositionTextureVertex var20 = new PositionTextureVertex(var11, var12, var13, 8.0F, 8.0F);
        PositionTextureVertex var21 = new PositionTextureVertex(par4, var12, var13, 8.0F, 0.0F);

        this.quadList[0] = new TexturedQuad(new PositionTextureVertex[]{var19, var15, var16, var20},
                par2 + par9 + par7, par3 + par9, par2 + par9 + par7 + par9, par3 + par9 + par8,
                par1ModelRenderer.textureWidth, par1ModelRenderer.textureHeight);
        this.quadList[1] = new TexturedQuad(new PositionTextureVertex[]{var32, var18, var21, var17}, par2,
                par3 + par9, par2 + par9, par3 + par9 + par8, par1ModelRenderer.textureWidth,
                par1ModelRenderer.textureHeight);
        this.quadList[2] = new TexturedQuad(new PositionTextureVertex[]{var19, var18, var32, var15}, par2 + par9,
                par3, par2 + par9 + par7, par3 + par9, par1ModelRenderer.textureWidth, par1ModelRenderer.textureHeight);
        this.quadList[3] = new TexturedQuad(new PositionTextureVertex[]{var16, var17, var21, var20},
                par2 + par9 + par7, par3 + par9, par2 + par9 + par7 + par7, par3, par1ModelRenderer.textureWidth,
                par1ModelRenderer.textureHeight);
        this.quadList[4] = new TexturedQuad(new PositionTextureVertex[]{var15, var32, var17, var16}, par2 + par9,
                par3 + par9, par2 + par9 + par7, par3 + par9 + par8, par1ModelRenderer.textureWidth,
                par1ModelRenderer.textureHeight);
        this.quadList[5] = new TexturedQuad(new PositionTextureVertex[]{var18, var19, var20, var21},
                par2 + par9 + par7 + par9, par3 + par9, par2 + par9 + par7 + par9 + par7, par3 + par9 + par8,
                par1ModelRenderer.textureWidth, par1ModelRenderer.textureHeight);
        if (par1ModelRenderer.mirror) {
            TexturedQuad[] var22 = this.quadList;

            for (TexturedQuad var25 : var22) {
                var25.flipFace();
            }
        }

    }

    @Override
    public void render(@Nonnull BufferBuilder buffer, float par2) {
        TexturedQuad[] var3 = this.quadList;
        for (TexturedQuad var6 : var3) {
            var6.draw(buffer, par2);
        }

    }
}
