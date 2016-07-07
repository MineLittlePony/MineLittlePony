package com.brohoof.minelittlepony.renderer;

import java.util.Random;

import com.brohoof.minelittlepony.PonyGender;
import com.brohoof.minelittlepony.PonyManager;
import com.brohoof.minelittlepony.PonyRace;
import com.brohoof.minelittlepony.PonySize;
import com.brohoof.minelittlepony.TailLengths;
import com.brohoof.minelittlepony.model.PMAPI;
import com.brohoof.minelittlepony.renderer.layer.LayerPonySkeletonOverlay;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.entity.layers.LayerBipedArmor;
import net.minecraft.entity.monster.EntitySkeleton;
import net.minecraft.entity.monster.SkeletonType;
import net.minecraft.util.ResourceLocation;

public class RenderPonySkeleton extends RenderPonyMob<EntitySkeleton> {
    public RenderPonySkeleton(RenderManager rm) {
        super(rm, PMAPI.skeleton);
        this.addLayer(new LayerBipedArmor(this) {
            @Override
            protected void initArmor() {
                this.modelLeggings = PMAPI.skeleton.getArmor().modelArmor;
                this.modelArmor = PMAPI.skeleton.getArmor().modelArmorChestplate;
            }
        });
        this.addLayer(new LayerPonySkeletonOverlay(this));
    }

    @Override
    protected void preRenderCallback(EntitySkeleton skeleton, float partialTicks) {
        super.preRenderCallback(skeleton, partialTicks);
        if (skeleton.getSkeletonType() == SkeletonType.WITHER) {
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
        this.playerModel.getModel().metadata.setSize(size == PonySize.FOAL ? PonySize.NORMAL : size);
        this.playerModel.getModel().metadata.setTail(TailLengths.STUB);
        this.playerModel.getModel().metadata.setGlowColor(rand.nextInt());
    }

    @Override
    protected ResourceLocation getEntityTexture(EntitySkeleton skeleton) {
        SkeletonType type = skeleton.getSkeletonType();
        if (type == SkeletonType.WITHER)
            return PonyManager.WITHER_SKELETON;
        else if (type == SkeletonType.STRAY)
            return PonyManager.STRAY_SKELETON;
        else
            return PonyManager.SKELETON;
    }
}
