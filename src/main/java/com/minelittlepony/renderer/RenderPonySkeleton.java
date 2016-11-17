package com.minelittlepony.renderer;

import java.util.Random;

import com.minelittlepony.PonyGender;
import com.minelittlepony.PonyRace;
import com.minelittlepony.PonySize;
import com.minelittlepony.TailLengths;
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
        this.addLayer(new LayerBipedArmor(this) {
            @Override
            protected void initArmor() {
                this.modelLeggings = PMAPI.skeleton.getArmor().modelArmor;
                this.modelArmor = PMAPI.skeleton.getArmor().modelArmorChestplate;
            }
        });
    }

    @Override
    protected void preRenderCallback(Skeleton skeleton, float partialTicks) {
        super.preRenderCallback(skeleton, partialTicks);

        Random rand = new Random(skeleton.getUniqueID().hashCode());
        this.playerModel.getModel().metadata.setGender(rand.nextBoolean() ? PonyGender.MARE : PonyGender.STALLION);
        switch (rand.nextInt(4)) {
        case 0:
        case 1:
            this.playerModel.getModel().metadata.setRace(PonyRace.UNICORN);
            break;
        case 2:
            this.playerModel.getModel().metadata.setRace(PonyRace.EARTH);
            break;
        case 3:
            this.playerModel.getModel().metadata.setRace(PonyRace.PEGASUS);
        }
        PonySize[] sizes = PonySize.values();
        PonySize size = sizes[rand.nextInt(sizes.length)];
        this.playerModel.getModel().metadata.setSize(size == PonySize.FOAL ? PonySize.NORMAL : size);
        this.playerModel.getModel().metadata.setTail(TailLengths.STUB);
        this.playerModel.getModel().metadata.setGlowColor(rand.nextInt());
    }

    @Override
    protected ResourceLocation getEntityTexture(Skeleton entity) {
        return getTexture(SKELETON);
    }

    public static class Stray extends RenderPonySkeleton<EntityStray> {

        public Stray(RenderManager rm) {
            super(rm);
        }

        @Override
        protected ResourceLocation getEntityTexture(EntityStray entity) {
            return getTexture(STRAY);
        }
    }

    public static class Wither extends RenderPonySkeleton<EntityWitherSkeleton> {

        public Wither(RenderManager rm) {
            super(rm);
            this.addLayer(new LayerPonyStrayOverlay(this));
        }

        @Override
        protected ResourceLocation getEntityTexture(EntityWitherSkeleton entity) {
            return getTexture(WITHER);
        }

        @Override
        protected void preRenderCallback(EntityWitherSkeleton skeleton, float partialTicks) {
            super.preRenderCallback(skeleton, partialTicks);
            GlStateManager.scale(1.2F, 1.2F, 1.2F);
        }

    }

}
