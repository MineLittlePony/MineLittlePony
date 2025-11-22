package com.minelittlepony.client.render.entity;

import com.google.common.collect.ImmutableList.Builder;
import com.minelittlepony.api.model.*;
import com.minelittlepony.api.model.ModelAttributes.Mode;
import com.minelittlepony.api.pony.Pony;
import com.minelittlepony.api.pony.meta.Wearable;
import com.minelittlepony.client.MineLittlePony;
import com.minelittlepony.client.model.*;
import com.minelittlepony.client.render.DebugBoundingBoxRenderer;
import com.minelittlepony.client.render.PonyRenderContext;
import com.minelittlepony.client.render.entity.feature.*;
import com.minelittlepony.client.render.entity.state.PlayerPonyRenderState;
import com.minelittlepony.client.render.entity.state.PonyRenderState;

import java.util.*;

import com.minelittlepony.client.render.EquineRenderManager;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.item.ItemModelManager;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.network.ClientPlayerLikeEntity;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.command.OrderedRenderCommandQueue;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.PlayerEntityRenderer;
import net.minecraft.client.render.entity.feature.*;
import net.minecraft.client.render.entity.state.*;
import net.minecraft.client.render.state.CameraRenderState;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.entity.EntityPose;
import net.minecraft.entity.PlayerLikeEntity;
import net.minecraft.util.*;
import net.minecraft.util.math.*;

import org.jetbrains.annotations.Nullable;
import org.joml.Quaternionf;
import org.joml.Vector3f;

