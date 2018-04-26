package com.minelittlepony.model.components;

import com.minelittlepony.model.AbstractPonyModel;
import com.minelittlepony.render.HornGlowRenderer;
import com.minelittlepony.render.PonyRenderer;

import net.minecraft.client.model.ModelBase;
import net.minecraft.entity.Entity;

import static org.lwjgl.opengl.GL11.*;
import static net.minecraft.client.renderer.GlStateManager.*;
import static com.minelittlepony.model.PonyModelConstants.*;

public class UnicornHorn extends ModelBase {

    protected final AbstractPonyModel pony;

    private PonyRenderer horn;
    private HornGlowRenderer glow;

    private boolean usingMagic;

    public UnicornHorn(AbstractPonyModel pony, float yOffset, float stretch) {
        this.pony = pony;

        horn = new PonyRenderer(pony, 0, 3);
        glow = new HornGlowRenderer(pony, 0, 3);
        
        horn.offset(HORN_X, HORN_Y, HORN_Z)
            .around(HEAD_RP_X, HEAD_RP_Y + yOffset, HEAD_RP_Z)
            .box(0, 0, 0, 1, 4, 1, stretch)
            .rotateAngleX = 0.5F;

        glow.offset(HORN_X, HORN_Y, HORN_Z)
            .around(HEAD_RP_X, HEAD_RP_Y + yOffset, HEAD_RP_Z)
            .setAlpha(0.4f).box(0, 0, 0, 1, 4, 1, stretch + 0.5F)
            .setAlpha(0.2f).box(0, 0, 0, 1, 3, 1, stretch + 0.8F);
    }

    @Override
    public void render(Entity entityIn, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, float scale) {
        if (!pony.metadata.getRace().hasHorn()) return;
        
        horn.render(scale);
        
        if (usingMagic && pony.metadata.hasMagic()) {
            renderMagic(pony.metadata.getGlowColor(), scale);
        }
    }
    
    private void renderMagic(int tint, float scale) {
        glPushAttrib(24577);
        disableTexture2D();
        disableLighting();
        enableBlend();
        blendFunc(GL_SRC_ALPHA, GL_ONE);

        horn.postRender(scale);
        glow.setTint(tint).render(scale);

        enableTexture2D();
        enableLighting();
        disableBlend();
        popAttrib();
    }

    public void setUsingMagic(boolean usingMagic) {
        this.usingMagic = usingMagic;
    }
}
