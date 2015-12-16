package com.brohoof.minelittlepony.renderer;

import java.util.Random;

import com.brohoof.minelittlepony.PonyGender;
import com.brohoof.minelittlepony.PonyManager;
import com.brohoof.minelittlepony.PonyRace;
import com.brohoof.minelittlepony.PonySize;
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
                this.field_177189_c = PMAPI.skeleton.getArmor().modelArmor;
                this.field_177186_d = PMAPI.skeleton.getArmor().modelArmorChestplate;
            }
        });
    }

    @Override
    protected void preRenderCallback(EntitySkeleton skeleton, float partialTicks) {
        if (skeleton.getSkeletonType() == 1) {
            GlStateManager.scale(1.2F, 1.2F, 1.2F);
        }

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
        this.playerModel.getModel().metadata.setSize(size== PonySize.FOAL ? PonySize.NORMAL : size);
        this.playerModel.getModel().metadata.setTail(TailLengths.STUB);
        this.playerModel.getModel().metadata.setGlowColor(rand.nextInt());
    }

    @Override
    protected ResourceLocation getEntityTexture(EntitySkeleton skeleton) {
        return skeleton.getSkeletonType() == 1 ? PonyManager.WITHER_SKELETON : PonyManager.SKELETON;
    }
}
