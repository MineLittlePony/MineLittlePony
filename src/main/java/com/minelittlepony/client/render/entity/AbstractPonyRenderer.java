package com.minelittlepony.client.render.entity;

import com.minelittlepony.api.model.ModelAttributes;
import com.minelittlepony.api.model.PonyModel;
import com.minelittlepony.api.pony.Pony;
import com.minelittlepony.api.pony.meta.Wearable;
import com.minelittlepony.client.model.*;
import com.minelittlepony.client.render.DebugBoundingBoxRenderer;
import com.minelittlepony.client.render.PonyRenderContext;
import com.minelittlepony.client.render.EquineRenderManager;
import com.minelittlepony.client.render.entity.feature.*;
import com.minelittlepony.client.render.entity.npc.textures.TextureSupplier;
import com.minelittlepony.mson.api.ModelKey;

import java.util.*;
import java.util.function.Consumer;
import java.util.function.Function;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.Frustum;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.MobEntityRenderer;
import net.minecraft.client.render.entity.feature.FeatureRenderer;
import net.minecraft.client.render.entity.model.*;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

public abstract class AbstractPonyRenderer<T extends MobEntity, M extends EntityModel<T> & PonyModel<T> & ModelWithArms> extends MobEntityRenderer<T, M> implements PonyRenderContext<T, M> {

    protected final EquineRenderManager<T, M> manager;

    private final Map<Wearable, Identifier> wearableTextures = new EnumMap<>(Wearable.class);

    private final TextureSupplier<T> texture;

    private final float scale;

    public AbstractPonyRenderer(EntityRendererFactory.Context context, ModelKey<? super M> key, TextureSupplier<T> texture, float scale) {
        super(context, null, 0.5F);
        this.manager = new EquineRenderManager<>(this, super::setupTransforms, key);
        this.texture = texture;
        this.scale = scale;
        addFeatures(context);
    }

    protected void addFeatures(EntityRendererFactory.Context context) {
        addFeature(new ArmourFeature<>(this, context.getModelManager()));
        addFeature(createHeldItemFeature(context));
        addFeature(new SkullFeature<>(this, context.getModelLoader()));
        addFeature(new ElytraFeature<>(this));
        addFeature(new GearFeature<>(this));
    }

    protected HeldItemFeature<T, M> createHeldItemFeature(EntityRendererFactory.Context context) {
        return new HeldItemFeature<>(this, context.getHeldItemRenderer());
    }

    @Override
    public final Identifier getTexture(T entity) {
        return texture.apply(entity);
    }

    @Override
    public void render(T entity, float entityYaw, float tickDelta, MatrixStack stack, VertexConsumerProvider renderContext, int lightUv) {
        manager.preRender(entity, ModelAttributes.Mode.THIRD_PERSON);
        if (manager.getModels().body() instanceof BipedEntityModel model) {
            model.setVisible(true);
        }
        super.render(entity, entityYaw, tickDelta, stack, renderContext, lightUv);
        DebugBoundingBoxRenderer.render(getEntityPony(entity), this, entity, stack, renderContext, tickDelta);
    }

    @Override
    protected void setupTransforms(T entity, MatrixStack stack, float ageInTicks, float rotationYaw, float partialTicks) {
        manager.setupTransforms(entity, stack, ageInTicks, rotationYaw, partialTicks);
    }

    @Override
    public boolean shouldRender(T entity, Frustum visibleRegion, double camX, double camY, double camZ) {
        return super.shouldRender(entity, manager.getFrustrum(entity, visibleRegion), camX, camY, camZ);
    }

    @Override
    public void scale(T entity, MatrixStack stack, float tickDelta) {
        shadowRadius = manager.getShadowSize();

        if (entity.isBaby()) {
            shadowRadius *= 3; // undo vanilla shadow scaling
        }

        if (!entity.hasVehicle()) {
            stack.translate(0, 0, -entity.getWidth() / 2); // move us to the center of the shadow
        } else {
            stack.translate(0, entity.getRidingOffset(entity.getVehicle()), 0);
        }

        stack.scale(scale, scale, scale);
    }

    @Override
    protected void renderLabelIfPresent(T entity, Text name, MatrixStack stack, VertexConsumerProvider renderContext, int maxDistance) {
        stack.push();
        stack.translate(0, manager.getNamePlateYOffset(entity), 0);
        super.renderLabelIfPresent(entity, name, stack, renderContext, maxDistance);
        stack.pop();
    }

    @Override
    public Identifier getDefaultTexture(T entity, Wearable wearable) {
        return wearableTextures.computeIfAbsent(wearable, w -> {
            Identifier texture = getTexture(entity).withPath(path -> path.split("\\.")[0] + "_" + wearable.name().toLowerCase(Locale.ROOT) + ".png");

            if (MinecraftClient.getInstance().getResourceManager().getResource(texture).isPresent()) {
                return texture;
            }
            return wearable.getDefaultTexture();
        });
    }

    @Override
    public void setModel(M model) {
        this.model = model;
    }

    @Override
    public EquineRenderManager<T, M> getInternalRenderer() {
        return manager;
    }

    @Override
    public Pony getEntityPony(T entity) {
        return Pony.getManager().getPony(getTexture(entity));
    }

    public static <E extends MobEntity, M extends ClientPonyModel<E>, T extends PonyRenderer<E, M>, F extends FeatureRenderer<E, M>>
            T appendFeature(T renderer, Function<T, F> featureFactory) {
        renderer.addFeature(featureFactory.apply(renderer));
        return renderer;
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    public static <T extends MobEntity, M extends EntityModel<T> & PonyModel<T> & ModelWithArms> AbstractPonyRenderer<T, M> proxy(EntityRendererFactory.Context context, ModelKey<? super M> key, TextureSupplier<T> texture, float scale,
            List exportedLayers, Consumer<M> modelConsumer) {
        var renderer = new AbstractPonyRenderer<T, M>(context, key, texture, scale) {
            @Override
            protected void addFeatures(EntityRendererFactory.Context context) {
                features.clear();
                super.addFeatures(context);
            }
        };
        exportedLayers.clear();
        exportedLayers.addAll(renderer.features);
        modelConsumer.accept(renderer.getModel());
        return renderer;
    }
}
