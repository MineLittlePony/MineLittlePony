package com.brohoof.minelittlepony.model.part;

import static net.minecraft.client.renderer.GlStateManager.*;

import org.lwjgl.opengl.GL11;

import com.brohoof.minelittlepony.PonyData;
import com.brohoof.minelittlepony.model.AbstractPonyModel;
import com.brohoof.minelittlepony.model.PonyModelConstants;
import com.brohoof.minelittlepony.renderer.HornGlowRenderer;

import net.minecraft.client.model.ModelRenderer;

public class UnicornHorn extends AbstractHeadPart implements PonyModelConstants {

    private ModelRenderer horn;
    private HornGlowRenderer[] hornglow;

    @Override
    public void init(AbstractPonyModel pony, float yOffset, float stretch) {
        super.init(pony, yOffset, stretch);

        this.horn = new ModelRenderer(pony, 0, 3);
        this.hornglow = new HornGlowRenderer[2];
        for (int i = 0; i < hornglow.length; i++) {
            this.hornglow[i] = new HornGlowRenderer(pony, 0, 3);
        }

        this.horn.addBox(-0.5F + HEAD_CENTRE_X, -10.0F + HEAD_CENTRE_Y, -1.5F + HEAD_CENTRE_Z, 1, 4, 1, stretch);
        this.horn.setRotationPoint(HEAD_RP_X, HEAD_RP_Y + yOffset, HEAD_RP_Z);

        this.hornglow[0].addBox(-0.5F + HEAD_CENTRE_X, -10.0F + HEAD_CENTRE_Y, -1.5F + HEAD_CENTRE_Z, 1, 4, 1, stretch + 0.5F);
        this.hornglow[0].setRotationPoint(HEAD_RP_X, HEAD_RP_Y + yOffset, HEAD_RP_Z);
        this.hornglow[1].addBox(-0.5F + HEAD_CENTRE_X, -10.0F + HEAD_CENTRE_Y, -1.5F + HEAD_CENTRE_Z, 1, 3, 1, stretch + 0.8F);
        this.hornglow[1].setRotationPoint(HEAD_RP_X, HEAD_RP_Y + yOffset, HEAD_RP_Z);
    }

    @Override
    public void render(PonyData data, float scale) {
        super.render(data, scale);
        if (data.getRace() != null && data.getRace().hasHorn()) {
            this.horn.render(scale);
            if (getPony().heldItemRight != 0 && data.getGlowColor() != 0) {
                GL11.glPushAttrib(24577);
                disableTexture2D();
                disableLighting();
                enableBlend();

                float var4 = (data.getGlowColor() >> 16 & 255) / 255.0F;
                float green = (data.getGlowColor() >> 8 & 255) / 255.0F;
                float blue = (data.getGlowColor() & 255) / 255.0F;
                blendFunc(GL11.GL_SRC_ALPHA, 1);

                color(var4, green, blue, 0.4F);
                this.hornglow[0].render(scale);
                color(var4, green, blue, 0.2F);
                this.hornglow[1].render(scale);

                enableTexture2D();
                enableLighting();
                disableBlend();
                popAttrib();
            }
        }
    }

    protected void position(float posX, float posY, float posZ) {
        AbstractPonyModel.setRotationPoint(this.horn, posX, posY, posZ);
        for (int i = 0; i < this.hornglow.length; i++) {
            AbstractPonyModel.setRotationPoint(this.hornglow[i], posX, posY, posZ);
        }
    }

    protected void rotate(float rotX, float rotY) {

        this.horn.rotateAngleX = rotX + 0.5F;
        this.horn.rotateAngleY = rotY;

        for (int i = 0; i < this.hornglow.length; i++) {
            this.hornglow[i].rotateAngleX = rotX + 0.5F;
            this.hornglow[i].rotateAngleY = rotY;
        }
    }

}
