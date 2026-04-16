package com.minelittlepony.client.render.entity;

import com.minelittlepony.api.model.ModelAttributes;
import com.minelittlepony.api.model.ModelWithHorn;
import com.minelittlepony.api.pony.Pony;
import com.minelittlepony.api.pony.meta.Wearable;
import com.minelittlepony.client.model.*;
import com.minelittlepony.client.render.PonyRenderContext;
import com.minelittlepony.client.render.EquineRenderManager;
import com.minelittlepony.client.render.entity.feature.*;
import com.minelittlepony.client.render.entity.npc.textures.TextureSupplier;
import com.minelittlepony.client.render.entity.state.PonyRenderState;
import com.minelittlepony.mson.api.ModelKey;

import java.util.*;
import java.util.function.*;

import net.minecraft.client.item.ItemModelManager;
import net.minecraft.client.render.command.OrderedRenderCommandQueue;
import net.minecraft.client.render.entity.*;
import net.minecraft.client.render.entity.feature.FeatureRenderer;
import net.minecraft.client.render.entity.feature.HeadFeatureRenderer;
import net.minecraft.client.render.entity.state.PlayerEntityRenderState;
import net.minecraft.client.render.state.CameraRenderState;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.item.Items;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.Atlases;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Box;

public abstract class AbstractPonyRenderer<
        T extends MobEntity,
        S extends PonyRenderState,
        M extends ClientPonyModel<S>
    > extends MobEntityRenderer<T, S, M> implements PonyRenderContext<T, S, M> {

    protected final EquineRenderManager<T, S, M> manager;

    private final Map<Identifier, Identifier> wearableTextures = new HashMap<>();

    private final TextureSupplier<T> texture;

    private final ResourceManager resources;

    protected final ItemModelManager itemModelManager;

    private final float scale;

    public AbstractPonyRenderer(EntityRendererFactory.Context context, ModelKey<? super M> key, TextureSupplier<T> texture, float scale) {
        super(context, null, 0.5F);
        this.manager = new EquineRenderManager<T, S, M>(this, super::setupTransforms, key);
        this.texture = texture;
        this.scale = scale;
        resources = context.getResourceManager();
        itemModelManager = context.getItemModelManager();
        addFeatures(context);
    }

    @Override
    public void updateRenderState(T entity, S state, float tickDelta) {
        super.updateRenderState(entity, state, tickDelta);
        BipedEntityRenderer.updateBipedRenderState(entity, state, tickDelta, itemModelManager);
        manager.updateState(entity, state, ModelAttributes.Mode.THIRD_PERSON, itemModelManager);
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    protected void addFeatures(EntityRendererFactory.Context context) {
        addFeature(new ArmourFeature<>(this, context.getEquipmentModelLoader(), context.getSpriteAtlasTexture(Atlases.ARMOR_TRIMS)));
        addPonyFeature(createHeldItemFeature(context));
        addFeature(createSkullFeature(context));
        addPonyFeature(new ElytraFeature<>(this, context.getEquipmentRenderer()));
        addFeature(new GearFeature<>(this));
        addPonyFeature(new PonyBodyPartFeature<>(this, m -> m instanceof ModelWithHorn, m -> ((ModelWithHorn)m).getHorn()));
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    protected final boolean addPonyFeature(FeatureRenderer<
                ? extends PlayerEntityRenderState,
                ? extends ClientPonyModel<? extends PlayerEntityRenderState>
            > feature) {
        return ((List)features).add(feature);
    }

    protected SkullFeature<S, M> createSkullFeature(EntityRendererFactory.Context context) {
        return new SkullFeature<>(this, context.getPlayerSkinCache(), context.getEntityModels(), HeadFeatureRenderer.HeadTransformation.DEFAULT, true);
    }

    protected HeldItemFeature<S, M> createHeldItemFeature(EntityRendererFactory.Context context) {
        return new HeldItemFeature<>(this);
    }

    @Override
    public final Identifier getTexture(S entity) {
        return entity.pony.texture();
    }

    @Override
    public void render(S state, MatrixStack stack, OrderedRenderCommandQueue queue, CameraRenderState camera) {
        model = lookupModel(state).body();
        super.render(state, stack, queue, camera);
    }

    @Override
    protected void setupTransforms(S state, MatrixStack stack, float animationProgress, float bodyYaw) {
        manager.setupTransforms(state, stack, animationProgress, bodyYaw);
    }

    @Override
    protected final Box getBoundingBox(T entity) {
        Box box = manager.getBoundingBox(entity, getUnscaledBoundingBox(entity));
        if (entity.getEquippedStack(EquipmentSlot.HEAD).isOf(Items.DRAGON_HEAD)) {
            return box.expand(0.5, 0.5, 0.5);
        }
        return box;
    }

    protected Box getUnscaledBoundingBox(T entity) {
        return entity.getBoundingBox();
    }

    @Override
    public void scale(S state, MatrixStack stack) {
        shadowRadius = state.attributes.size.shadowSize();

        if (state.baby) {
            shadowRadius *= 3; // undo vanilla shadow scaling
        }

        if (!state.hasVehicle && !state.attributes.isLyingDown) {
            stack.translate(0, 0, -state.width / 2); // move us to the center of the shadow
        } else {
            if (state.attributes.isSitting && state.hasVehicle) {
                stack.translate(0, 0.25F, 0);
            }
            stack.translate(0, state.vehicleOffset, 0);
        }

        stack.scale(scale, scale, scale);
    }

    @Override
    protected void renderLabelIfPresent(S state, MatrixStack matrices, OrderedRenderCommandQueue queue, CameraRenderState camera) {
        matrices.push();
        matrices.translate(0, state.nameplateYOffset, 0);
        super.renderLabelIfPresent(state, matrices, queue, camera);
        matrices.pop();
    }

    @Override
    public Identifier getDefaultTexture(S state, Wearable wearable) {
        Identifier texture = getTexture(state).withPath(path -> path.split("\\.")[0] + "_" + wearable.name().toLowerCase(Locale.ROOT) + ".png");
        return wearableTextures.computeIfAbsent(texture, t -> resources.getResource(t).isPresent() ? t : wearable.getDefaultTexture());
    }

    @Override
    public EquineRenderManager<T, S, M> getEquineManager() {
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

    @Deprecated
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
