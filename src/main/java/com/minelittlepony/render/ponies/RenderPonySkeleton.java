package com.minelittlepony.render.ponies;

import com.minelittlepony.PonyConfig;
import com.minelittlepony.model.PMAPI;
import com.minelittlepony.render.RenderPonyMob;
import com.minelittlepony.render.PonySkull;
import com.minelittlepony.render.PonySkullRenderer.ISkull;
import com.minelittlepony.render.layer.LayerPonyStrayOverlay;
import com.mojang.authlib.GameProfile;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.entity.layers.LayerBipedArmor;
import net.minecraft.entity.monster.AbstractSkeleton;
import net.minecraft.entity.monster.EntityStray;
import net.minecraft.entity.monster.EntityWitherSkeleton;
import net.minecraft.util.ResourceLocation;

public class RenderPonySkeleton<Skeleton extends AbstractSkeleton> extends RenderPonyMob<Skeleton> {

    public static final ResourceLocation SKELETON = new ResourceLocation("minelittlepony", "textures/entity/skeleton/skeleton_pony.png");
    public static final ResourceLocation WITHER = new ResourceLocation("minelittlepony", "textures/entity/skeleton/skeleton_wither_pony.png");
    public static final ResourceLocation STRAY = new ResourceLocation("minelittlepony", "textures/entity/skeleton/stray_pony.png");

    public static final ISkull SKULL = new PonySkull() {
        @Override
        public boolean canRender(PonyConfig config) {
            return config.skeletons;
        }

        @Override
        public ResourceLocation getSkinResource(GameProfile profile) {
            return RenderPonySkeleton.SKELETON;
        }
    }.register(ISkull.SKELETON);

    public RenderPonySkeleton(RenderManager manager) {
        super(manager, PMAPI.skeleton);
    }

    @Override
    protected void addLayers() {
        super.addLayers();
        addLayer(new LayerBipedArmor(this) {
            @Override
            protected void initArmor() {
                modelLeggings = getModelWrapper().getArmor().innerLayer;
                modelArmor = getModelWrapper().getArmor().outerLayer;
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
            addLayer(new LayerPonyStrayOverlay(this));
        }

        @Override
        protected ResourceLocation getTexture(EntityStray entity) {
            return STRAY;
        }
    }

    public static class Wither extends RenderPonySkeleton<EntityWitherSkeleton> {

        public static final ISkull SKULL = new PonySkull() {
            @Override
            public boolean canRender(PonyConfig config) {
                return config.skeletons;
            }

            @Override
            public ResourceLocation getSkinResource(GameProfile profile) {
                return RenderPonySkeleton.WITHER;
            }
        }.register(ISkull.WITHER);

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
