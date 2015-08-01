package com.minelittlepony.minelp.renderer;

import org.lwjgl.opengl.GL11;

import com.minelittlepony.minelp.PonyManager;
import com.minelittlepony.minelp.model.PMAPI;

import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.entity.layers.LayerBipedArmor;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.monster.EntitySkeleton;
import net.minecraft.util.ResourceLocation;

public class RenderPonySkeleton extends RenderPonyMob<EntitySkeleton> {
    public RenderPonySkeleton(RenderManager rm) {
        super(rm, PMAPI.skeletonPony);
        addLayer(new LayerBipedArmor(this) {
            @Override
            protected void func_177177_a() {
                this.field_177189_c = PMAPI.skeletonPony.model;
                this.field_177186_d = PMAPI.skeletonPony.model;
            }
        });
    }

    @Override
    protected void preRenderCallback(EntitySkeleton skeleton, float partialTicks) {
        if (skeleton.getSkeletonType() == 1) {
            GL11.glScalef(1.2F, 1.2F, 1.2F);
        }

    }

    protected void a(EntityLivingBase entity, float partialTicks) {
        this.preRenderCallback((EntitySkeleton) entity, partialTicks);
    }

    @Override
    protected ResourceLocation getEntityTexture(EntitySkeleton skeleton) {
        return skeleton.getSkeletonType() == 1 ? PonyManager.skeletonWitherPonyResource
                : PonyManager.skeletonPonyResource;
    }
}
