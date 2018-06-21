package com.minelittlepony.model.ponies;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.monster.EntityZombieVillager;
import net.minecraft.entity.passive.EntityVillager;
import static com.minelittlepony.model.PonyModelConstants.*;

import com.minelittlepony.model.components.SaddleBags;
import com.minelittlepony.model.player.ModelAlicorn;
import com.minelittlepony.pony.data.PonyWearable;
import com.minelittlepony.render.plane.PlaneRenderer;

public class ModelVillagerPony extends ModelAlicorn {

    public PlaneRenderer apron, trinket;

    private int profession;

    public ModelVillagerPony() {
        super(false);
    }

    @Override
    protected void shakeBody(float move, float swing, float bodySwing, float ticks) {
        super.shakeBody(move, swing, bodySwing, ticks);
        apron.rotateAngleY = bodySwing;
        trinket.rotateAngleY = bodySwing;
    }

    @Override
    public void setLivingAnimations(EntityLivingBase entity, float limbSwing, float limbSwingAmount, float partialTickTime) {
        profession = getProfession(entity);
    }

    @Override
    protected void renderBody(Entity entity, float move, float swing, float ticks, float headYaw, float headPitch, float scale) {
        super.renderBody(entity, move, swing, ticks, headYaw, headPitch, scale);

        if (profession == 2) {
            trinket.render(scale);
        } else if (profession > 2) {
            apron.render(scale);
        }
    }

    @Override
    public boolean isWearing(PonyWearable wearable) {
        if (wearable == PonyWearable.SADDLE_BAGS) {
            return profession > -1 && profession < 2;
        }

        return super.isWearing(wearable);
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
    public void init(float yOffset, float stretch) {
        super.init(yOffset, stretch);
        saddlebags = new SaddleBags(this);
        saddlebags.init(yOffset, stretch);

        apron = new PlaneRenderer(this, 56, 16)
             .offset(BODY_CENTRE_X, BODY_CENTRE_Y, BODY_CENTRE_Z)
             .around(HEAD_RP_X, HEAD_RP_Y + yOffset, HEAD_RP_Z)
             .addBackPlane(-4, -4, -9, 8, 10, stretch);
        trinket = new PlaneRenderer(this, 0, 3)
               .offset(BODY_CENTRE_X, BODY_CENTRE_Y, BODY_CENTRE_Z)
               .around(HEAD_RP_X, HEAD_RP_Y + yOffset, HEAD_RP_Z)
               .addBackPlane(-2, -4, -9, 4, 5, stretch);
    }
}
