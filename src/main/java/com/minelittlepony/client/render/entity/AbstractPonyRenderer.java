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
import com.mojang.blaze3d.vertex.PoseStack;

import java.util.*;
import java.util.function.*;

import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.*;
import net.minecraft.client.renderer.entity.layers.CustomHeadLayer;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.entity.state.AvatarRenderState;
import net.minecraft.client.renderer.item.ItemModelResolver;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.data.AtlasIds;
import net.minecraft.resources.Identifier;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.item.Items;
import net.minecraft.world.phys.AABB;

public abstract class AbstractPonyRenderer<
        T extends Mob,
        S extends PonyRenderState,
        M extends ClientPonyModel<S>
    > extends MobRenderer<T, S, M> implements PonyRenderContext<T, S, M> {

    protected final EquineRenderManager<T, S, M> manager;

    private final Map<Identifier, Identifier> wearableTextures = new HashMap<>();

    private final TextureSupplier<T> texture;

    private final ResourceManager resources;

    protected final ItemModelResolver itemModelManager;

    private final float scale;

    public AbstractPonyRenderer(EntityRendererProvider.Context context, ModelKey<? super M> key, TextureSupplier<T> texture, float scale) {
        super(context, null, 0.5F);
        this.manager = new EquineRenderManager<T, S, M>(this, super::setupRotations, key);
        this.texture = texture;
        this.scale = scale;
        resources = context.getResourceManager();
        itemModelManager = context.getItemModelResolver();
        addFeatures(context);
    }

    @Override
    public void extractRenderState(T entity, S state, float tickDelta) {
        super.extractRenderState(entity, state, tickDelta);
        HumanoidMobRenderer.extractHumanoidRenderState(entity, state, tickDelta, itemModelManager);
        manager.updateState(entity, state, ModelAttributes.Mode.THIRD_PERSON, itemModelManager);
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    protected void addFeatures(EntityRendererProvider.Context context) {
        addLayer(new ArmourFeature<>(this, context.getEquipmentAssets(), context.getAtlas(AtlasIds.ARMOR_TRIMS)));
        addPonyFeature(createHeldItemFeature(context));
        addLayer(createSkullFeature(context));
        addPonyFeature(new ElytraFeature<>(this, context.getEquipmentRenderer()));
        addLayer(new GearFeature<>(this));
        addPonyFeature(new PonyBodyPartFeature<>(this, m -> ((ModelWithHorn)m).getHorn(), m -> m instanceof ModelWithHorn));
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    protected final boolean addPonyFeature(RenderLayer<
                ? extends AvatarRenderState,
                ? extends ClientPonyModel<? extends AvatarRenderState>
            > feature) {
        return ((List)layers).add(feature);
    }

    protected SkullFeature<S, M> createSkullFeature(EntityRendererProvider.Context context) {
        return new SkullFeature<>(this, context.getPlayerSkinRenderCache(), context.getModelSet(), CustomHeadLayer.Transforms.DEFAULT, true);
    }

    protected HeldItemFeature<S, M> createHeldItemFeature(EntityRendererProvider.Context context) {
        return new HeldItemFeature<>(this);
    }

    @Override
    public final Identifier getTextureLocation(S entity) {
        return entity.pony.texture();
    }

    @Override
    public void submit(S state, PoseStack stack, SubmitNodeCollector frame, CameraRenderState camera) {
        model = lookupModel(state).body();
        super.submit(state, stack, frame, camera);
    }

    @Override
    protected void setupRotations(S state, PoseStack stack, float bodyRot, float entityScale) {
        manager.setupTransforms(state, stack, bodyRot, entityScale);
    }

    @Override
    protected final AABB getBoundingBoxForCulling(T entity) {
        AABB box = manager.getBoundingBox(entity, getUnscaledBoundingBox(entity));
        if (entity.getItemBySlot(EquipmentSlot.HEAD).is(Items.DRAGON_HEAD)) {
            return box.inflate(0.5, 0.5, 0.5);
        }
        return box;
    }

    protected AABB getUnscaledBoundingBox(T entity) {
        return entity.getBoundingBox();
    }

    @Override
    public void scale(S state, PoseStack stack) {
        shadowRadius = state.attributes.size.shadowSize();

        if (state.isBaby) {
            shadowRadius *= 3; // undo vanilla shadow scaling
        }

        if (!state.isPassenger && !state.attributes.isLyingDown) {
            stack.translate(0, 0, -state.boundingBoxWidth / 2); // move us to the center of the shadow
        } else {
            if (state.attributes.isSitting && state.isPassenger) {
                stack.translate(0, 0.25F, 0);
            }
        }

        stack.scale(scale, scale, scale);
    }

    @Override
    protected void submitNameDisplay(S state, PoseStack matrices, SubmitNodeCollector frame, CameraRenderState camera) {
        matrices.pushPose();
        matrices.translate(0, state.nameplateYOffset, 0);
        super.submitNameDisplay(state, matrices, frame, camera);
        matrices.popPose();
    }

    @Override
    public Identifier getDefaultTexture(S state, Wearable wearable) {
        Identifier texture = getTextureLocation(state).withPath(path -> path.split("\\.")[0] + "_" + wearable.name().toLowerCase(Locale.ROOT) + ".png");
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

    public static <E extends Mob,
                C extends PonyRenderState,
                M extends ClientPonyModel<C>,
                T extends PonyRenderer<E, C, M>,
                F extends RenderLayer<C, M>>
            T appendFeature(T renderer, Function<T, F> featureFactory) {
        renderer.addLayer(featureFactory.apply(renderer));
        return renderer;
    }

    @Deprecated(forRemoval = true)
    @SuppressWarnings({"unchecked", "rawtypes"})
    public static <
            T extends Mob,
            S extends PonyRenderState,
            M extends ClientPonyModel<S>> AbstractPonyRenderer<T, S, M> proxy(
                    EntityRendererProvider.Context context, ModelKey<? super M> key,
                    TextureSupplier<T> texture,
                    float scale,
                    List exportedLayers,
                    Consumer<M> modelConsumer,
                    Supplier<S> renderStateSupplier) {
        return new AbstractPonyRenderer<T, S, M>(context, key, texture, scale) {
            {
                exportedLayers.clear();
                exportedLayers.addAll(layers);
                modelConsumer.accept(getModel());
            }
            @Override
            protected void addFeatures(EntityRendererProvider.Context context) {
                layers.clear();
                super.addFeatures(context);
            }

            @Override
            public S createRenderState() {
                return renderStateSupplier.get();
            }
        };
    }
}
