package com.minelittlepony.client.render.entity;

import com.minelittlepony.api.model.*;
import com.minelittlepony.api.model.ModelAttributes.Mode;
import com.minelittlepony.api.pony.Pony;
import com.minelittlepony.api.pony.meta.Wearable;
import com.minelittlepony.client.MineLittlePony;
import com.minelittlepony.client.model.*;
import com.minelittlepony.client.render.PonyRenderContext;
import com.minelittlepony.client.render.entity.feature.*;
import com.minelittlepony.client.render.entity.state.PlayerPonyRenderState;
import com.minelittlepony.client.render.entity.state.PonyRenderState;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;

import java.util.*;

import com.minelittlepony.client.render.EquineRenderManager;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.ClientAvatarEntity;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.layers.*;
import net.minecraft.client.renderer.entity.player.AvatarRenderer;
import net.minecraft.client.renderer.entity.state.AvatarRenderState;
import net.minecraft.client.renderer.item.ItemModelResolver;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.Direction;
import net.minecraft.core.component.DataComponents;
import net.minecraft.data.AtlasIds;
import net.minecraft.resources.Identifier;
import net.minecraft.util.*;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.*;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

import org.jetbrains.annotations.Nullable;
import org.joml.Quaternionf;
import org.joml.Vector3f;

