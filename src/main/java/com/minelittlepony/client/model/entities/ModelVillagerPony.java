package com.minelittlepony.client.model.entities;

import net.minecraft.entity.LivingEntity;
import net.minecraft.village.VillagerDataContainer;
import net.minecraft.village.VillagerProfession;

import com.minelittlepony.client.model.ModelMobPony;
import com.minelittlepony.client.util.render.plane.PlaneRenderer;
import com.minelittlepony.pony.meta.Wearable;

public class ModelVillagerPony<T extends LivingEntity & VillagerDataContainer> extends ModelMobPony<T> {

    public PlaneRenderer apron;
    public PlaneRenderer trinket;

    private VillagerProfession profession;

    public boolean special;
    public boolean special2;

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
    }

    @Override
    protected void renderBody(T entity, float move, float swing, float ticks, float headYaw, float headPitch, float scale) {
        super.renderBody(entity, move, swing, ticks, headYaw, headPitch, scale);

        if (!special && profession != VillagerProfession.NONE && profession != VillagerProfession.NITWIT) {
            if (profession == VillagerProfession.BUTCHER) {
                trinket.render(scale);
            } else {
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
            return !special && profession != VillagerProfession.NONE && profession == VillagerProfession.NITWIT;
        }

        if (wearable == Wearable.MUFFIN) {
            return special2;
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
}
