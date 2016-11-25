package com.minelittlepony.model;

import com.minelittlepony.PonyData;
import com.minelittlepony.model.AbstractPonyModel;
import com.minelittlepony.model.PonyModelConstants;
import com.minelittlepony.renderer.HornGlowRenderer;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelBiped.ArmPose;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;
import org.lwjgl.opengl.GL11;

import static net.minecraft.client.renderer.GlStateManager.*;

public class UnicornHorn extends ModelBase implements PonyModelConstants {

    protected final AbstractPonyModel pony;
    private ModelRenderer horn;
    private HornGlowRenderer[] hornglow;

    public UnicornHorn(AbstractPonyModel pony, float yOffset, float stretch) {
        this.pony = pony;

        this.horn = new ModelRenderer(pony, 0, 3);
        this.hornglow = new HornGlowRenderer[2];
        for (int i = 0; i < hornglow.length; i++) {
            this.hornglow[i] = new HornGlowRenderer(pony, 0, 3);
        }

        this.horn.addBox(-0.5F + HEAD_CENTRE_X, -10.0F + HEAD_CENTRE_Y, -1.5F + HEAD_CENTRE_Z, 1, 4, 1, stretch);
        this.horn.setRotationPoint(HEAD_RP_X, HEAD_RP_Y + yOffset, HEAD_RP_Z);
        this.horn.rotateAngleX = 0.5F;

        this.hornglow[0].addBox(-0.5F + HEAD_CENTRE_X, -10.0F + HEAD_CENTRE_Y, -1.5F + HEAD_CENTRE_Z, 1, 4, 1, stretch + 0.5F);
        this.hornglow[1].addBox(-0.5F + HEAD_CENTRE_X, -10.0F + HEAD_CENTRE_Y, -1.5F + HEAD_CENTRE_Z, 1, 3, 1, stretch + 0.8F);
    }

    @Override
    public void render(Entity entityIn, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, float scale) {

        PonyData data = pony.metadata;

        if (data.getRace() != null && data.getRace().hasHorn()) {
            this.horn.render(scale);
            if ((pony.leftArmPose != ArmPose.EMPTY || pony.rightArmPose != ArmPose.EMPTY) && data.hasMagic()) {
                GL11.glPushAttrib(24577);
                disableTexture2D();
                disableLighting();
                enableBlend();

                float red = (data.getGlowColor() >> 16 & 255) / 255.0F;
                float green = (data.getGlowColor() >> 8 & 255) / 255.0F;
                float blue = (data.getGlowColor() & 255) / 255.0F;
                blendFunc(GL11.GL_SRC_ALPHA, 1);

                this.horn.postRender(scale);

                color(red, green, blue, 0.4F);
                this.hornglow[0].render(scale);
                color(red, green, blue, 0.2F);
                this.hornglow[1].render(scale);

                enableTexture2D();
                enableLighting();
                disableBlend();
                popAttrib();
            }
        }
    }

}
