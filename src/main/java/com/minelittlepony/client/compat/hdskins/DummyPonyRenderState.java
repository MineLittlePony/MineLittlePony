package com.minelittlepony.client.compat.hdskins;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.item.ItemRenderState;
import net.minecraft.entity.EntityPose;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.item.ItemDisplayContext;
import net.minecraft.item.ItemStack;
import net.minecraft.util.*;

import com.minelittlepony.api.config.PonyConfig;
import com.minelittlepony.api.events.PonyModelPrepareCallback;
import com.minelittlepony.api.model.*;
import com.minelittlepony.api.pony.*;
import com.minelittlepony.api.pony.meta.Wearable;
import com.minelittlepony.client.render.EquineRenderManager;
import com.minelittlepony.client.render.entity.state.PlayerPonyRenderState;
import com.minelittlepony.hdskins.client.VanillaModels;
import com.minelittlepony.hdskins.client.gui.DummyPlayerRenderState;
import com.minelittlepony.hdskins.client.gui.player.skins.PlayerSkins;
import com.minelittlepony.hdskins.client.gui.player.skins.PlayerSkins.PlayerSkin;
import com.minelittlepony.hdskins.profile.SkinType;

import java.util.*;

public class DummyPonyRenderState extends DummyPlayerRenderState implements PreviewRenderState {

    private final PlayerPonyRenderState ponyState = new PlayerPonyRenderState();

    private final Map<EquipmentSlot, ItemStack> equipment = new HashMap<>();
    private final Map<Arm, ItemStack> handStacks = new HashMap<>();

    private final Map<Arm, ItemRenderState> handItemStates = Map.of(
            Arm.LEFT, ponyState.leftHandItemState,
            Arm.RIGHT, ponyState.rightHandItemState
    );
    private final Map<Arm, ItemRenderState> magicHandItemStates = Map.of(
            Arm.LEFT, ponyState.glintlessLeftHandItemState,
            Arm.RIGHT, ponyState.glintlessRightHandItemState
    );

    private boolean stateIncomplete;

    public DummyPonyRenderState(PlayerSkins<?> skins) {
        super(skins);
    }

    public void setPose(EntityPose pose) {
        this.pose = pose == EntityPose.STANDING && isInSneakingPose ? EntityPose.CROUCHING : pose;
    }

    public void setHandStack(Hand hand, ItemStack stack) {
        super.setHandStack(hand, stack);
        Arm arm = hand == Hand.MAIN_HAND ? mainArm : mainArm.getOpposite();
        handStacks.put(arm, stack);
        var resolver = MinecraftClient.getInstance().getItemModelManager();
        var context = arm == Arm.LEFT ? ItemDisplayContext.THIRD_PERSON_LEFT_HAND : ItemDisplayContext.THIRD_PERSON_RIGHT_HAND;
        resolver.clearAndUpdate(handItemStates.get(arm), stack, context, null, null, 0);
        if (PonyConfig.getInstance().tpsmagic.get() && ponyState.hasMagicGlow()) {
            resolver.clearAndUpdate(magicHandItemStates.get(arm), EquineRenderManager.getWithoutGlint(stack), context, null, null, 0);
        } else {
            magicHandItemStates.get(arm).clear();
        }
    }

    public void tickAnimations() {
        super.tickAnimations();
        ponyState.equippedChestStack = equippedChestStack;
        ponyState.pose = pose;
        ponyState.hasVehicle = hasVehicle;
        ponyState.y = y;
        ponyState.isSwimming = isSwimming;
        ponyState.usingRiptide = usingRiptide;
        ponyState.positionOffset = positionOffset;
    }

    public void updateState(float xPosition, float yPosition, float mouseX, float mouseY, float tickDelta) {
        super.updateState(xPosition, yPosition, mouseX, mouseY, tickDelta);
        ponyState.bodyYaw = bodyYaw;
        ponyState.relativeHeadYaw = relativeHeadYaw;
        ponyState.pitch = pitch;
        ponyState.leftWingPitch = leftWingPitch;
        ponyState.leftWingYaw = leftWingYaw;
        ponyState.leftWingRoll = leftWingRoll;
        ponyState.handSwingProgress = handSwingProgress;
        ponyState.limbAmplitudeInverse = limbAmplitudeInverse;
        ponyState.limbSwingAnimationProgress = limbSwingAnimationProgress;
        ponyState.limbSwingAmplitude = limbSwingAmplitude;
        ponyState.handSwinging = handSwinging;
        ponyState.activeHand = activeHand;
        ponyState.preferredArm = preferredArm;

        ponyState.isPreviewModel = true;

        ponyState.updateState(MinecraftClient.getInstance().getItemModelManager(),
                equipment, handStacks,
                Pony.getManager().getPony(skins.get(SkinType.SKIN).getId()), ModelAttributes.Mode.OTHER
        );
        ponyState.smallArms = VanillaModels.isSlim(skins.getSkinVariant());
        ponyState.form = getForm();

        ponyState.wearabledTextures.clear();
        for (Wearable wearable : Wearable.REGISTRY.values()) {
            if (ponyState.isWearing(wearable)) {
                getSkin(SkinType.REGISTRY.get(wearable.getId())).ifPresent(skin -> {
                    ponyState.wearabledTextures.put(wearable, skin);
                });
            }
        }

        stateIncomplete = true;
    }

    @Override
    public void completeStateUpdate(PonyModel<?> model) {
        if (stateIncomplete) {
            stateIncomplete = false;
            PonyModelPrepareCallback.EVENT.invoker().onPonyModelPrepared(ponyState.attributes, model, ModelAttributes.Mode.OTHER);
        }
    }

    private Optional<Identifier> getSkin(SkinType type) {
        PlayerSkin skin = skins.get(type);

        if (skin.isReady() || skins.getProvidedSkinTypes().contains(type.getId())) {
            return Optional.of(skin.getId());
        }

        PlayerSkin main = skins.get(SkinType.SKIN);
        Wearable wearable = Wearable.REGISTRY.getOrDefault(type.getId(), Wearable.NONE);
        PonyData metadata = Pony.getManager().getPony(main.getId()).metadata();
        if (wearable != Wearable.NONE && metadata.gear().matches(wearable)) {

            if (wearable.isSaddlebags() && metadata.race().supportsLegacySaddlebags()) {
                return Optional.of(main.getId());
            }

            return Optional.of(wearable.getDefaultTexture());
        }

        return Optional.empty();
    }

    private Identifier getForm() {
        if (skins.getPosture().getActiveSkinType() == MineLPHDSkins.seaponySkinType) {
            return PonyForm.SEAPONY;
        }
        if (skins.getPosture().getActiveSkinType() == MineLPHDSkins.nirikSkinType) {
            return PonyForm.NIRIK;
        }
        return PonyForm.DEFAULT;
    }

    @Override
    public PlayerPonyRenderState getRenderState() {
        return ponyState;
    }
}
