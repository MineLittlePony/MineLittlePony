package com.minelittlepony.client.render.entities;

import com.minelittlepony.client.model.ModelWrapper;
import com.minelittlepony.client.model.entities.ModelIllagerPony;
import com.minelittlepony.client.render.RenderPonyMob;
import com.minelittlepony.client.render.layer.LayerHeldItemIllager;
import com.minelittlepony.client.render.layer.LayerHeldPonyItem;
import com.mojang.blaze3d.platform.GlStateManager;

import net.minecraft.client.render.entity.EntityRenderDispatcher;
import net.minecraft.entity.mob.EvokerEntity;
import net.minecraft.entity.mob.IllagerEntity;
import net.minecraft.entity.mob.IllusionerEntity;
import net.minecraft.entity.mob.VindicatorEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;

public abstract class RenderPonyIllager<T extends IllagerEntity> extends RenderPonyMob<T, ModelIllagerPony<T>> {

    public static final Identifier ILLUSIONIST = new Identifier("minelittlepony", "textures/entity/illager/illusionist_pony.png");
    public static final Identifier EVOKER = new Identifier("minelittlepony", "textures/entity/illager/evoker_pony.png");
    public static final Identifier VINDICATOR = new Identifier("minelittlepony", "textures/entity/illager/vindicator_pony.png");

    public RenderPonyIllager(EntityRenderDispatcher manager) {
        super(manager, new ModelWrapper<>(new ModelIllagerPony<>()));
    }

    @Override
    protected LayerHeldPonyItem<T, ModelIllagerPony<T>> createItemHoldingLayer() {
        return new LayerHeldItemIllager<>(this);
    }

    @Override
    public void scale(T entity, float ticks) {
        super.scale(entity, ticks);
        GlStateManager.scalef(BASE_MODEL_SCALE, BASE_MODEL_SCALE, BASE_MODEL_SCALE);
    }

    public static class Vindicator extends RenderPonyIllager<VindicatorEntity> {

        public Vindicator(EntityRenderDispatcher manager) {
            super(manager);

        }

        @Override
        public Identifier findTexture(VindicatorEntity entity) {
            return VINDICATOR;
        }
    }

    public static class Evoker extends RenderPonyIllager<EvokerEntity> {

        public Evoker(EntityRenderDispatcher manager) {
            super(manager);
        }

        @Override
        public Identifier findTexture(EvokerEntity entity) {
            return EVOKER;
        }
    }

    public static class Illusionist extends RenderPonyIllager<IllusionerEntity> {

        public Illusionist(EntityRenderDispatcher manager) {
            super(manager);
        }

        @Override
        public Identifier findTexture(IllusionerEntity entity) {
            return ILLUSIONIST;
        }

        @Override
        public void render(IllusionerEntity entity, double x, double y, double z, float yaw, float ticks) {
            if (entity.isInvisible()) {
                Vec3d[] clones = entity.method_7065(ticks);
                float rotation = getAge(entity, ticks);

                for (int i = 0; i < clones.length; ++i) {
                    super.render(entity,
                            x + clones[i].x + MathHelper.cos(i + rotation * 0.5F) * 0.025D,
                            y + clones[i].y + MathHelper.cos(i + rotation * 0.75F) * 0.0125D,
                            z + clones[i].z + MathHelper.cos(i + rotation * 0.7F) * 0.025D,
                            yaw, ticks);
                }
            } else {
                super.render(entity, x, y, z, yaw, ticks);
            }
        }

        @Override
        protected boolean method_4056(IllusionerEntity entity) {
            return true;
        }
    }
}
