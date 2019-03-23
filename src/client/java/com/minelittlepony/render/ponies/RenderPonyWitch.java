package com.minelittlepony.render.ponies;

import com.minelittlepony.model.PMAPI;
import com.minelittlepony.render.RenderPonyMob;
import com.minelittlepony.render.layer.LayerHeldPonyItem;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms.TransformType;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.monster.EntityWitch;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumHandSide;
import net.minecraft.util.ResourceLocation;

public class RenderPonyWitch extends RenderPonyMob<EntityWitch> {

    private static final ResourceLocation WITCH_TEXTURES = new ResourceLocation("minelittlepony", "textures/entity/witch_pony.png");

    public RenderPonyWitch(RenderManager manager) {
        super(manager, PMAPI.witch);
    }

    @Override
    protected LayerHeldPonyItem<EntityWitch> createItemHoldingLayer() {
        return new LayerHeldPonyItem<EntityWitch>(this) {
            @Override
            protected void preItemRender(EntityWitch entity, ItemStack drop, TransformType transform, EnumHandSide hand) {
                GlStateManager.translate(0, -0.3F, -0.8F);
                GlStateManager.rotate(10, 1, 0, 0);
            }
        };
    }

    @Override
    public void preRenderCallback(EntityWitch entity, float ticks) {
        super.preRenderCallback(entity, ticks);
        GlStateManager.scale(BASE_MODEL_SCALE, BASE_MODEL_SCALE, BASE_MODEL_SCALE);
    }

    @Override
    public ResourceLocation getTexture(EntityWitch entity) {
        return WITCH_TEXTURES;
    }
}
