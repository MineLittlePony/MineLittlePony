package com.minelittlepony.minelp.model;

import com.minelittlepony.minelp.renderer.ScalableModelRenderer;
import net.minecraft.client.model.PositionTextureVertex;
import net.minecraft.client.model.TexturedQuad;
import net.minecraft.client.renderer.Tessellator;

public class ScalableModelBox {
   private PositionTextureVertex[] vertexPositions;
   private TexturedQuad[] quadList;
   public final float posX1;
   public final float posY1;
   public final float posZ1;
   public final float posX2;
   public final float posY2;
   public final float posZ2;
   public String field_78247_g;

   public ScalableModelBox(ScalableModelRenderer par1ModelRenderer, int par2, int par3, float par4, float par5, float par6, int par7, int par8, int par9, float par10) {
      this.posX1 = par4;
      this.posY1 = par5;
      this.posZ1 = par6;
      this.posX2 = par4 + par7;
      this.posY2 = par5 + par8;
      this.posZ2 = par6 + par9;
      this.vertexPositions = new PositionTextureVertex[8];
      this.quadList = new TexturedQuad[6];
      float var11 = par4 + par7;
      float var12 = par5 + par8;
      float var13 = par6 + par9;
      par4 -= par10;
      par5 -= par10;
      par6 -= par10;
      var11 += par10;
      var12 += par10;
      var13 += par10;
      if(par1ModelRenderer.mirror) {
         float var23 = var11;
         var11 = par4;
         par4 = var23;
      }

      PositionTextureVertex var231 = new PositionTextureVertex(par4, par5, par6, 0.0F, 0.0F);
      PositionTextureVertex var15 = new PositionTextureVertex(var11, par5, par6, 0.0F, 8.0F);
      PositionTextureVertex var16 = new PositionTextureVertex(var11, var12, par6, 8.0F, 8.0F);
      PositionTextureVertex var17 = new PositionTextureVertex(par4, var12, par6, 8.0F, 0.0F);
      PositionTextureVertex var18 = new PositionTextureVertex(par4, par5, var13, 0.0F, 0.0F);
      PositionTextureVertex var19 = new PositionTextureVertex(var11, par5, var13, 0.0F, 8.0F);
      PositionTextureVertex var20 = new PositionTextureVertex(var11, var12, var13, 8.0F, 8.0F);
      PositionTextureVertex var21 = new PositionTextureVertex(par4, var12, var13, 8.0F, 0.0F);
      this.vertexPositions[0] = var231;
      this.vertexPositions[1] = var15;
      this.vertexPositions[2] = var16;
      this.vertexPositions[3] = var17;
      this.vertexPositions[4] = var18;
      this.vertexPositions[5] = var19;
      this.vertexPositions[6] = var20;
      this.vertexPositions[7] = var21;
      this.quadList[0] = new TexturedQuad(new PositionTextureVertex[]{var19, var15, var16, var20}, par2 + par9 + par7, par3 + par9, par2 + par9 + par7 + par9, par3 + par9 + par8, par1ModelRenderer.textureWidth, par1ModelRenderer.textureHeight);
      this.quadList[1] = new TexturedQuad(new PositionTextureVertex[]{var231, var18, var21, var17}, par2, par3 + par9, par2 + par9, par3 + par9 + par8, par1ModelRenderer.textureWidth, par1ModelRenderer.textureHeight);
      this.quadList[2] = new TexturedQuad(new PositionTextureVertex[]{var19, var18, var231, var15}, par2 + par9, par3, par2 + par9 + par7, par3 + par9, par1ModelRenderer.textureWidth, par1ModelRenderer.textureHeight);
      this.quadList[3] = new TexturedQuad(new PositionTextureVertex[]{var16, var17, var21, var20}, par2 + par9 + par7, par3 + par9, par2 + par9 + par7 + par7, par3, par1ModelRenderer.textureWidth, par1ModelRenderer.textureHeight);
      this.quadList[4] = new TexturedQuad(new PositionTextureVertex[]{var15, var231, var17, var16}, par2 + par9, par3 + par9, par2 + par9 + par7, par3 + par9 + par8, par1ModelRenderer.textureWidth, par1ModelRenderer.textureHeight);
      this.quadList[5] = new TexturedQuad(new PositionTextureVertex[]{var18, var19, var20, var21}, par2 + par9 + par7 + par9, par3 + par9, par2 + par9 + par7 + par9 + par7, par3 + par9 + par8, par1ModelRenderer.textureWidth, par1ModelRenderer.textureHeight);
      if(par1ModelRenderer.mirror) {
         for (TexturedQuad element : this.quadList) {
            element.flipFace();
         }
      }

   }

   public void render(Tessellator par1Tessellator, float par2) {
      for (TexturedQuad element : this.quadList) {
         element.draw(par1Tessellator.getWorldRenderer(), par2);
      }

   }

   public ScalableModelBox func_78244_a(String par1Str) {
      this.field_78247_g = par1Str;
      return this;
   }
}
