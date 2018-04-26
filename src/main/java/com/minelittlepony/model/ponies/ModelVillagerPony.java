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
            bipedBody.postRender(this.scale);
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
        bag = new PlaneRenderer(this, 56, 19);
        apron = new PlaneRenderer(this, 56, 16);
        trinket = new PlaneRenderer(this, 0, 3);
    }

    @Override
    protected void initPositions(float yOffset, float stretch) {
        super.initPositions(yOffset, stretch);
        
        bag.offset(BODY_CENTRE_X, BODY_CENTRE_Y, BODY_CENTRE_Z)
           .around(HEAD_RP_X, HEAD_RP_Y + yOffset, HEAD_RP_Z)
           .tex(56, 25).addBackPlane(-7, -5,    -4, 3, 6, stretch) //right bag front
                       .addBackPlane( 4, -5,    -4, 3, 6, stretch) //left bag front
           .tex(59, 25).addBackPlane(-7, -5,     4, 3, 6, stretch) //right bag back
                       .addBackPlane( 5, -5,     4, 3, 6, stretch) //left bag back
           .tex(56, 29).addWestPlane(-7, -5,    -4, 6, 8, stretch) //right bag outside
                       .addWestPlane( 7, -5,    -4, 6, 8, stretch) //left bag outside
                       .addWestPlane(-4, -5,    -4, 6, 8, stretch) //right bag inside
                       .addWestPlane( 4, -5,    -4, 6, 8, stretch) //left bag inside
            .tex(56, 31).addTopPlane(-4, -4.5F, -1, 8, 1, stretch) //strap front
                        .addTopPlane(-4, -4.5F,  0, 8, 1, stretch) //strap back
           .child(0).tex(56, 16).addTopPlane(2, -5, -2,  8, 3, stretch) //right bag top
                                .addTopPlane(2, -5, -13, 8, 3, stretch) //left bag top
                 .tex(56, 22).addBottomPlane(2,  1, -2,  8, 3, stretch) //right bag bottom
                             .addBottomPlane(2,  1, -13, 8, 3, stretch) //left bag bottom
                    .rotateAngleY = 4.712389F;
        
        apron.offset(BODY_CENTRE_X, BODY_CENTRE_Y, BODY_CENTRE_Z)
             .around(HEAD_RP_X, HEAD_RP_Y + yOffset, HEAD_RP_Z)
             .addBackPlane(-4, -4, -9, 8, 10, stretch);
        trinket.offset(BODY_CENTRE_X, BODY_CENTRE_Y, BODY_CENTRE_Z)
               .around(HEAD_RP_X, HEAD_RP_Y + yOffset, HEAD_RP_Z)
               .addBackPlane(-2, -4, -9, 4, 5, stretch);
    }
}