public class PlayerPonyRenderer<Player extends PlayerLikeEntity & ClientPlayerLikeEntity>
        extends PlayerEntityRenderer<Player>
        implements PonyRenderContext<
            Player,
            PlayerPonyRenderState,
            ClientPonyModel<PlayerPonyRenderState>
        > {
    protected final EquineRenderManager<Player, PlayerPonyRenderState, ClientPonyModel<PlayerPonyRenderState>> manager;

    private ModelAttributes.Mode mode = ModelAttributes.Mode.THIRD_PERSON;
    protected final ItemModelManager itemModelManager;

    @SuppressWarnings({"unchecked", "rawtypes"})
    public PlayerPonyRenderer(EntityRendererFactory.Context context, boolean slim) {
        super(context, slim);
        itemModelManager = context.getItemModelManager();
        manager = new EquineRenderManager<>(this, super::setupTransforms, race -> ModelType.getPlayerModel(race).create(slim));

        // remove vanilla features (keep modded ones)
        features.removeIf(feature -> {
            return feature instanceof ArmorFeatureRenderer
                    || feature instanceof PlayerHeldItemFeatureRenderer
                    || feature instanceof Deadmau5FeatureRenderer
                    || feature instanceof CapeFeatureRenderer
                    || feature instanceof HeadFeatureRenderer
                    || feature instanceof ElytraFeatureRenderer
                    || feature instanceof ShoulderParrotFeatureRenderer;
        });
        addPonyFeature(new ArmourFeature<>(this, context.getEquipmentModelLoader(), context.getSpriteAtlasTexture(Atlases.ARMOR_TRIMS)));
        addPonyFeature(new HeldItemFeature<>(this));
        addPonyFeature(new DJPon3Feature<>(this));
        addFeature(new CapeFeature(this, context.getEntityModels(), context.getEquipmentModelLoader()));
        addPonyFeature(new SkullFeature<>(this, context.getPlayerSkinCache(), context.getEntityModels(), HeadFeatureRenderer.HeadTransformation.DEFAULT, true));
        addPonyFeature(new ElytraFeature(this, context.getEquipmentRenderer()));
        addPonyFeature(new PassengerFeature<>(this, context));
        addPonyFeature(new GearFeature<>(this));

        addPonyFeature(new PonyBodyPartFeature<>(this, m -> m instanceof ModelWithHorn, m -> ((ModelWithHorn)m).getHorn()));
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    protected final boolean addPonyFeature(FeatureRenderer<
            ? extends PlayerEntityRenderState,
            ? extends ClientPonyModel<? extends PonyRenderState>
            > feature) {
        return ((List)features).add(feature);
    }

    @Override
    public Vec3d getPositionOffset(PlayerEntityRenderState state) {
        Vec3d offset = super.getPositionOffset(state);
        return offset
                .multiply(((PlayerPonyRenderState)state).attributes.size.scaleFactor())
                .add(0, state.baseScale * ((PlayerPonyRenderState)state).yOffset, 0);
    }

    @Override
    public PlayerEntityRenderState createRenderState() {
        return new PlayerPonyRenderState();
    }

    @Override
    public void updateRenderState(Player entity, PlayerEntityRenderState state, float tickDelta) {
        super.updateRenderState(entity, state, tickDelta);
        manager.updateState(entity, (PlayerPonyRenderState)state, mode, itemModelManager);
    }

    public final PlayerPonyRenderState getAndUpdateRenderState(Player entity, float tickDelta, ModelAttributes.Mode mode) {
        try {
            this.mode = mode;
            return (PlayerPonyRenderState)getAndUpdateRenderState(entity, tickDelta);
        } finally {
            this.mode = ModelAttributes.Mode.THIRD_PERSON;
        }
    }

    @Override
    protected void setupTransforms(PlayerEntityRenderState state, MatrixStack matrices, float animationProgress, float bodyYaw) {
        manager.completeStateUpdate(state);
        model = lookupModel(state).body();
        shadowRadius = ((PlayerPonyRenderState)state).attributes.size.shadowSize();
        manager.setupTransforms((PlayerPonyRenderState)state, matrices, animationProgress, bodyYaw);
    }

    @Override
    protected Box getBoundingBox(Player entity) {
        return manager.getBoundingBox(entity, entity.getBoundingBox());
    }

    @Override
    protected void renderLabelIfPresent(PlayerEntityRenderState state, MatrixStack matrices, OrderedRenderCommandQueue queue, CameraRenderState camera) {
        matrices.push();
        matrices.translate(0, ((PlayerPonyRenderState)state).nameplateYOffset, 0);
        super.renderLabelIfPresent(state, matrices, queue, camera);
        matrices.pop();
    }

    @Override
    protected void appendHitboxes(Player entity, Builder<EntityHitbox> builder, float tickDelta) {
        super.appendHitboxes(entity, builder, tickDelta);
        DebugBoundingBoxRenderer.appendHitbox(entity, manager, builder, tickDelta);
    }

    @Override
    public final void renderRightArm(MatrixStack matrices, OrderedRenderCommandQueue queue, int light, Identifier skinTexture, boolean sleeveVisible) {
        renderArm(matrices, queue, light, skinTexture, sleeveVisible, Arm.RIGHT);
    }

    @Override
    public final void renderLeftArm(MatrixStack matrices, OrderedRenderCommandQueue queue, int light, Identifier skinTexture, boolean sleeveVisible) {
        renderArm(matrices, queue, light, skinTexture, sleeveVisible, Arm.LEFT);
    }

    @Nullable
    @SuppressWarnings("unchecked")
    public Vec3d getHandPos(Player player, Arm arm, float swingProgress, float tickDelta) {
        if (dispatcher.gameOptions.getPerspective().isFirstPerson() && player == MinecraftClient.getInstance().player) {
            return null;
        }

        var state = getAndUpdateRenderState(player, tickDelta, Mode.THIRD_PERSON);
        MatrixStack matrices = new MatrixStack();
        if (state.isInPose(EntityPose.SLEEPING)) {
            Direction direction = state.sleepingDirection;
            if (direction != null) {
                float bodyLength = state.standingEyeHeight - 0.1F;
                matrices.translate(-direction.getOffsetX() * bodyLength, 0, -direction.getOffsetZ() * bodyLength);
            }
        }
        float scale = state.baseScale;
        matrices.scale(scale, scale, scale);
        setupTransforms(state, matrices, state.bodyYaw, 0);
        matrices.scale(-1, -1, 1);
        scale(state, matrices);
        matrices.translate(0, -1.501F, 0);
        model.setAngles(state);
        ((ClientPonyModel<PlayerPonyRenderState>)model).transformHeldItem(state, arm, matrices);
        model.setArmAngle(state, arm, matrices);
        var a = arm == Arm.LEFT ? model.leftArm : model.rightArm;
        Quaternionf rotation = new Quaternionf().rotationZYX(a.roll, a.yaw, a.pitch);
        matrices.multiply(rotation);
        matrices.translate(0, -0.2F, -0.7F);
        matrices.multiply(rotation.conjugate());
        model.body.applyTransform(matrices);

        matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(-90));
        matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(180));
        boolean left = arm == Arm.LEFT;
        matrices.translate((left ? -1 : 1) / 16F, 0.125F, -0.625F);
        var vec = matrices.peek().getPositionMatrix().transformPosition(new Vector3f());
        var pos = new Vec3d(
                MathHelper.lerp(tickDelta, player.lastX, player.getX()),
                MathHelper.lerp(tickDelta, player.lastY, player.getY()),
                MathHelper.lerp(tickDelta, player.lastZ, player.getZ())
        );

        return pos.add(vec.x, vec.y, vec.z);
    }

    @SuppressWarnings("unchecked")
    protected void renderArm(MatrixStack stack, OrderedRenderCommandQueue queue, int light, Identifier skinTexture, boolean sleeveVisible, Arm side) {

        ClientPlayerEntity player = MinecraftClient.getInstance().player;

        var renderer = MineLittlePony.getInstance().getRenderDispatcher().getPonyRenderer(player);
        if (((Object)renderer) != this) {
            return;
        }
        PonyRenderState state = renderer.getAndUpdateRenderState(player, MinecraftClient.getInstance().getRenderTickCounter().getTickProgress(false));

        if (state.hasMagicGlow() && (player.getStackInHand(Hand.MAIN_HAND).contains(DataComponentTypes.MAP_ID) || player.getStackInHand(Hand.OFF_HAND).contains(DataComponentTypes.MAP_ID))) {
            return;
        }

        stack.push();
        float reflect = side == Arm.LEFT ? -1 : 1;

        stack.translate(reflect * -0.3F, -0.54F, 0);

        model = lookupModel(state).body();

        ModelPart arm = side == Arm.LEFT ? model.leftArm : model.rightArm;
        arm.resetTransform();
        arm.visible = true;
        model.leftSleeve.visible = sleeveVisible;
        model.rightSleeve.visible = sleeveVisible;
        arm.roll = reflect * 0.1F;
        // seapony has different angles, so make sure they're correct
        arm.pitch = 0;
        arm.yaw = 0;

        if (model instanceof PonyModel ponyModel) {
            ponyModel.transform(state, BodyPart.LEGS, arm);
        }

        queue.submitModelPart(arm, stack, RenderLayer.getEntityTranslucent(skinTexture), light, OverlayTexture.DEFAULT_UV, null);
        stack.pop();
    }

    @Override
    public EquineRenderManager<Player, PlayerPonyRenderState, ClientPonyModel<PlayerPonyRenderState>> getEquineManager() {
        return manager;
    }

    @Override
    public Pony getEntityPony(Player entity) {
        return Pony.getManager().getPony(entity);
    }

    @Override
    public final Identifier getTexture(PlayerEntityRenderState state) {
        return ((PlayerPonyRenderState)state).pony.texture();
    }

    @Override
    public Identifier getDefaultTexture(PlayerPonyRenderState state, Wearable wearable) {
        if (state.wearabledTextures.containsKey(wearable)) {
            return state.wearabledTextures.get(wearable);
        }

        if (wearable.isSaddlebags() && state.race.supportsLegacySaddlebags() && state.attributes.isEmbedded(wearable)) {
            return getTexture(state);
        }

        return wearable.getDefaultTexture();
    }
}
