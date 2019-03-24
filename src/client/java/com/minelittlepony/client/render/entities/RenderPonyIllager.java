package com.minelittlepony.client.render.entities;

import com.minelittlepony.client.model.ModelWrapper;
import com.minelittlepony.client.model.entities.ModelIllagerPony;
import com.minelittlepony.client.render.RenderPonyMob;
import com.minelittlepony.client.render.layer.LayerHeldItemIllager;
import com.minelittlepony.client.render.layer.LayerHeldPonyItem;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.monster.AbstractIllager;
import net.minecraft.entity.monster.EntityEvoker;
import net.minecraft.entity.monster.EntityIllusionIllager;
import net.minecraft.entity.monster.EntityVindicator;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;

public abstract class RenderPonyIllager<T extends AbstractIllager> extends RenderPonyMob<T> {

    public static final ResourceLocation ILLUSIONIST = new ResourceLocation("minelittlepony", "textures/entity/illager/illusionist_pony.png");
    public static final ResourceLocation EVOKER = new ResourceLocation("minelittlepony", "textures/entity/illager/evoker_pony.png");
    public static final ResourceLocation VINDICATOR = new ResourceLocation("minelittlepony", "textures/entity/illager/vindicator_pony.png");

    private static final ModelWrapper MODEL_WRAPPER = new ModelWrapper(new ModelIllagerPony());

    public RenderPonyIllager(RenderManager manager) {
        super(manager, MODEL_WRAPPER);
    }

    @Override
    protected LayerHeldPonyItem<T> createItemHoldingLayer() {
        return new LayerHeldItemIllager<>(this);
    }

    @Override
    public void preRenderCallback(T entity, float ticks) {
        super.preRenderCallback(entity, ticks);
        GlStateManager.scalef(BASE_MODEL_SCALE, BASE_MODEL_SCALE, BASE_MODEL_SCALE);
    }

    public static class Vindicator extends RenderPonyIllager<EntityVindicator> {

        public Vindicator(RenderManager manager) {
            super(manager);

        }

        @Override
        public ResourceLocation getTexture(EntityVindicator entity) {
            return VINDICATOR;
        }
    }

    public static class Evoker extends RenderPonyIllager<EntityEvoker> {

        public Evoker(RenderManager manager) {
            super(manager);
        }

        @Override
        public ResourceLocation getTexture(EntityEvoker entity) {
            return EVOKER;
        }
    }

    public static class Illusionist extends RenderPonyIllager<EntityIllusionIllager> {

        public Illusionist(RenderManager manager) {
            super(manager);
        }

        @Override
        public ResourceLocation getTexture(EntityIllusionIllager entity) {
            return ILLUSIONIST;
        }

        @Override
        public void doRender(EntityIllusionIllager entity, double x, double y, double z, float yaw, float ticks) {
            if (entity.isInvisible()) {
                Vec3d[] clones = entity.getRenderLocations(ticks);
                float rotation = handleRotationFloat(entity, ticks);

                for (int i = 0; i < clones.length; ++i) {
                    super.doRender(entity,
                            x + clones[i].x + MathHelper.cos(i + rotation * 0.5F) * 0.025D,
                            y + clones[i].y + MathHelper.cos(i + rotation * 0.75F) * 0.0125D,
                            z + clones[i].z + MathHelper.cos(i + rotation * 0.7F) * 0.025D,
                            yaw, ticks);
                }
            } else {
                super.doRender(entity, x, y, z, yaw, ticks);
            }
        }

        @Override
        protected boolean isVisible(EntityIllusionIllager entity) {
            return true;
        }
    }
}
