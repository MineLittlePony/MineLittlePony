package com.minelittlepony.model.ponies;

import net.minecraft.entity.Entity;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.util.math.MathHelper;
import static com.minelittlepony.model.PonyModelConstants.*;

import com.minelittlepony.render.plane.PlaneRenderer;

public class ModelVillagerPony extends ModelPlayerPony {

    public PlaneRenderer bag, apron, trinket;

    public ModelVillagerPony() {
        super(false);
    }

    @Override
    public void setRotationAngles(float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, float scaleFactor, Entity entityIn) {
        super.setRotationAngles(limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scaleFactor, entityIn);

        float swing = 0;
        if (swingProgress > -9990.0F && !metadata.hasMagic()) {
            swing = MathHelper.sin(MathHelper.sqrt(swingProgress) * (float)Math.PI * 2) * 0.04f;
        }
        bag.rotateAngleY = swing;
        apron.rotateAngleY = swing;
        trinket.rotateAngleY = swing;
    }

    @Override
    protected void renderBody(Entity entityIn, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, float scale) {
        super.renderBody(entityIn, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scale);

        if (entityIn instanceof EntityVillager) {
            this.bipedBody.postRender(this.scale);
            int profession = ((EntityVillager) entityIn).getProfession();
            if (profession < 2) {
                bag.render(scale);
            } else if (profession == 2) {
                trinket.render(scale);
            } else if (profession > 2) {
                apron.render(scale);
            }
        }
    }

    @Override
    protected void initTextures() {
        super.initTextures();
        bag = new PlaneRenderer(this, 56, 19).at(BODY_CENTRE_X, BODY_CENTRE_Y, BODY_CENTRE_Z);
        apron = new PlaneRenderer(this, 56, 16).at(BODY_CENTRE_X, BODY_CENTRE_Y, BODY_CENTRE_Z);
        trinket = new PlaneRenderer(this, 0, 3).at(BODY_CENTRE_X, BODY_CENTRE_Y, BODY_CENTRE_Z);
    }

    @Override
    protected void initPositions(float yOffset, float stretch) {
        super.initPositions(yOffset, stretch);
        
        bag.around(HEAD_RP_X, HEAD_RP_Y + yOffset, HEAD_RP_Z);
        bag.setTextureOffset(56, 29);
        bag.addWestPlane(-7, -5, -4, 6, 8, stretch);
        bag.addWestPlane(-4, -5, -4, 6, 8, stretch);
        bag.addWestPlane( 4, -5, -4, 6, 8, stretch);
        bag.addWestPlane( 7, -5, -4, 6, 8, stretch);
        
        PlaneRenderer rotatedPieces = new PlaneRenderer(this, 56, 16);
        rotatedPieces.rotateAngleY = 4.712389F;
        bag.addChild(rotatedPieces);
        
        rotatedPieces.addTopPlane(2, -5, -2, 8, 3, stretch);
        rotatedPieces.addTopPlane(2, -5, -13, 8, 3, stretch);
        rotatedPieces.setTextureOffset(56, 22);
        rotatedPieces.addBottomPlane(2, 1, -2, 8, 3, stretch);
        
        bag.setTextureOffset(56, 22);
        bag.addBottomPlane(2, 1, -13, 8, 3, stretch);
        bag.setTextureOffset(56, 25);
        bag.addBackPlane(-7, -5, -4, 3, 6, stretch);
        bag.addBackPlane( 4, -5, -4, 3, 6, stretch);
        bag.setTextureOffset(59, 25);
        bag.addBackPlane(-7, -5, 4, 3, 6, stretch);
        bag.addBackPlane( 5, -5, 4, 3, 6, stretch);
        bag.setTextureOffset(56, 31);
        bag.addTopPlane(-4, -4.5F, -1, 8, 1, stretch);
        bag.addTopPlane(-4, -4.5F, 0, 8, 1, stretch);
        
        apron.around(HEAD_RP_X, HEAD_RP_Y + yOffset, HEAD_RP_Z).addBackPlane(-4, -4, -9, 8, 10, stretch);
        trinket.around(HEAD_RP_X, HEAD_RP_Y + yOffset, HEAD_RP_Z).addBackPlane(-2, -4, -9, 4, 5, stretch);
    }
}
