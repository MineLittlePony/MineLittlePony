package com.minelittlepony.client.model.entity;

import net.minecraft.client.model.ModelPart;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.entity.model.ModelWithHat;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.passive.AbstractTraderEntity;
import net.minecraft.util.math.MathHelper;
import net.minecraft.village.VillagerDataContainer;
import net.minecraft.village.VillagerProfession;

import com.minelittlepony.api.pony.IPony;
import com.minelittlepony.api.pony.meta.Race;
import com.minelittlepony.client.model.entity.race.AlicornModel;
import com.minelittlepony.client.render.EquineRenderManager;
import com.minelittlepony.client.render.entity.npc.PonyTextures;
import com.minelittlepony.model.IPart;
import com.minelittlepony.mson.api.ModelContext;

public class VillagerPonyModel<T extends LivingEntity & VillagerDataContainer> extends AlicornModel<T> implements ModelWithHat {

    private ModelPart apron;
    private ModelPart trinket;

    private IPart batWings;
    private IPart batEars;

    public VillagerPonyModel() {
        super(false);
    }

    @Override
    public void init(ModelContext context) {
        super.init(context);
        batWings = context.findByName("bat_wings");
        batEars = context.findByName("bat_ears");
        apron = context.findByName("apron");
        trinket = context.findByName("trinket");
    }

    @Override
    public void updateLivingState(T entity, IPony pony, EquineRenderManager.Mode mode) {
        super.updateLivingState(entity, pony, mode);

        ears.setVisible(pony.getMetadata().getRace() != Race.BATPONY);
        batEars.setVisible(pony.getMetadata().getRace() == Race.BATPONY);
    }

    @Override
    public IPart getWings() {
        if (getMetadata().getRace() == Race.BATPONY) {
            return batWings;
        }
        return super.getWings();
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
