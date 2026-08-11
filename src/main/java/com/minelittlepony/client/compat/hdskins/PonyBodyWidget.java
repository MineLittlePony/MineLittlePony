package com.minelittlepony.client.compat.hdskins;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.item.ItemModelResolver;
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
import com.minelittlepony.api.pony.meta.Race;
import com.minelittlepony.api.pony.meta.Wearable;
import com.minelittlepony.api.state.PreviewRenderState;
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
        super(skins, new State(skins));
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
    public void tick() {
        super.tick();
        if (skins.getPosture().getActiveSkinType() == MineLPHDSkins.seaponySkinType || skins.getPosture().getActiveSkinType() == MineLPHDSkins.nirikSkinType) {
            playerState.skin = new net.minecraft.world.entity.player.PlayerSkin(
                    skins.get(skins.getPosture().getActiveSkinType()).getAsset(),
                    playerState.skin.cape(),
                    playerState.skin.elytra(),
                    playerState.skin.model(), playerState.skin.secure()
            );
        }
    }

    @Override
    public void updateState(float xPosition, float yPosition, float mouseX, float mouseY, float tickDelta) {
        super.updateState(xPosition, yPosition, mouseX, mouseY, tickDelta);

        SkinType bodySkinType = SkinType.SKIN;
        if ((skins.getPosture().getActiveSkinType() == MineLPHDSkins.seaponySkinType && skins.get(MineLPHDSkins.seaponySkinType).isReady())
            || (skins.getPosture().getActiveSkinType() == MineLPHDSkins.nirikSkinType && skins.get(MineLPHDSkins.nirikSkinType).isReady())) {
            bodySkinType = skins.getPosture().getActiveSkinType();
        }

        playerState.updateState(Minecraft.getInstance().getItemModelResolver(),
                equipment, handStacks,
                Pony.getManager().getPony(skins.get(bodySkinType).getId()), ModelAttributes.Mode.OTHER
        );
    }

    static class State extends PlayerPonyRenderState implements PreviewRenderState {
        private final PlayerSkins<?> skins;
        public boolean stateIncomplete;

        public State(PlayerSkins<?> skins) {
            this.skins = skins;
        }

        public void updateState(ItemModelResolver resolver,
                Map<EquipmentSlot, ItemStack> equipment,
                Map<HumanoidArm, ItemStack> armStacks,
                Pony pony, ModelAttributes.Mode mode) {
            boolean sneaking = isCrouching;
            attributes.updateLivingState(null, pony, ModelAttributes.Mode.OTHER);
            attributes.isSitting = isPassenger;
            attributes.isCrouching = isCrouching = sneaking;
            sleepingInBed = hasPose(Pose.SLEEPING);
            attributes.isSleeping = sleepingInBed;
            attributes.isLyingDown = sleepingInBed;
            attributes.isRiptide = isAutoSpinAttack;
            attributes.isSwimming = isVisuallySwimming || attributes.isRiptide;
            attributes.checkRainboom(null, null, ageInTicks);
            attributes.motionLerp = 1;
            super.updateState(resolver, equipment, armStacks, pony, mode);
            smallArms = VanillaModels.isSlim(skins.getSkinVariant());
            form = getForm();
            if (form == PonyForm.SEAPONY) {
                race = Race.SEAPONY;
                pose = Pose.STANDING;
                isCrouching = false;
                attributes.isCrouching = false;
            }
            wearabledTextures.clear();
            for (Wearable wearable : Wearable.REGISTRY.values()) {
                if (wearable != Wearable.NONE) {
                    getGearSkin(wearable, pony).ifPresent(skin -> {
                        wearabledTextures.put(wearable, skin);
                    });
                }
            }
            isPreviewModel = true;
            stateIncomplete = true;
        }

        @Override
        public void completeStateUpdate(Models<?> models) {
            if (stateIncomplete) {
                stateIncomplete = false;
                PonyRenderStatePrepareCallback.EVENT.invoker().onPonyRenderStatePrepared(this, models.body(), ModelAttributes.Mode.OTHER);
            }
        }

        @Override
        public boolean isWearing(Wearable wearable) {
            return wearabledTextures.containsKey(wearable);
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

        private Optional<Identifier> getGearSkin(Wearable wearable, Pony pony) {
            SkinType type = SkinType.REGISTRY.getValue(wearable.getId());
            PlayerSkin skin = skins.get(type);

            if (skin.isReady() || skins.getProvidedSkinTypes().contains(type.getId())) {
                return Optional.of(skin.getId());
            }

            if (pony.metadata().gear().matches(wearable)) {
                if (wearable.isSaddlebags() && pony.metadata().race().supportsLegacySaddlebags()) {
                    return Optional.of(pony.texture());
                }

                return Optional.of(wearable.getDefaultTexture());
            }

            return Optional.empty();
        }

    }
}
