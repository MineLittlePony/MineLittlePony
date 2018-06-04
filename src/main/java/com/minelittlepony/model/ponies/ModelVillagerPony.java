package com.minelittlepony.model.ponies;

import net.minecraft.entity.Entity;
import net.minecraft.entity.monster.EntityZombieVillager;
import net.minecraft.entity.passive.EntityVillager;
import static com.minelittlepony.model.PonyModelConstants.*;

import com.minelittlepony.model.components.SaddleBags;
import com.minelittlepony.model.player.ModelAlicorn;
import com.minelittlepony.render.plane.PlaneRenderer;

public class ModelVillagerPony extends ModelAlicorn {

    public PlaneRenderer apron, trinket;

    public SaddleBags saddlebags;

    public ModelVillagerPony() {
        super(false);
    }

    @Override
    protected void shakeBody(float move, float swing, float bodySwing, float ticks) {
        super.shakeBody(move, swing, bodySwing, ticks);

        saddlebags.setRotationAndAngles(rainboom, move, swing, bodySwing, ticks);
        apron.rotateAngleY = bodySwing;
        trinket.rotateAngleY = bodySwing;
    }

    @Override
    protected void renderBody(Entity entity, float move, float swing, float ticks, float headYaw, float headPitch, float scale) {
        super.renderBody(entity, move, swing, ticks, headYaw, headPitch, scale);

        int profession = getProfession(entity);
        if (profession > -1) {
            if (profession < 2) {
                saddlebags.renderPart(scale);
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
        saddlebags = new SaddleBags(this);
        apron = new PlaneRenderer(this, 56, 16);
        trinket = new PlaneRenderer(this, 0, 3);
    }

    @Override
    protected void initPositions(float yOffset, float stretch) {
        super.initPositions(yOffset, stretch);

        saddlebags.init(yOffset, stretch);

        apron.offset(BODY_CENTRE_X, BODY_CENTRE_Y, BODY_CENTRE_Z)
             .around(HEAD_RP_X, HEAD_RP_Y + yOffset, HEAD_RP_Z)
             .addBackPlane(-4, -4, -9, 8, 10, stretch);
        trinket.offset(BODY_CENTRE_X, BODY_CENTRE_Y, BODY_CENTRE_Z)
               .around(HEAD_RP_X, HEAD_RP_Y + yOffset, HEAD_RP_Z)
               .addBackPlane(-2, -4, -9, 4, 5, stretch);
    }
}
