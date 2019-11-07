package com.minelittlepony.client.model.components;

import com.minelittlepony.client.model.AbstractPonyModel;
import com.minelittlepony.client.model.IPonyModel;
import com.minelittlepony.client.util.render.Part;
import com.minelittlepony.model.IPart;
import com.mojang.blaze3d.platform.GlStateManager;

import java.util.UUID;

import net.minecraft.util.math.MathHelper;

import org.lwjgl.opengl.GL11;

public class SeaponyTail implements IPart {

    private static final float TAIL_ROTX = PI / 2;

    private Part tailBase;

    private Part tailTip;
    private Part tailFins;

    private IPonyModel<?> model;

    public SeaponyTail(AbstractPonyModel<?> model) {
        this.model = model;

        tailBase = new Part(model, 0, 38);
        tailTip = new Part(model, 24, 0);
        tailFins = new Part(model, 56, 20);

        tailBase.addChild(tailTip);
        tailTip.addChild(tailFins);
    }

    @Override
    public void init(float yOffset, float stretch) {
        tailBase.rotate(TAIL_ROTX, 0, 0)
                .offset(0, -4, -2)
                .around(-2, 10, 8)
                .box(0,  4, 2, 4, 6, 4, stretch)
                .flip();

        tailTip.rotate(0, 0, 0).around(1, 5, 1)
                .box(0, 0, 0, 2, 6, 1, stretch);

        tailFins.offset(1, 0, 4).rotate(-TAIL_ROTX, 0, 0)
                 .top(-8, 0, 0, 8, 8, stretch)
          .flip().top( 0, 0, 0, 8, 8, stretch);
    }

    @Override
    public void setRotationAndAngles(boolean rainboom, UUID interpolatorId, float move, float swing, float bodySwing, float ticks) {
        float rotation = model.getAttributes().isSleeping ? 0 : MathHelper.sin(ticks * 0.536f) / 4;

        tailBase.pitch = TAIL_ROTX + rotation;
        tailTip.pitch = rotation;
        tailFins.pitch = rotation - TAIL_ROTX;
    }

    @Override
    public void renderPart(float scale, UUID interpolatorId) {
        GL11.glPushAttrib(GL11.GL_ALL_ATTRIB_BITS);
        GlStateManager.enableBlend();
        tailBase.render(scale);
        GlStateManager.disableBlend();
        GL11.glPopAttrib();
    }

}
