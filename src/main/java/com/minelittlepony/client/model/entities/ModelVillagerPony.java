package com.minelittlepony.client.model.entities;

import net.minecraft.client.render.entity.model.ModelWithHat;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.passive.AbstractTraderEntity;
import net.minecraft.util.math.MathHelper;
import net.minecraft.village.VillagerDataContainer;
import net.minecraft.village.VillagerProfession;

import com.minelittlepony.client.model.ModelMobPony;
import com.minelittlepony.client.util.render.plane.PlaneRenderer;
import com.minelittlepony.pony.meta.Wearable;

public class ModelVillagerPony<T extends LivingEntity & VillagerDataContainer> extends ModelMobPony<T> implements ModelWithHat {

    public PlaneRenderer apron;
    public PlaneRenderer trinket;


    private VillagerProfession profession;

    public boolean special;
    public boolean special2;

    public boolean hatVisible;

    @Override
    protected void shakeBody(float move, float swing, float bodySwing, float ticks) {
        super.shakeBody(move, swing, bodySwing, ticks);
        apron.yaw = bodySwing;
        trinket.yaw = bodySwing;
    }

    @Override
    public void animateModel(T entity, float limbSwing, float limbSwingAmount, float partialTickTime) {
        profession = entity.getVillagerData().getProfession();
        special = entity.hasCustomName() && "Derpy".equals(entity.getCustomName().getString());
        special2 = special && entity.getUuid().getLeastSignificantBits() % 20 == 0;
        attributes.visualHeight = special2 ? 2.3F : 2;
    }

    @Override
    protected void renderBody(float scale) {
        super.renderBody(scale);

        if (!special && profession != VillagerProfession.NONE && profession != VillagerProfession.NITWIT) {
            if (profession == VillagerProfession.BUTCHER) {
                apron.render(scale);
            } else {
                trinket.render(scale);
            }
        }
    }

    @Override
    public boolean isWearing(Wearable wearable) {

        if (wearable == Wearable.SADDLE_BAGS) {
            return !special && profession != VillagerProfession.NONE && (
                       profession == VillagerProfession.CARTOGRAPHER
                    || profession == VillagerProfession.FARMER
                    || profession == VillagerProfession.FISHERMAN
                    || profession == VillagerProfession.LIBRARIAN
                    || profession == VillagerProfession.SHEPHERD);
        }

        if (wearable == Wearable.MUFFIN) {
            return special2;
        }
        if (wearable == Wearable.VILLAGER) {
            return hatVisible;
        }

        return super.isWearing(wearable);
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

    @Override
    public void setHatVisible(boolean visible) {
        hatVisible = visible;
    }

    @Override
    public void setAngles(T entity, float move, float swing, float ticks, float headYaw, float headPitch, float scale) {
        super.setAngles(entity, move, swing, ticks, headYaw, headPitch, scale);

        boolean isHeadRolling = false;
        if (entity instanceof AbstractTraderEntity) {
            isHeadRolling = ((AbstractTraderEntity)entity).getHeadRollingTimeLeft() > 0;
        }

        if (isHeadRolling) {
            float roll = 0.3F * MathHelper.sin(0.45F * ticks);

            this.head.roll = roll;
            this.headwear.roll = roll;

            this.head.pitch = 0.4F;
            this.headwear.pitch = 0.4F;
        } else {
            this.head.roll = 0.0F;
            this.headwear.roll = 0.0F;
        }
    }
}
