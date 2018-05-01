package com.minelittlepony.model.ponies;

import net.minecraft.entity.Entity;
import net.minecraft.entity.monster.EntityZombieVillager;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.util.math.MathHelper;
import static com.minelittlepony.model.PonyModelConstants.*;

import com.minelittlepony.model.player.ModelAlicorn;
import com.minelittlepony.render.plane.PlaneRenderer;

public class ModelVillagerPony extends ModelAlicorn {

    public PlaneRenderer bag, apron, trinket;

    public ModelVillagerPony() {
        super(false);
    }

    @Override
    public void setRotationAngles(float move, float swing, float ticks, float headYaw, float headPitch, float scale, Entity entity) {
        super.setRotationAngles(move, swing, ticks, headYaw, headPitch, scale, entity);

        float angleY = 0;
        if (swingProgress > -9990.0F && !canCast()) {
            angleY = MathHelper.sin(MathHelper.sqrt(swingProgress) * PI * 2) * 0.04F;
        }
        bag.rotateAngleY = angleY;
        apron.rotateAngleY = angleY;
        trinket.rotateAngleY = angleY;
    }

    @Override
    protected void renderBody(Entity entity, float move, float swing, float ticks, float headYaw, float headPitch, float scale) {
        super.renderBody(entity, move, swing, ticks, headYaw, headPitch, scale);

        int profession = getProfession(entity);
        if (profession > -1) {
            bipedBody.postRender(this.scale);
            if (profession < 2) {
                bag.render(scale);
            } else if (profession == 2) {
                trinket.render(scale);
            } else if (profession > 2) {
                apron.render(scale);
            }
        }
    }

    protected int getProfession(Entity entity) {
        if (entity instanceof EntityVillager) {
            return ((EntityVillager) entity).getProfession();
        }
        if (entity instanceof EntityZombieVillager) {
            return ((EntityZombieVillager) entity).getProfession();
        }
        return -1;
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
           .tex(56, 25).addBackPlane(-7,     -5,    -4, 3, 6, stretch) //right bag front
                       .addBackPlane( 4,     -5,    -4, 3, 6, stretch) //left bag front
           .tex(59, 25).addBackPlane(-7,     -5,     4, 3, 6, stretch) //right bag back
                       .addBackPlane( 4,     -5,     4, 3, 6, stretch) //left bag back
           .tex(56, 29).addWestPlane(-7,     -5,    -4, 6, 8, stretch) //right bag outside
                       .addWestPlane( 7,     -5,    -4, 6, 8, stretch) //left bag outside
                       .addWestPlane(-4.01f, -5,    -4, 6, 8, stretch) //right bag inside
                       .addWestPlane( 4.01f, -5,    -4, 6, 8, stretch) //left bag inside
           .tex(56, 31) .addTopPlane(-4,     -4.5F, -1, 8, 1, stretch) //strap front
                        .addTopPlane(-4,     -4.5F,  0, 8, 1, stretch) //strap back
                       .addBackPlane(-4,     -4.5F,  0, 8, 1, stretch)
                      .addFrontPlane(-4,     -4.5F,  0, 8, 1, stretch)
           .child(0).tex(56, 16).flipZ().addTopPlane(2, -5, -13, 8, 3, stretch) //left bag top
                                .flipZ().addTopPlane(2, -5,  -2, 8, 3, stretch) //right bag top
                 .tex(56, 22).flipZ().addBottomPlane(2,  1, -13, 8, 3, stretch) //left bag bottom
                             .flipZ().addBottomPlane(2,  1,  -2, 8, 3, stretch) //right bag bottom
                    .rotateAngleY = 4.712389F;

        apron.offset(BODY_CENTRE_X, BODY_CENTRE_Y, BODY_CENTRE_Z)
             .around(HEAD_RP_X, HEAD_RP_Y + yOffset, HEAD_RP_Z)
             .addBackPlane(-4, -4, -9, 8, 10, stretch);
        trinket.offset(BODY_CENTRE_X, BODY_CENTRE_Y, BODY_CENTRE_Z)
               .around(HEAD_RP_X, HEAD_RP_Y + yOffset, HEAD_RP_Z)
               .addBackPlane(-2, -4, -9, 4, 5, stretch);
    }
}
