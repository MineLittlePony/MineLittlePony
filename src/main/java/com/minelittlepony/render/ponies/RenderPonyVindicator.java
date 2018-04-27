package com.minelittlepony.render.ponies;

import com.minelittlepony.model.PMAPI;
import com.minelittlepony.model.ponies.ModelIllagerPony;
import com.minelittlepony.render.RenderPonyMob;
import com.minelittlepony.render.layer.LayerHeldPonyItem;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.monster.EntityVindicator;
import net.minecraft.util.EnumHandSide;
import net.minecraft.util.ResourceLocation;

public class RenderPonyVindicator extends RenderPonyMob<EntityVindicator> {

    private static final ResourceLocation VINDICATOR = new ResourceLocation("minelittlepony", "textures/entity/illager/vindicator_pony.png");

    public RenderPonyVindicator(RenderManager manager) {
        super(manager, PMAPI.illager);

    }

    @Override
    protected void addLayers() {
        this.addLayer(new LayerHeldPonyItem<EntityVindicator>(this) {
            @Override
            public void doPonyRender(EntityVindicator entity, float move, float swing, float ticks, float age, float headYaw, float headPitch, float scale) {
                if (entity.isAggressive()) {
                    super.doPonyRender(entity, move, swing, ticks, age, headYaw, headPitch, scale);
                }
            }

            @Override
            protected void translateToHand(EnumHandSide side) {
                ((ModelIllagerPony) getRenderer().getMainModel()).getArm(side).postRender(0.0625F);
            }
        });
    }

    @Override
    protected ResourceLocation getTexture(EntityVindicator entity) {
        return VINDICATOR;
    }

    @Override
    protected void preRenderCallback(EntityVindicator entity, float ticks) {
        super.preRenderCallback(entity, ticks);
        GlStateManager.scale(0.9375F, 0.9375F, 0.9375F);
    }

}
