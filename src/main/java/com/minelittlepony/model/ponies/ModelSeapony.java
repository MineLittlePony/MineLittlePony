package com.minelittlepony.model.ponies;

import com.minelittlepony.model.components.SeaponyTail;
import com.minelittlepony.model.player.ModelUnicorn;
import com.minelittlepony.render.PonyRenderer;
import com.minelittlepony.render.plane.PlaneRenderer;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.MathHelper;

import static com.minelittlepony.model.PonyModelConstants.*;

public class ModelSeapony extends ModelUnicorn {

    private static final float FIN_ROTY = PI / 6;

    PonyRenderer bodyCenter;

    PlaneRenderer leftFin;
    PlaneRenderer centerFin;
    PlaneRenderer rightFin;

    public ModelSeapony() {
        super(false);
        textureHeight = 64;
    }

    @Override
    protected void initLegs(float yOffset, float stretch) {
        super.initLegs(yOffset, stretch);
        // hide the back legs
        bipedLeftLeg.showModel = false;
        bipedRightLeg.showModel = false;
        bipedLeftLegwear.showModel = false;
        bipedRightLegwear.showModel = false;

        centerFin = new PlaneRenderer(this, 58, 28)
                .rotate(PI / 2 - 0.1F, 0, 0).around(0, 6, 9)
                .addEastPlane(0, -6, 0, 12, 6, stretch);

        leftFin = new PlaneRenderer(this, 56, 16)
                .rotate(0, FIN_ROTY, 0).around(3, -6, 3)
               .flipZ().addEastPlane(0, 0, 0, 12, 8, stretch);

        rightFin = new PlaneRenderer(this, 56, 16)
                .rotate(0, -FIN_ROTY, 0).around(-3, -6, 3)
                .addWestPlane(0, 0, 0, 12, 8, stretch);
    }

    @Override
    protected void initTail(float yOffset, float stretch) {
        tail = new SeaponyTail(this);
        tail.init(yOffset, stretch);
    }

    @Override
    protected void initBody(float yOffset, float stretch) {
        super.initBody(yOffset, stretch);
        bodyCenter = new PonyRenderer(this, 0, 48)
                .around(0, 6, 1)
                .box(-3, -1, 0, 6, 7, 9, stretch).flip();
    }

    @Override
    public void setRotationAngles(float move, float swing, float ticks, float headYaw, float headPitch, float scale, Entity entity) {
        super.setRotationAngles(move, swing, ticks, headYaw, headPitch, scale, entity);

        float finAngle = FIN_ROTY + MathHelper.cos(ticks / 10) / 5;

        leftFin.rotateAngleY = finAngle;
        rightFin.rotateAngleY = -finAngle;
        centerFin.rotateAngleZ = MathHelper.cos(ticks / 10) / 5;

        if (!entity.isInWater()) {
            bipedLeftArm.rotateAngleX -= 0.5F;
            bipedRightArm.rotateAngleX -= 0.5F;
        }

        if (!entity.isInWater() || entity.onGround) {
            bipedLeftArm.rotateAngleY -= 0.5F;
            bipedRightArm.rotateAngleY += 0.5F;
        }
    }

    @Override
    protected void rotateLegs(float move, float swing, float ticks, Entity entity) {
        super.rotateLegs(move, swing, ticks, entity);
        bipedLeftArm.rotateAngleX -= 1.4F;
        bipedLeftArm.rotateAngleY -= 0.3F;
        bipedRightArm.rotateAngleX -= 1.4F;
        bipedRightArm.rotateAngleY += 0.3F;
    }

    @Override
    public void render(Entity entity, float move, float swing, float ticks, float headYaw, float headPitch, float scale) {
        GlStateManager.pushMatrix();
        GlStateManager.translate(0, 0.6F, 0);
        super.render(entity, move, swing, ticks, headYaw, headPitch, scale);
        GlStateManager.popMatrix();
    }

    @Override
    protected void renderBody(Entity entity, float move, float swing, float ticks, float headYaw, float headPitch, float scale) {
        bipedBody.render(scale);
        bodyCenter.render(scale);
        bipedBody.postRender(scale);

        tail.renderPart(scale);

        GlStateManager.enableBlend();


        leftFin.render(scale);
        centerFin.render(scale);
        rightFin.render(scale);

        GlStateManager.disableBlend();

    }

    @Override
    public boolean canCast() {
        return true;
    }

    @Override
    public void setVisible(boolean visible) {
        super.setVisible(visible);

        // hide the back legs
        bipedLeftLeg.showModel = false;
        bipedRightLeg.showModel = false;
        bipedLeftLegwear.showModel = false;
        bipedRightLegwear.showModel = false;
    }
}
