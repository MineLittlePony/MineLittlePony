package com.minelittlepony.model.components;

import com.minelittlepony.model.AbstractPonyModel;
import com.minelittlepony.renderer.HornGlowRenderer;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;

import static org.lwjgl.opengl.GL11.*;
import static net.minecraft.client.renderer.GlStateManager.*;
import static com.minelittlepony.model.PonyModelConstants.*;

public class UnicornHorn extends ModelBase {
    static final float
            hornX = HEAD_CENTRE_X - 0.5F,
            hornY = HEAD_CENTRE_Y - 10,
            hornZ = HEAD_CENTRE_Z - 1.5F;

    protected final AbstractPonyModel pony;

    private ModelRenderer horn;

    private HornGlowRenderer glow;

    private boolean usingMagic;

    public UnicornHorn(AbstractPonyModel pony, float yOffset, float stretch) {
        this.pony = pony;

        horn = new ModelRenderer(pony, 0, 3);
        glow = new HornGlowRenderer(pony, 0, 3);
        
        horn.addBox(hornX, hornY, hornZ, 1, 4, 1, stretch);
        horn.setRotationPoint(HEAD_RP_X, HEAD_RP_Y + yOffset, HEAD_RP_Z);
        horn.rotateAngleX = 0.5F;

        glow.setAlpha(0.4f).addBox(hornX, hornY, hornZ, 1, 4, 1, stretch + 0.5F);
        glow.setAlpha(0.2f).addBox(hornX, hornY, hornZ, 1, 3, 1, stretch + 0.8F);
    }

    @Override
    public void render(Entity entityIn, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, float scale) {
        if (!pony.metadata.getRace().hasHorn()) return;
        
        this.horn.render(scale);
        
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
