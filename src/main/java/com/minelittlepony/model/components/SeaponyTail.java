package com.minelittlepony.model.components;

import com.minelittlepony.model.AbstractPonyModel;
import com.minelittlepony.model.capabilities.IModelPart;
import com.minelittlepony.render.PonyRenderer;
import com.minelittlepony.render.plane.PlaneRenderer;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.math.MathHelper;

import static com.minelittlepony.model.PonyModelConstants.*;

public class SeaponyTail implements IModelPart {

    private static final float TAIL_ROTX = PI / 2;

    private PonyRenderer tailBase;

    private PlaneRenderer tailTip;
    private PlaneRenderer tailFins;

    public SeaponyTail(AbstractPonyModel model) {
        tailBase = new PonyRenderer(model, 0, 38);
        tailTip = new PlaneRenderer(model, 24, 0);
        tailFins = new PlaneRenderer(model, 56, 20);

        tailBase.addChild(tailTip);
        tailTip.addChild(tailFins);
    }

    @Override
    public void init(float yOffset, float stretch) {
        tailBase.rotate(TAIL_ROTX, 0, 0).around(-2, 14, 8)
                .box(0, 0, 0, 4, 6, 4, stretch).flip();

        tailTip.rotate(0, 0, 0).around(1, 5, 1)
                .box(0, 0, 0, 2, 6, 1, stretch);

        tailFins.offset(1, 0, 4).rotate(-TAIL_ROTX, 0, 0)
                .addTopPlane(-8, 0, 0, 8, 8, stretch)
                .flip().addTopPlane(0, 0, 0, 8, 8, stretch);
    }

    @Override
    public void setRotationAndAngles(boolean rainboom, float move, float swing, float bodySwing, float ticks) {
        float rotation = MathHelper.sin(ticks * 0.536f) / 4;

        tailBase.offset(0, -4, -2).around(-2, 10, 8);

        tailBase.rotateAngleX = TAIL_ROTX + rotation;
        tailTip.rotateAngleX = rotation;
        tailFins.rotateAngleX = rotation - TAIL_ROTX;
    }

    @Override
    public void renderPart(float scale) {
        GlStateManager.enableBlend();
        tailBase.render(scale);
        GlStateManager.disableBlend();
    }

}
