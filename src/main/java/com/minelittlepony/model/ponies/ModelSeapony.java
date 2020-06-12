package com.minelittlepony.model.ponies;

import com.minelittlepony.model.BodyPart;
import com.minelittlepony.model.armour.ModelPonyArmor;
import com.minelittlepony.model.armour.PonyArmor;
import com.minelittlepony.model.components.SeaponyTail;
import com.minelittlepony.model.player.ModelUnicorn;
import com.minelittlepony.pony.data.IPony;
import com.minelittlepony.render.model.PlaneRenderer;
import com.minelittlepony.render.model.PonyRenderer;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.math.MathHelper;

import org.lwjgl.opengl.GL11;

public class ModelSeapony extends ModelUnicorn {

    PonyRenderer bodyCenter;

    PlaneRenderer leftFin;
    PlaneRenderer centerFin;
    PlaneRenderer rightFin;

    public ModelSeapony() {
        super(false);
        textureHeight = 64;
    }

    @Override
    public PonyArmor createArmour() {
        return new PonyArmor(new Armour(), new Armour());
    }

    @Override
    public void updateLivingState(EntityLivingBase entity, IPony pony) {
        super.updateLivingState(entity, pony);

        // Seaponies can't sneak, silly
        isSneak = false;
        isCrouching = false;
    }

    @Override
    protected void ponySleep() {
       // noop
    }

    @Override
    protected void ponyRide() {
        // noop
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
                .east(0, -6, 0, 12, 6, stretch);

        leftFin = new PlaneRenderer(this, 56, 16)
                .rotate(0, FIN_ROT_Y, 0).around(3, -6, 3)
               .flipZ().east(0, 0, 0, 12, 8, stretch);

        rightFin = new PlaneRenderer(this, 56, 16)
                .rotate(0, -FIN_ROT_Y, 0).around(-3, -6, 3)
                .west(0, 0, 0, 12, 8, stretch);
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

        float flapMotion = MathHelper.cos(ticks / 10) / 5;

        if (isSleeping()) {
            flapMotion /= 2;
        }

        float finAngle = FIN_ROT_Y + flapMotion;

        leftFin.rotateAngleY = finAngle;
        rightFin.rotateAngleY = -finAngle;

        if (!isSleeping()) {
            centerFin.rotateAngleZ = flapMotion;
        }
    }

    @Override
    protected void rotateLegs(float move, float swing, float ticks, Entity entity) {
        super.rotateLegs(move, swing, ticks, entity);
        bipedLeftArm.rotateAngleX -= 1.4F;
        bipedLeftArm.rotateAngleY -= 0.3F;
        bipedRightArm.rotateAngleX -= 1.4F;
        bipedRightArm.rotateAngleY += 0.3F;

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
    protected void rotateLegsSwimming(float move, float swing, float ticks, Entity entity) {
        super.rotateLegsOnGround(move, swing, ticks, entity);
    }

    @Override
    public void render(Entity entity, float move, float swing, float ticks, float headYaw, float headPitch, float scale) {
        setVisible(bipedLeftArmwear.showModel);

        super.render(entity, move, swing, ticks, headYaw, headPitch, scale);
    }

    @Override
    public void transform(BodyPart part) {
        GlStateManager.translate(0, 0.6F, 0);

        super.transform(part);
    }

    @Override
    protected void renderBody(Entity entity, float move, float swing, float ticks, float headYaw, float headPitch, float scale) {
        bipedBody.render(scale);
        bodyCenter.render(scale);
        bipedBody.postRender(scale);

        tail.renderPart(scale, entity.getUniqueID());

        GL11.glPushAttrib(GL11.GL_ALL_ATTRIB_BITS);
        GlStateManager.enableBlend();


        leftFin.render(scale);
        centerFin.render(scale);
        rightFin.render(scale);

        GlStateManager.disableBlend();
        GL11.glPopAttrib();

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

    class Armour extends ModelPonyArmor {

        @Override
        public void showBoots() {
            bipedRightArm.showModel = true;
            bipedLeftArm.showModel = true;
        }

        @Override
        public void updateLivingState(EntityLivingBase entity, IPony pony) {
            super.updateLivingState(entity, pony);

            // Seaponies can't sneak, silly
            isSneak = false;
            isCrouching = false;
        }

        @Override
        protected void rotateLegs(float move, float swing, float ticks, Entity entity) {
            super.rotateLegs(move, swing, ticks, entity);
            bipedLeftArm.rotateAngleX -= 1.4F;
            bipedLeftArm.rotateAngleY -= 0.3F;
            bipedRightArm.rotateAngleX -= 1.4F;
            bipedRightArm.rotateAngleY += 0.3F;

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
        protected void rotateLegsSwimming(float move, float swing, float ticks, Entity entity) {
            super.rotateLegsOnGround(move, swing, ticks, entity);
        }

        @Override
        public void transform(BodyPart part) {
            GlStateManager.translate(0, 0.6F, 0);

            super.transform(part);
        }
    }
}
