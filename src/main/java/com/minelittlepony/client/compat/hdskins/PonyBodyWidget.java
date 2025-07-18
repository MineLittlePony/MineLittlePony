package com.minelittlepony.client.compat.hdskins;

import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.EntityPose;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.*;

import com.minelittlepony.api.events.PonyModelPrepareCallback;
import com.minelittlepony.api.model.*;
import com.minelittlepony.api.pony.*;
import com.minelittlepony.api.pony.meta.Wearable;
import com.minelittlepony.client.render.entity.state.PlayerPonyRenderState;
import com.minelittlepony.hdskins.client.VanillaModels;
import com.minelittlepony.hdskins.client.gui.PlayerBodyWidget;
import com.minelittlepony.hdskins.client.gui.player.skins.PlayerSkins;
import com.minelittlepony.hdskins.client.gui.player.skins.PlayerSkins.PlayerSkin;
import com.minelittlepony.hdskins.profile.SkinType;

import java.util.*;

public class PonyBodyWidget extends PlayerBodyWidget<PonyBodyWidget.State> {
    private final Map<EquipmentSlot, ItemStack> equipment = new HashMap<>();
    private final Map<Arm, ItemStack> handStacks = new HashMap<>();

    public PonyBodyWidget(PlayerSkins<?> skins) {
        super(skins, new State());
    }

    @Override
    public void setHandStack(Hand hand, ItemStack stack) {
        super.setHandStack(hand, stack);
        handStacks.put(hand == Hand.MAIN_HAND ? playerState.mainArm : playerState.mainArm.getOpposite(), stack);
    }

    @Override
    public void updateState(float xPosition, float yPosition, float mouseX, float mouseY, float tickDelta) {
        super.updateState(xPosition, yPosition, mouseX, mouseY, tickDelta);

        boolean sneaking = playerState.isInSneakingPose;

        playerState.updateState(MinecraftClient.getInstance().getItemModelManager(),
                equipment, handStacks,
                Pony.getManager().getPony(skins.get(SkinType.SKIN).getId()), ModelAttributes.Mode.OTHER
        );
        playerState.smallArms = VanillaModels.isSlim(skins.getSkinVariant());
        playerState.form = getForm();

        playerState.attributes.isSitting = playerState.hasVehicle;
        playerState.attributes.isCrouching = playerState.isInSneakingPose = sneaking;
        playerState.attributes.isSleeping = playerState.sleepingInBed;

        playerState.wearabledTextures.clear();
        for (Wearable wearable : Wearable.REGISTRY.values()) {
            if (playerState.isWearing(wearable)) {
                getSkin(SkinType.REGISTRY.get(wearable.getId())).ifPresent(skin -> {
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
        public void completeStateUpdate(PonyModel<?> model) {
            if (stateIncomplete) {
                stateIncomplete = false;
                PonyModelPrepareCallback.EVENT.invoker().onPonyModelPrepared(attributes, model, ModelAttributes.Mode.OTHER);
            }
        }
    }
}