public class PlayerPonyRenderer<Player extends Avatar & ClientAvatarEntity>
        extends AvatarRenderer<Player>
        implements PonyRenderContext<
            Player,
            PlayerPonyRenderState,
            ClientPonyModel<PlayerPonyRenderState>
        > {
    protected final EquineRenderManager<Player, PlayerPonyRenderState, ClientPonyModel<PlayerPonyRenderState>> manager;

    private ModelAttributes.Mode mode = ModelAttributes.Mode.THIRD_PERSON;
    protected final ItemModelResolver itemModelManager;

    @SuppressWarnings({"unchecked", "rawtypes"})
    public PlayerPonyRenderer(EntityRendererProvider.Context context, boolean slim) {
        super(context, slim);
        itemModelManager = context.getItemModelResolver();
        manager = new EquineRenderManager<>(this, super::setupRotations, race -> ModelType.getPlayerModel(race).create(slim));

        // remove vanilla features (keep modded ones)
        layers.removeIf(feature -> {
            return feature instanceof HumanoidArmorLayer
                    || feature instanceof PlayerItemInHandLayer
                    || feature instanceof Deadmau5EarsLayer
                    || feature instanceof CapeLayer
                    || feature instanceof CustomHeadLayer
                    || feature instanceof WingsLayer
                    || feature instanceof ParrotOnShoulderLayer;
        });
        addPonyFeature(new ArmourFeature<>(this, context.getEquipmentAssets(), context.getAtlas(AtlasIds.ARMOR_TRIMS)));
        addPonyFeature(new HeldItemFeature<>(this));
        addPonyFeature(new DJPon3Feature<>(this));
        addPonyFeature(new CapeFeature(this, context.getModelSet(), context.getEquipmentAssets()));
        addPonyFeature(new SkullFeature<>(this, context.getPlayerSkinRenderCache(), context.getModelSet(), CustomHeadLayer.Transforms.DEFAULT, true));
        addPonyFeature(new ElytraFeature(this, context.getEquipmentRenderer()));
        addPonyFeature(new PassengerFeature<>(this, context));
        addPonyFeature(new GearFeature<>(this));

        addPonyFeature(new PonyBodyPartFeature<>(this, m -> ((ModelWithHorn)m).getHorn(), m -> m instanceof ModelWithHorn));
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    protected final boolean addPonyFeature(RenderLayer<
            ? extends AvatarRenderState,
            ? extends ClientPonyModel<? extends PonyRenderState>
            > feature) {
        return ((List)layers).add(feature);
    }

    @Override
    public Vec3 getRenderOffset(AvatarRenderState state) {
        Vec3 offset = super.getRenderOffset(state);
        return offset
                .scale(((PlayerPonyRenderState)state).attributes.size.scaleFactor())
                .add(0, state.scale * ((PlayerPonyRenderState)state).yOffset, 0);
    }

    @Override
    public AvatarRenderState createRenderState() {
        return new PlayerPonyRenderState();
    }

    @Override
    public void extractRenderState(Player entity, AvatarRenderState state, float tickDelta) {
        super.extractRenderState(entity, state, tickDelta);
        manager.updateState(entity, (PlayerPonyRenderState)state, mode, itemModelManager);
    }

    public final PlayerPonyRenderState createRenderState(Player entity, float tickDelta, ModelAttributes.Mode mode) {
        try {
            this.mode = mode;
            return (PlayerPonyRenderState)createRenderState(entity, tickDelta);
        } finally {
            this.mode = ModelAttributes.Mode.THIRD_PERSON;
        }
    }

    @Override
    protected void setupRotations(AvatarRenderState state, PoseStack matrices, float bodyRot, float entityScale) {
        manager.completeStateUpdate(state);
        model = lookupModel(state).body();
        shadowRadius = ((PlayerPonyRenderState)state).attributes.size.shadowSize();
        manager.setupTransforms((PlayerPonyRenderState)state, matrices, bodyRot, entityScale);
    }

    @Override
    protected AABB getBoundingBoxForCulling(Player entity) {
        return manager.getBoundingBox(entity, entity.getBoundingBox());
    }

    @Override
    protected void submitNameDisplay(AvatarRenderState state, PoseStack matrices, SubmitNodeCollector queue, CameraRenderState camera) {
        matrices.pushPose();
        matrices.translate(0, ((PlayerPonyRenderState)state).nameplateYOffset, 0);
        super.submitNameDisplay(state, matrices, queue, camera);
        matrices.popPose();
    }

    @Override
    public final void renderRightHand(PoseStack matrices, SubmitNodeCollector queue, int light, Identifier skinTexture, boolean sleeveVisible) {
        renderArm(matrices, queue, light, skinTexture, sleeveVisible, HumanoidArm.RIGHT);
    }

    @Override
    public final void renderLeftHand(PoseStack matrices, SubmitNodeCollector queue, int light, Identifier skinTexture, boolean sleeveVisible) {
        renderArm(matrices, queue, light, skinTexture, sleeveVisible, HumanoidArm.LEFT);
    }

    @Nullable
    @SuppressWarnings("unchecked")
    public Vec3 getHandPos(Player player, HumanoidArm arm, float swingProgress, float tickDelta) {
        if (entityRenderDispatcher.options.getCameraType().isFirstPerson() && player == Minecraft.getInstance().player) {
            return null;
        }

        var state = createRenderState(player, tickDelta, Mode.THIRD_PERSON);
        PoseStack matrices = new PoseStack();
        if (state.hasPose(Pose.SLEEPING)) {
            Direction direction = state.bedOrientation;
            if (direction != null) {
                float bodyLength = state.eyeHeight - 0.1F;
                matrices.translate(-direction.getStepX() * bodyLength, 0, -direction.getStepZ() * bodyLength);
            }
        }
        float scale = state.scale;
        matrices.scale(scale, scale, scale);
        setupRotations(state, matrices, state.bodyRot, 0);
        matrices.scale(-1, -1, 1);
        scale(state, matrices);
        matrices.translate(0, -1.501F, 0);
        model.setupAnim(state);
        ((ClientPonyModel<PlayerPonyRenderState>)model).transformHeldItem(state, arm, matrices);
        model.translateToHand(state, arm, matrices);
        ModelPart a = arm == HumanoidArm.LEFT ? model.leftArm : model.rightArm;
        Quaternionf rotation = new Quaternionf().rotationZYX(a.zRot, a.yRot, a.xRot);
        matrices.mulPose(rotation);
        matrices.translate(0, -0.2F, -0.7F);
        matrices.mulPose(rotation.conjugate());
        model.body.translateAndRotate(matrices);

        matrices.mulPose(Axis.XP.rotationDegrees(-90));
        matrices.mulPose(Axis.YP.rotationDegrees(180));
        boolean left = arm == HumanoidArm.LEFT;
        matrices.translate((left ? -1 : 1) / 16F, 0.125F, -0.625F);
        var vec = matrices.last().pose().transformPosition(new Vector3f());
        var pos = new Vec3(
                Mth.lerp(tickDelta, player.xOld, player.getX()),
                Mth.lerp(tickDelta, player.yOld, player.getY()),
                Mth.lerp(tickDelta, player.zOld, player.getZ())
        );

        return pos.add(vec.x, vec.y, vec.z);
    }

    @SuppressWarnings("unchecked")
    protected void renderArm(PoseStack stack, SubmitNodeCollector queue, int light, Identifier skinTexture, boolean sleeveVisible, HumanoidArm side) {

        LocalPlayer player = Minecraft.getInstance().player;

        var renderer = MineLittlePony.getInstance().getRenderDispatcher().getPonyRenderer(player);
        if (((Object)renderer) != this) {
            return;
        }
        PonyRenderState state = renderer.createRenderState(player, Minecraft.getInstance().getDeltaTracker().getGameTimeDeltaPartialTick(false));

        if (state.hasMagicGlow() && (
                player.getItemInHand(InteractionHand.MAIN_HAND).has(DataComponents.MAP_ID)
             || player.getItemInHand(InteractionHand.OFF_HAND).has(DataComponents.MAP_ID)
        )) {
            return;
        }

        stack.pushPose();
        float reflect = side == HumanoidArm.LEFT ? -1 : 1;

        stack.translate(reflect * -0.3F, -0.54F, 0);

        model = lookupModel(state).body();

        ModelPart arm = side == HumanoidArm.LEFT ? model.leftArm : model.rightArm;
        arm.resetPose();
        arm.visible = true;
        model.leftSleeve.visible = sleeveVisible;
        model.rightSleeve.visible = sleeveVisible;
        arm.zRot = reflect * 0.1F;
        // seapony has different angles, so make sure they're correct
        arm.xRot = 0;
        arm.yRot = 0;

        if (model instanceof PonyModel ponyModel) {
            ponyModel.transform(state, BodyPart.LEGS, arm);
        }

        queue.submitModelPart(arm, stack, RenderTypes.entityTranslucent(skinTexture), light, OverlayTexture.NO_OVERLAY, null);
        stack.popPose();
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
    public final Identifier getTextureLocation(AvatarRenderState state) {
        return ((PlayerPonyRenderState)state).pony.texture();
    }

    @Override
    public Identifier getDefaultTexture(PlayerPonyRenderState state, Wearable wearable) {
        if (state.wearabledTextures.containsKey(wearable)) {
            return state.wearabledTextures.get(wearable);
        }

        if (wearable.isSaddlebags() && state.race.supportsLegacySaddlebags() && state.attributes.isEmbedded(wearable)) {
            return getTextureLocation(state);
        }

        return wearable.getDefaultTexture();
    }
}
