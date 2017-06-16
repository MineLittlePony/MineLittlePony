package com.minelittlepony.renderer;

import com.minelittlepony.model.PMAPI;
import com.minelittlepony.renderer.layer.LayerPonyStrayOverlay;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.entity.layers.LayerBipedArmor;
import net.minecraft.entity.monster.AbstractSkeleton;
import net.minecraft.entity.monster.EntityStray;
import net.minecraft.entity.monster.EntityWitherSkeleton;
import net.minecraft.util.ResourceLocation;

public class RenderPonySkeleton<Skeleton extends AbstractSkeleton> extends RenderPonyMob<Skeleton> {

    private static final ResourceLocation SKELETON = new ResourceLocation("minelittlepony", "textures/entity/skeleton/skeleton_pony.png");
    private static final ResourceLocation WITHER = new ResourceLocation("minelittlepony", "textures/entity/skeleton/skeleton_wither_pony.png");
    private static final ResourceLocation STRAY = new ResourceLocation("minelittlepony", "textures/entity/skeleton/stray_pony.png");

    public RenderPonySkeleton(RenderManager rm) {
        super(rm, PMAPI.skeleton);
    }

    @Override
    protected void addLayers() {
        super.addLayers();
        this.addLayer(new LayerBipedArmor(this) {
            @Override
            protected void initArmor() {
                this.modelLeggings = getPony().getArmor().modelArmor;
                this.modelArmor = getPony().getArmor().modelArmorChestplate;
            }
        });
    }

    @Override
    protected ResourceLocation getTexture(Skeleton entity) {
        return SKELETON;
    }

    public static class Stray extends RenderPonySkeleton<EntityStray> {

        public Stray(RenderManager rm) {
            super(rm);
            this.addLayer(new LayerPonyStrayOverlay(this));
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
        protected void preRenderCallback(EntityWitherSkeleton skeleton, float partialTicks) {
            super.preRenderCallback(skeleton, partialTicks);
            GlStateManager.scale(1.2F, 1.2F, 1.2F);
        }

    }

}
