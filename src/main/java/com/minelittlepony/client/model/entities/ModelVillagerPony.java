package com.minelittlepony.client.model.entities;

import net.minecraft.client.model.ModelPart;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.entity.model.ModelWithHat;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.passive.AbstractTraderEntity;
import net.minecraft.util.math.MathHelper;
import net.minecraft.village.VillagerDataContainer;
import net.minecraft.village.VillagerProfession;

import com.minelittlepony.client.model.components.BatWings;
import com.minelittlepony.client.model.components.PonyEars;
import com.minelittlepony.client.model.races.ModelAlicorn;
import com.minelittlepony.client.render.entities.villager.PonyTextures;
import com.minelittlepony.model.IPart;
import com.minelittlepony.pony.meta.Race;

public class ModelVillagerPony<T extends LivingEntity & VillagerDataContainer> extends ModelAlicorn<T> implements ModelWithHat {

    private ModelPart apron;
    private ModelPart trinket;

    private IPart batWings;

    public ModelVillagerPony() {
        super(false);
    }

    @Override
    public IPart getWings() {
        if (getMetadata().getRace() == Race.BATPONY) {
            return batWings;
        }
        return super.getWings();
    }

    @Override
    protected void initWings(float yOffset, float stretch) {
        super.initWings(yOffset, stretch);
        batWings = new BatWings<>(this, yOffset, stretch);
    }

    @Override
    protected void initEars(Part head, float yOffset, float stretch) {
        ears = new PonyEars(head, true);
        ears.init(yOffset, stretch);
    }

    @Override
    protected void shakeBody(float move, float swing, float bodySwing, float ticks) {
        super.shakeBody(move, swing, bodySwing, ticks);
        apron.yaw = bodySwing;
        trinket.yaw = bodySwing;
    }

    @Override
    public void animateModel(T entity, float limbSwing, float limbSwingAmount, float partialTickTime) {
        boolean special = PonyTextures.isBestPony(entity);

        VillagerProfession profession = entity.getVillagerData().getProfession();

        attributes.visualHeight = PonyTextures.isCrownPony(entity) ? 2.3F : 2;
        apron.visible = !special && profession == VillagerProfession.BUTCHER;
        trinket.visible = !special && !apron.visible && profession != VillagerProfession.NONE && profession != VillagerProfession.NITWIT;
    }

    @Override
    protected void renderBody(MatrixStack stack, VertexConsumer vertices, int overlayUv, int lightUv, float red, float green, float blue, float alpha) {
        super.renderBody(stack, vertices, overlayUv, lightUv, red, green, blue, alpha);
        apron.render(stack, vertices, overlayUv, lightUv, red, green, blue, alpha);
        //trinket.render(stack, vertices, overlayUv, lightUv, red, green, blue, alpha);
    }

    @Override
    public void init(float yOffset, float stretch) {
        super.init(yOffset, stretch);

        apron = new Part(this, 56, 16)
               .offset(BODY_CENTRE_X, BODY_CENTRE_Y, BODY_CENTRE_Z)
               .around(HEAD_RP_X, HEAD_RP_Y + yOffset, HEAD_RP_Z)
               .south(-4, -4, -9, 8, 10, stretch);
        trinket = new Part(this, 0, 3)
               .offset(BODY_CENTRE_X, BODY_CENTRE_Y, BODY_CENTRE_Z)
               .around(HEAD_RP_X, HEAD_RP_Y + yOffset, HEAD_RP_Z)
               .south(-2, -4, -9, 4, 5, stretch);
    }

    @Override
    public void setHatVisible(boolean visible) {
    }

    @Override
    public void setAngles(T entity, float move, float swing, float ticks, float headYaw, float headPitch) {
        super.setAngles(entity, move, swing, ticks, headYaw, headPitch);

        boolean isHeadRolling = entity instanceof AbstractTraderEntity
                && ((AbstractTraderEntity)entity).getHeadRollingTimeLeft() > 0;

        if (isHeadRolling) {
            float roll = 0.3F * MathHelper.sin(0.45F * ticks);

            this.head.roll = roll;
            this.helmet.roll = roll;

            this.head.pitch = 0.4F;
            this.helmet.pitch = 0.4F;
        } else {
            this.head.roll = 0.0F;
            this.helmet.roll = 0.0F;
        }
    }
}
