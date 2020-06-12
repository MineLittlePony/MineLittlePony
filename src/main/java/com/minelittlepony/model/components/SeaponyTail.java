package com.minelittlepony.model.components;

import com.minelittlepony.model.AbstractPonyModel;
import com.minelittlepony.model.capabilities.IModel;
import com.minelittlepony.model.capabilities.IModelPart;
import com.minelittlepony.render.model.PlaneRenderer;
import com.minelittlepony.render.model.PonyRenderer;

import java.util.UUID;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.math.MathHelper;

import org.lwjgl.opengl.GL11;

import static com.minelittlepony.model.PonyModelConstants.*;

public class SeaponyTail implements IModelPart {

    private static final float TAIL_ROTX = PI / 2;

    private PonyRenderer tailBase;

    private PlaneRenderer tailTip;
    private PlaneRenderer tailFins;

    private IModel model;

    public SeaponyTail(AbstractPonyModel model) {
        this.model = model;

        tailBase = new PonyRenderer(model, 0, 38);
        tailTip = new PlaneRenderer(model, 24, 0);
        tailFins = new PlaneRenderer(model, 56, 20);

        tailBase.addChild(tailTip);
        tailTip.addChild(tailFins);
    }

    @Override
    public void init(float yOffset, float stretch) {
        tailBase.rotate(TAIL_ROTX, 0, 0)
                .offset(0, -1, 0)
                .around(-2, 10, 8)
                .box( 0,  0, 0, 4, 6, 4, stretch)
                .flip();

        tailTip.rotate(0, 0, 0).around(1, 5, 1)
                .box(0, 0, 0, 2, 6, 1, stretch);

        tailFins.offset(1, 0, 4).rotate(-TAIL_ROTX, 0, 0)
                 .top(-8, 0, 0, 8, 8, stretch)
          .flip().top( 0, 0, 0, 8, 8, stretch);
    }

    @Override
    public void setRotationAndAngles(boolean rainboom, UUID interpolatorId, float move, float swing, float bodySwing, float ticks) {
        float rotation = model.isSleeping() ? 0 : MathHelper.sin(ticks * 0.536f) / 4;

        tailBase.rotateAngleX = TAIL_ROTX + rotation;
        tailTip.rotateAngleX = rotation;
        tailFins.rotateAngleX = rotation - TAIL_ROTX;
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
