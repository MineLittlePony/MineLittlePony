package com.minelittlepony.client.render.entity;

import com.minelittlepony.client.model.ModelType;
import com.minelittlepony.client.model.entity.SkeleponyModel;
import com.minelittlepony.client.render.entity.feature.StrayClothingFeature;
import com.minelittlepony.client.render.entity.npc.textures.TextureSupplier;

import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.entity.mob.*;
import net.minecraft.util.Identifier;

public class SkeleponyRenderer<Skeleton extends AbstractSkeletonEntity> extends PonyRenderer<Skeleton, SkeleponyModel<Skeleton>> {

    public static final Identifier SKELETON = new Identifier("minelittlepony", "textures/entity/skeleton/skeleton_pony.png");
    public static final Identifier WITHER = new Identifier("minelittlepony", "textures/entity/skeleton/skeleton_wither_pony.png");
    public static final Identifier STRAY = new Identifier("minelittlepony", "textures/entity/skeleton/stray_pony.png");

    public SkeleponyRenderer(EntityRendererFactory.Context context, Identifier texture, float scale) {
        super(context, ModelType.SKELETON, TextureSupplier.of(texture), scale);
    }

    public static SkeleponyRenderer<SkeletonEntity> skeleton(EntityRendererFactory.Context context) {
        return new SkeleponyRenderer<>(context, SKELETON, 1);
    }

    public static SkeleponyRenderer<StrayEntity> stray(EntityRendererFactory.Context context) {
        return PonyRenderer.appendFeature(new SkeleponyRenderer<>(context, STRAY, 1), StrayClothingFeature::new);
    }

    public static SkeleponyRenderer<WitherSkeletonEntity> wither(EntityRendererFactory.Context context) {
        return new SkeleponyRenderer<>(context, WITHER, 1.2F);
    }
}
