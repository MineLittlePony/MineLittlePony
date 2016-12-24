package com.minelittlepony.renderer;

import com.minelittlepony.PonyGender;
import com.minelittlepony.PonyRace;
import com.minelittlepony.PonySize;
import com.minelittlepony.TailLengths;
import com.minelittlepony.model.PMAPI;
import com.minelittlepony.renderer.layer.LayerPonyStrayOverlay;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.entity.layers.LayerBipedArmor;
import net.minecraft.entity.monster.EntitySkeleton;
import net.minecraft.entity.monster.SkeletonType;
import net.minecraft.util.ResourceLocation;

import java.util.Random;

public class RenderPonySkeleton<Skeleton extends EntitySkeleton> extends RenderPonyMob<Skeleton> {

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
        this.addLayer(new LayerPonyStrayOverlay(this));
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

        if (skeleton.getSkeletonType() == SkeletonType.WITHER) {
            GlStateManager.scale(1.2F, 1.2F, 1.2F);
        }
    }

    @Override
    protected ResourceLocation getEntityTexture(Skeleton entity) {
        return getTexture(getResource(entity));
    }

    private ResourceLocation getResource(Skeleton entity) {
        switch (entity.getSkeletonType()) {
            case WITHER:
                return WITHER;
            case STRAY:
                return STRAY;
            case NORMAL:
            default:
                return SKELETON;
        }
    }

}
