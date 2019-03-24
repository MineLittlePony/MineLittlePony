package com.minelittlepony.client.model.entities;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.monster.EntityZombieVillager;
import net.minecraft.entity.passive.EntityVillager;

import com.minelittlepony.client.model.ModelMobPony;
import com.minelittlepony.client.util.render.plane.PlaneRenderer;
import com.minelittlepony.pony.meta.Wearable;

public class ModelVillagerPony extends ModelMobPony {

    public PlaneRenderer apron;
    public PlaneRenderer trinket;

    private int profession;

    public boolean special;
    public boolean special2;

    @Override
    protected void shakeBody(float move, float swing, float bodySwing, float ticks) {
        super.shakeBody(move, swing, bodySwing, ticks);
        apron.rotateAngleY = bodySwing;
        trinket.rotateAngleY = bodySwing;
    }

    @Override
    public void setLivingAnimations(EntityLivingBase entity, float limbSwing, float limbSwingAmount, float partialTickTime) {
        profession = getProfession(entity);
        special = "Derpy".equals(entity.getCustomName().getUnformattedComponentText());
        special2 = special && entity.getUniqueID().getLeastSignificantBits() % 20 == 0;
    }

    @Override
    protected void renderBody(Entity entity, float move, float swing, float ticks, float headYaw, float headPitch, float scale) {
        super.renderBody(entity, move, swing, ticks, headYaw, headPitch, scale);

        if (!special) {
            if (profession == 2) {
                trinket.render(scale);
            } else if (profession > 2) {
                apron.render(scale);
            }
        }
    }

    @Override
    public float getModelHeight() {
        return special2 ? 2.3F : 2;
    }

    @Override
    public boolean isWearing(Wearable wearable) {
        if (wearable == Wearable.SADDLE_BAGS) {
            return !special && profession > -1 && profession < 2;
        }

        if (wearable == Wearable.MUFFIN) {
            return special2;
        }

        return super.isWearing(wearable);
    }

    @SuppressWarnings("deprecation")
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

        apron = new PlaneRenderer(this, 56, 16)
               .offset(BODY_CENTRE_X, BODY_CENTRE_Y, BODY_CENTRE_Z)
               .around(HEAD_RP_X, HEAD_RP_Y + yOffset, HEAD_RP_Z)
               .south(-4, -4, -9, 8, 10, stretch);
        trinket = new PlaneRenderer(this, 0, 3)
               .offset(BODY_CENTRE_X, BODY_CENTRE_Y, BODY_CENTRE_Z)
               .around(HEAD_RP_X, HEAD_RP_Y + yOffset, HEAD_RP_Z)
               .south(-2, -4, -9, 4, 5, stretch);
    }
}
