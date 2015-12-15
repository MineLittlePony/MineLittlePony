package com.brohoof.minelittlepony.renderer;

import java.util.Random;

import com.brohoof.minelittlepony.PonyManager;
import com.brohoof.minelittlepony.PonyRace;
import com.brohoof.minelittlepony.TailLengths;
import com.brohoof.minelittlepony.model.PMAPI;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.entity.layers.LayerBipedArmor;
import net.minecraft.entity.monster.EntitySkeleton;
import net.minecraft.util.ResourceLocation;

public class RenderPonySkeleton extends RenderPonyMob<EntitySkeleton> {
    public RenderPonySkeleton(RenderManager rm) {
        super(rm, PMAPI.skeleton);
        addLayer(new LayerBipedArmor(this) {
            @Override
            protected void initArmor() {
                this.field_177189_c = PMAPI.skeleton.getModel();
                this.field_177186_d = PMAPI.skeleton.getModel();
            }
        });
    }

    @Override
    protected void preRenderCallback(EntitySkeleton skeleton, float partialTicks) {
        if (skeleton.getSkeletonType() == 1) {
            GlStateManager.scale(1.2F, 1.2F, 1.2F);
        }

        Random rand = new Random(skeleton.getUniqueID().hashCode());
        switch (rand.nextInt() % 3) {
        case 0:
        case 1:
            this.playerModel.getModel().metadata.setRace(PonyRace.UNICORN);
            this.playerModel.getModel().metadata.setGlowColor(rand.nextInt());
            break;
        case 2:
            this.playerModel.getModel().metadata.setRace(PonyRace.EARTH);
        }
        this.playerModel.getModel().metadata.setTail(TailLengths.STUB);
    }

    @Override
    protected ResourceLocation getEntityTexture(EntitySkeleton skeleton) {
        return skeleton.getSkeletonType() == 1 ? PonyManager.WITHER_SKELETON : PonyManager.SKELETON;
    }
}
