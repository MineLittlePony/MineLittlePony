package com.minelittlepony.render.ponies;

import com.minelittlepony.model.PMAPI;
import com.minelittlepony.render.RenderPonyMob;
import com.minelittlepony.render.layer.LayerHeldPonyItem;
import com.minelittlepony.render.layer.LayerHeldPonyItemMagical;

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
        return new LayerHeldPonyItemMagical<EntityWitch>(this) {
            @Override
            protected void preItemRender(EntityWitch entity, ItemStack drop, TransformType transform, EnumHandSide hand) {
                if (isUnicorn()) {
                    GlStateManager.translate(-0.1F, 0.7F, 0);
                    GlStateManager.rotate(110, 1, 0, 0);
                } else {
                    GlStateManager.translate(0, -0.3F, -0.8F);
                    GlStateManager.rotate(10, 1, 0, 0);
                }
            }
        };
    }

    @Override
    protected void preRenderCallback(EntityWitch entity, float ticks) {
        super.preRenderCallback(entity, ticks);
        GlStateManager.scale(0.9375F, 0.9375F, 0.9375F);
    }

    @Override
    protected ResourceLocation getTexture(EntityWitch entity) {
        return WITCH_TEXTURES;
    }
}
