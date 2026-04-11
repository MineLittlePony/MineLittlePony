package com.minelittlepony.client.compat.hdskins;

import net.minecraft.client.Minecraft;
import net.minecraft.core.component.DataComponentMap;
import net.minecraft.core.component.DataComponents;
import net.minecraft.resources.Identifier;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.*;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemStackTemplate;

import org.jetbrains.annotations.Nullable;

import com.minelittlepony.api.events.PonyRenderStatePrepareCallback;
import com.minelittlepony.api.model.*;
import com.minelittlepony.api.pony.*;
import com.minelittlepony.api.pony.meta.Wearable;
import com.minelittlepony.client.render.entity.state.PlayerPonyRenderState;
import com.minelittlepony.common.util.registry.ForwardingHolder;
import com.minelittlepony.hdskins.client.VanillaModels;
import com.minelittlepony.hdskins.client.gui.PlayerBodyWidget;
import com.minelittlepony.hdskins.client.gui.player.skins.PlayerSkins;
import com.minelittlepony.hdskins.client.gui.player.skins.PlayerSkins.PlayerSkin;
import com.minelittlepony.hdskins.profile.SkinType;

import java.util.*;

public class PonyBodyWidget extends PlayerBodyWidget<PonyBodyWidget.State> {
    private final Map<EquipmentSlot, ItemStack> equipment = new HashMap<>();
    private final Map<HumanoidArm, ItemStack> handStacks = new HashMap<>();

    public PonyBodyWidget(PlayerSkins<?> skins) {
        super(skins, new State());
    }

    @Override
    public void setHandStack(InteractionHand hand, Optional<ItemStackTemplate> stack) {
        super.setHandStack(hand, stack);
        handStacks.compute(hand == InteractionHand.MAIN_HAND ? playerState.mainArm : playerState.mainArm.getOpposite(), (_, _) -> createStack(stack));
    }

    public void setEquippedStack(EquipmentSlot slot, Optional<ItemStackTemplate> stack) {
        super.setEquippedStack(slot, stack);
        equipment.compute(slot, (_, _) -> createStack(stack));
    }

    @Nullable
    private ItemStack createStack(Optional<ItemStackTemplate> template) {
        return template.map(t -> t.typeHolder().areComponentsBound() ? t.create() : new ItemStack(ForwardingHolder.withComponents(t.typeHolder(), DataComponentMap.builder()
                    .addAll(DataComponents.COMMON_ITEM_COMPONENTS)
                    .set(DataComponents.ITEM_MODEL, t.item().unwrapKey().orElseThrow().identifier())
                .build()), 1, t.components())).orElse(null);
    }

    @Override
    public void updateState(float xPosition, float yPosition, float mouseX, float mouseY, float tickDelta) {
        super.updateState(xPosition, yPosition, mouseX, mouseY, tickDelta);

        boolean sneaking = playerState.isCrouching;
        Pony pony = Pony.getManager().getPony(skins.get(SkinType.SKIN).getId());

        playerState.attributes.updateLivingState(null, pony, ModelAttributes.Mode.OTHER);
        playerState.attributes.isSitting = playerState.isPassenger;
        playerState.attributes.isCrouching = playerState.isCrouching = sneaking;
        playerState.sleepingInBed = playerState.hasPose(Pose.SLEEPING);
        playerState.attributes.isSleeping = playerState.sleepingInBed;
        playerState.attributes.isLyingDown = playerState.sleepingInBed;
        playerState.attributes.isRiptide = playerState.isAutoSpinAttack;
        playerState.attributes.isSwimming = playerState.isVisuallySwimming || playerState.attributes.isRiptide;
        playerState.attributes.checkRainboom(null, null, playerState.ageInTicks);
        playerState.attributes.motionLerp = 1;
        playerState.updateState(Minecraft.getInstance().getItemModelResolver(),
                equipment, handStacks,
                pony, ModelAttributes.Mode.OTHER
        );
        playerState.smallArms = VanillaModels.isSlim(skins.getSkinVariant());
        playerState.form = getForm();

        playerState.wearabledTextures.clear();
        for (Wearable wearable : Wearable.REGISTRY.values()) {
            if (playerState.isWearing(wearable)) {
                getSkin(SkinType.REGISTRY.getValue(wearable.getId())).ifPresent(skin -> {
                    playerState.wearabledTextures.put(wearable, skin);
                });
            }
        }

        playerState.isPreviewModel = true;
        playerState.stateIncomplete = true;
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

    static class State extends PlayerPonyRenderState implements PreviewRenderState {
        public boolean stateIncomplete;

        @Override
        public void completeStateUpdate(Models<?> models) {
            if (stateIncomplete) {
                stateIncomplete = false;
                PonyRenderStatePrepareCallback.EVENT.invoker().onPonyRenderStatePrepared(this, models.body(), ModelAttributes.Mode.OTHER);
            }
        }
    }
}
