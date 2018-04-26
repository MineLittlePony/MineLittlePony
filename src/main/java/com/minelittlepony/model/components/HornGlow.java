package com.minelittlepony.model.components;

import net.minecraft.client.model.ModelBox;
import net.minecraft.client.model.PositionTextureVertex;
import net.minecraft.client.model.TexturedQuad;
import net.minecraft.client.renderer.BufferBuilder;

import javax.annotation.Nonnull;

import com.minelittlepony.render.HornGlowRenderer;

public class HornGlow extends ModelBox {

    private final float alpha;
    
    private final HornGlowRenderer parent;
    
    private TexturedQuad[] quadList;

    public HornGlow(HornGlowRenderer parent, int texU, int texV, float x, float y, float z, int w, int h, int d, float scale, float alpha) {
        super(parent, texU, texV, x, y, z, w, h, d, scale);
        
        this.parent = parent;
        this.alpha = alpha;
        
        this.quadList = new TexturedQuad[6];
        
        float x2 = x + w + scale;
        float y2 = y + h + scale;
        float z2 = z + d + scale;
        
        x -= scale;
        y -= scale;
        z -= scale;
        
        if (parent.mirror) {
            float f3 = x2;
            x2 = x;
            x = f3;
        }

        float halfpar4 = x + w * 0.05F;
        float halfpar6 = z + d * 0.05F;
        float halfvar11 = x + w * 0.95F;
        float halfvar13 = z + d * 0.95F;
        
        PositionTextureVertex p7 = new PositionTextureVertex(halfpar4, y, halfpar6, 0, 0);
        PositionTextureVertex p0 = new PositionTextureVertex(halfvar11, y, halfpar6, 0, 8);
        PositionTextureVertex p1 = new PositionTextureVertex(x2, y2, z, 8, 8);
        PositionTextureVertex p2 = new PositionTextureVertex(x, y2, z, 8, 0);
        PositionTextureVertex p3 = new PositionTextureVertex(halfpar4, y, halfvar13, 0, 0);
        PositionTextureVertex p4 = new PositionTextureVertex(halfvar11, y, halfvar13, 0, 8);
        PositionTextureVertex p5 = new PositionTextureVertex(x2, y2, z2, 8, 8);
        PositionTextureVertex p6 = new PositionTextureVertex(x, y2, z2, 8, 0);

        this.quadList[0] = new TexturedQuad(new PositionTextureVertex[]{p4, p0, p1, p5}, texU + d + w, texV + d, texU + d + w + d, texV + d + h, parent.textureWidth, parent.textureHeight);
        this.quadList[1] = new TexturedQuad(new PositionTextureVertex[]{p7, p3, p6, p2}, texU, texV + d, texU + d, texV + d + h, parent.textureWidth, parent.textureHeight);
        this.quadList[2] = new TexturedQuad(new PositionTextureVertex[]{p4, p3, p7, p0}, texU + d, texV, texU + d + w, texV + d, parent.textureWidth, parent.textureHeight);
        this.quadList[3] = new TexturedQuad(new PositionTextureVertex[]{p1, p2, p6, p5}, texU + d + w, texV + d, texU + d + w + w, texV, parent.textureWidth, parent.textureHeight);
        this.quadList[4] = new TexturedQuad(new PositionTextureVertex[]{p0, p7, p2, p1}, texU + d, texV + d, texU + d + w, texV + d + h, parent.textureWidth, parent.textureHeight);
        this.quadList[5] = new TexturedQuad(new PositionTextureVertex[]{p3, p4, p5, p6}, texU + d + w + d, texV + d, texU + d + w + d + w, texV + d + h, parent.textureWidth, parent.textureHeight);
        
        if (parent.mirror) {
            for (TexturedQuad i : quadList) {
                i.flipFace();
            }
        }
    }

    @Override
    public void render(@Nonnull BufferBuilder buffer, float scale) {
        parent.applyTint(alpha);
        
        for (TexturedQuad i : quadList) {
            i.draw(buffer, scale);
        }
    }
}
