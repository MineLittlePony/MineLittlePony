package com.minelittlepony.render.ponies;

import com.minelittlepony.model.PMAPI;
import com.minelittlepony.model.ponies.ModelIllagerPony;
import com.minelittlepony.render.RenderPonyMob;
import com.minelittlepony.render.layer.LayerHeldPonyItem;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.monster.EntityEvoker;
import net.minecraft.util.EnumHandSide;
import net.minecraft.util.ResourceLocation;

public class RenderPonyEvoker extends RenderPonyMob<EntityEvoker> {

    private static final ResourceLocation EVOKER = new ResourceLocation("minelittlepony", "textures/entity/illager/evoker_pony.png");

    public RenderPonyEvoker(RenderManager manager) {
        super(manager, PMAPI.illager);
    }

    @Override
    protected void addLayers() {
        addLayer(new LayerHeldPonyItem<EntityEvoker>(this) {
            @Override
            public void doPonyRender(EntityEvoker entity, float move, float swing, float ticks, float age, float headYaw, float headPitch, float scale) {
                if (entity.isSpellcasting()) {
                    super.doPonyRender(entity, move, swing, ticks, age, headYaw, headPitch, scale);
                }
            }

            @Override
            protected void postRenderArm(EnumHandSide side) {
                ((ModelIllagerPony) getRenderer().getMainModel()).getArm(side).postRender(0.0625F);
            }
        });
    }

    @Override
    protected ResourceLocation getTexture(EntityEvoker entity) {
        return EVOKER;
    }

    @Override
    protected void preRenderCallback(EntityEvoker entity, float ticks) {
        super.preRenderCallback(entity, ticks);
        GlStateManager.scale(0.9375F, 0.9375F, 0.9375F);
    }

}
