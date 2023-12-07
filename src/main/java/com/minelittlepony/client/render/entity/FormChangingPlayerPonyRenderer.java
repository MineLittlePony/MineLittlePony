package com.minelittlepony.client.render.entity;

import com.minelittlepony.api.pony.*;

import java.util.function.Predicate;

import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Arm;
import net.minecraft.util.Identifier;

public class FormChangingPlayerPonyRenderer extends PlayerPonyRenderer {
    protected boolean transformed;

    private final Identifier alternateFormSkinId;
    private final Predicate<AbstractClientPlayerEntity> formModifierPredicate;

    public FormChangingPlayerPonyRenderer(EntityRendererFactory.Context context,
            boolean slim, Identifier alternateFormSkinId, Predicate<AbstractClientPlayerEntity> formModifierPredicate) {
        super(context, slim);
        this.alternateFormSkinId = alternateFormSkinId;
        this.formModifierPredicate = formModifierPredicate;
    }

    @Override
    public Identifier getTexture(AbstractClientPlayerEntity player) {
        if (transformed) {
            return SkinsProxy.instance.getSkin(alternateFormSkinId, player).orElseGet(() -> super.getTexture(player));
        }
        return super.getTexture(player);
    }

    @Override
    public void render(AbstractClientPlayerEntity player, float entityYaw, float tickDelta, MatrixStack stack, VertexConsumerProvider renderContext, int light) {
        super.render(player, entityYaw, tickDelta, stack, renderContext, light);
        updateForm(player);
    }

    @Override
    protected void renderArm(MatrixStack stack, VertexConsumerProvider renderContext, int lightUv, AbstractClientPlayerEntity player, Arm side) {
        super.renderArm(stack, renderContext, lightUv, player, side);
        updateForm(player);
    }

    protected void updateForm(AbstractClientPlayerEntity player) {
        transformed = formModifierPredicate.test(player);
    }
}
