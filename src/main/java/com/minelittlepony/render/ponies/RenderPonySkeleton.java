package com.minelittlepony.render.ponies;

import com.minelittlepony.model.PMAPI;
import com.minelittlepony.render.RenderPonyMob;
import com.minelittlepony.render.layer.LayerPonyStrayOverlay;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.monster.AbstractSkeleton;
import net.minecraft.entity.monster.EntityStray;
import net.minecraft.entity.monster.EntityWitherSkeleton;
import net.minecraft.util.ResourceLocation;

public class RenderPonySkeleton<Skeleton extends AbstractSkeleton> extends RenderPonyMob<Skeleton> {

    public static final ResourceLocation SKELETON = new ResourceLocation("minelittlepony", "textures/entity/skeleton/skeleton_pony.png");
    public static final ResourceLocation WITHER = new ResourceLocation("minelittlepony", "textures/entity/skeleton/skeleton_wither_pony.png");
    public static final ResourceLocation STRAY = new ResourceLocation("minelittlepony", "textures/entity/skeleton/stray_pony.png");

    public RenderPonySkeleton(RenderManager manager) {
        super(manager, PMAPI.skeleton);
    }

    @Override
    protected ResourceLocation getTexture(Skeleton entity) {
        return SKELETON;
    }

    public static class Stray extends RenderPonySkeleton<EntityStray> {

        public Stray(RenderManager rm) {
            super(rm);
            addLayer(new LayerPonyStrayOverlay(this));
        }

        @Override
        protected ResourceLocation getTexture(EntityStray entity) {
            return STRAY;
        }
    }

    public static class Wither extends RenderPonySkeleton<EntityWitherSkeleton> {

        public Wither(RenderManager rm) {
            super(rm);
        }

        @Override
        protected ResourceLocation getTexture(EntityWitherSkeleton entity) {
            return WITHER;
        }

        @Override
        public void preRenderCallback(EntityWitherSkeleton skeleton, float ticks) {
            super.preRenderCallback(skeleton, ticks);
            GlStateManager.scale(1.2F, 1.2F, 1.2F);
        }

    }

}
