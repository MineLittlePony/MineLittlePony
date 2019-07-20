package com.minelittlepony.client.render.entities;

import com.minelittlepony.client.model.entities.ModelWitchPony;
import com.minelittlepony.client.render.layer.LayerHeldPonyItem;
import com.mojang.blaze3d.platform.GlStateManager;

import net.fabricmc.fabric.api.client.render.EntityRendererRegistry;
import net.minecraft.client.render.entity.EntityRenderDispatcher;
import net.minecraft.client.render.model.json.ModelTransformation;
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
            protected void preItemRender(WitchEntity entity, ItemStack drop, ModelTransformation.Type transform, Arm hand) {
                GlStateManager.translatef(0, -0.3F, -0.8F);
                GlStateManager.rotatef(10, 1, 0, 0);
            }
        };
    }

    @Override
    public void scale(WitchEntity entity, float ticks) {
        super.scale(entity, ticks);
        GlStateManager.scalef(BASE_MODEL_SCALE, BASE_MODEL_SCALE, BASE_MODEL_SCALE);
    }

    @Override
    public Identifier findTexture(WitchEntity entity) {
        return WITCH_TEXTURES;
    }
}
