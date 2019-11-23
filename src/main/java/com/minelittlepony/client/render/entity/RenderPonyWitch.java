package com.minelittlepony.client.render.entity;

import com.minelittlepony.client.model.entity.ModelWitchPony;
import com.minelittlepony.client.render.entity.feature.LayerHeldPonyItem;

import net.fabricmc.fabric.api.client.rendereregistry.v1.EntityRendererRegistry;
import net.minecraft.client.render.entity.EntityRenderDispatcher;
import net.minecraft.client.render.model.json.ModelTransformation;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.util.math.Vector3f;
import net.minecraft.entity.mob.WitchEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Arm;
import net.minecraft.util.Identifier;

public class RenderPonyWitch extends RenderPonyMob<WitchEntity, ModelWitchPony> {

    private static final Identifier WITCH_TEXTURES = new Identifier("minelittlepony", "textures/entity/witch_pony.png");

    public RenderPonyWitch(EntityRenderDispatcher manager, EntityRendererRegistry.Context context) {
        super(manager, new ModelWitchPony());
    }

    @Override
    protected LayerHeldPonyItem<WitchEntity, ModelWitchPony> createItemHoldingLayer() {
        return new LayerHeldPonyItem<WitchEntity, ModelWitchPony>(this) {
            @Override
            protected void preItemRender(WitchEntity entity, ItemStack drop, ModelTransformation.Type transform, Arm hand, MatrixStack stack) {
                stack.translate(0, -0.3F, -0.8F);
                stack.multiply(Vector3f.POSITIVE_X.getDegreesQuaternion(10));
            }
        };
    }

    @Override
    public void scale(WitchEntity entity, MatrixStack stack, float ticks) {
        super.scale(entity, stack, ticks);
        stack.scale(BASE_MODEL_SCALE, BASE_MODEL_SCALE, BASE_MODEL_SCALE);
    }

    @Override
    public Identifier findTexture(WitchEntity entity) {
        return WITCH_TEXTURES;
    }
}
