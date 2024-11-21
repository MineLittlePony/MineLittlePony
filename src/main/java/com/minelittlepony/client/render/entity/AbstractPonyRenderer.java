package com.minelittlepony.client.render.entity;

import com.minelittlepony.api.model.ModelAttributes;
import com.minelittlepony.api.pony.Pony;
import com.minelittlepony.api.pony.meta.Wearable;
import com.minelittlepony.client.model.*;
import com.minelittlepony.client.render.DebugBoundingBoxRenderer;
import com.minelittlepony.client.render.PonyRenderContext;
import com.minelittlepony.client.render.EquineRenderManager;
import com.minelittlepony.client.render.entity.feature.*;
import com.minelittlepony.client.render.entity.npc.textures.TextureSupplier;
import com.minelittlepony.client.render.entity.state.PonyRenderState;
import com.minelittlepony.mson.api.ModelKey;

import java.util.*;
import java.util.function.*;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.Frustum;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.MobEntityRenderer;
import net.minecraft.client.render.entity.feature.FeatureRenderer;
import net.minecraft.client.render.entity.state.PlayerEntityRenderState;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

public abstract class AbstractPonyRenderer<
        T extends MobEntity,
        S extends PonyRenderState,
        M extends ClientPonyModel<S>
    > extends MobEntityRenderer<T, S, M> implements PonyRenderContext<T, S, M> {

    protected final EquineRenderManager<T, S, M> manager;

    private final Map<Wearable, Identifier> wearableTextures = new EnumMap<>(Wearable.class);

    private final TextureSupplier<T> texture;

    private final float scale;

    public AbstractPonyRenderer(EntityRendererFactory.Context context, ModelKey<? super M> key, TextureSupplier<T> texture, float scale) {
        super(context, null, 0.5F);
        this.manager = new EquineRenderManager<T, S, M>(this, super::setupTransforms, key);
        this.texture = texture;
        this.scale = scale;
        addFeatures(context);
    }

    @Override
    public void updateRenderState(T entity, S state, float tickDelta) {
        super.updateRenderState(entity, state, tickDelta);
        manager.updateState(entity, state, ModelAttributes.Mode.THIRD_PERSON);
    }

    protected void addFeatures(EntityRendererFactory.Context context) {
        addFeature(new ArmourFeature<>(this, context.getEquipmentModelLoader()));
        addPonyFeature(createHeldItemFeature(context));
        addFeature(new SkullFeature<>(this, context.getModelLoader(), context.getItemRenderer()));
        addPonyFeature(new ElytraFeature<>(this, context.getEquipmentRenderer()));
        addFeature(new GearFeature<>(this));
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    protected final boolean addPonyFeature(FeatureRenderer<
                ? extends PlayerEntityRenderState,
                ? extends ClientPonyModel<? extends PlayerEntityRenderState>
            > feature) {
        return ((List)features).add(feature);
    }

    protected HeldItemFeature<S, M> createHeldItemFeature(EntityRendererFactory.Context context) {
        return new HeldItemFeature<>(this, context.getItemRenderer());
    }

    @Override
    public final Identifier getTexture(S entity) {
        return entity.pony.texture();
    }

    @Override
    public void render(S state, MatrixStack stack, VertexConsumerProvider vertices, int light) {
        super.render(state, stack, vertices, light);
        DebugBoundingBoxRenderer.render(state, stack, vertices);
    }

    @Override
    protected void setupTransforms(S state, MatrixStack stack, float animationProgress, float bodyYaw) {
        manager.setupTransforms(state, stack, animationProgress, bodyYaw);
    }

    @Override
    public boolean shouldRender(T state, Frustum visibleRegion, double camX, double camY, double camZ) {
        return super.shouldRender(state, manager.getFrustrum(state, visibleRegion), camX, camY, camZ);
    }

    @Override
    public void scale(S state, MatrixStack stack) {
        shadowRadius = state.getShadowSize();

        if (state.baby) {
            shadowRadius *= 3; // undo vanilla shadow scaling
        }

        if (!state.hasVehicle) {
            stack.translate(0, 0, -state.width / 2); // move us to the center of the shadow
        } else {
            if (state.attributes.isSitting && state.hasVehicle) {
                stack.translate(0, 0.25F, 0);
            }
        }

        stack.scale(scale, scale, scale);
    }

    @Override
    protected void renderLabelIfPresent(S state, Text name, MatrixStack matrices, VertexConsumerProvider vertices, int light) {
        matrices.push();
        matrices.translate(0, state.nameplateYOffset, 0);
        super.renderLabelIfPresent(state, name, matrices, vertices, light);
        matrices.pop();
    }

    @Override
    public Identifier getDefaultTexture(S state, Wearable wearable) {
        return wearableTextures.computeIfAbsent(wearable, w -> {
            Identifier texture = getTexture(state).withPath(path -> path.split("\\.")[0] + "_" + wearable.name().toLowerCase(Locale.ROOT) + ".png");

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
    public EquineRenderManager<T, S, M> getInternalRenderer() {
        return manager;
    }

    @Override
    public Pony getEntityPony(T entity) {
        return Pony.getManager().getPony(texture.apply(entity));
    }

    public static <E extends MobEntity, C extends PonyRenderState, M extends ClientPonyModel<C>, T extends PonyRenderer<E, C, M>, F extends FeatureRenderer<C, M>>
            T appendFeature(T renderer, Function<T, F> featureFactory) {
        renderer.addFeature(featureFactory.apply(renderer));
        return renderer;
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    public static <
            T extends MobEntity,
            S extends PonyRenderState,
            M extends ClientPonyModel<S>> AbstractPonyRenderer<T, S, M> proxy(
                    EntityRendererFactory.Context context, ModelKey<? super M> key,
                    TextureSupplier<T> texture,
                    float scale,
                    List exportedLayers,
                    Consumer<M> modelConsumer,
                    Supplier<S> renderStateSupplier) {
        return new AbstractPonyRenderer<T, S, M>(context, key, texture, scale) {
            {
                exportedLayers.clear();
                exportedLayers.addAll(features);
                modelConsumer.accept(getModel());
            }
            @Override
            protected void addFeatures(EntityRendererFactory.Context context) {
                features.clear();
                super.addFeatures(context);
            }

            @Override
            public S createRenderState() {
                return renderStateSupplier.get();
            }
        };
    }
}
