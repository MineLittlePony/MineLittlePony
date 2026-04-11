package com.minelittlepony.client.render.entity;

import net.minecraft.client.renderer.Sheets;
import net.minecraft.client.renderer.entity.*;
import net.minecraft.client.renderer.entity.layers.SimpleEquipmentLayer;
import net.minecraft.client.renderer.entity.state.HumanoidRenderState;
import net.minecraft.client.resources.model.EquipmentClientInfo;
import net.minecraft.resources.Identifier;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.monster.Strider;
import net.minecraft.world.item.ItemStack;

import com.minelittlepony.api.pony.DefaultPonySkinHelper;
import com.minelittlepony.client.MineLittlePony;
import com.minelittlepony.client.model.ModelType;
import com.minelittlepony.client.model.armour.PonifiedEquipmentRenderer;
import com.minelittlepony.client.model.entity.StriderDragonModel;
import com.minelittlepony.client.render.entity.state.PonifiedRenderState;
import com.mojang.blaze3d.vertex.PoseStack;

import java.util.UUID;

public class StriderRenderer extends MobRenderer<Strider, StriderRenderer.State, StriderDragonModel> {
    public static final Identifier DRAGON_PONIES = MineLittlePony.id("textures/entity/strider/pony");
    public static final Identifier COLD_DRAGON_PONIES = MineLittlePony.id("textures/entity/strider/cold_pony");

    public StriderRenderer(EntityRendererProvider.Context context) {
        super(context, ModelType.STRIDER.createModel(), 0.5F);
        addLayer(new SimpleEquipmentLayer<>(this, new PonifiedEquipmentRenderer(context.getEquipmentAssets(), context.getAtlas(Sheets.ARMOR_TRIMS_SHEET)),
                EquipmentClientInfo.LayerType.STRIDER_SADDLE,
                state -> state.saddleStack,
                ModelType.STRIDER_SADDLE.createModel(),
                ModelType.STRIDER_SADDLE.createModel(),
                1
        ));
    }

    @Override
    public State createRenderState() {
        return new State();
    }

    @Override
    public void extractRenderState(Strider entity, State state, float tickDelta) {
        super.extractRenderState(entity, state, tickDelta);
        state.uuid = entity.getUUID();
        state.cold = entity.isFreezing();
        state.saddleStack = entity.getItemBySlot(EquipmentSlot.SADDLE);
        state.flailAmount = 1 + (float)Mth.clamp(entity.getDeltaMovement().y * 10, 0, 7);
        state.walkAnimationSpeed *= 2;
        state.walkAnimationPos *= 1.5F;

    }

    @Override
    public Identifier getTextureLocation(State state) {
        return MineLittlePony.getInstance().getVariatedTextures().get(state.cold ? COLD_DRAGON_PONIES : DRAGON_PONIES, state.uuid).orElse(DefaultPonySkinHelper.STEVE);
    }

    @Override
    protected void scale(State state, PoseStack stack) {
        float scale = 0.9375F;
        if (state.isBaby) {
            scale *= 0.5F;
            shadowRadius = 0.25F;
        } else {
            shadowRadius = 0.5F;
        }

        stack.scale(scale, scale, scale);
    }

    @Override
    protected boolean isShaking(State state) {
        return state.cold;
    }

    public static class State extends HumanoidRenderState implements PonifiedRenderState {
        public UUID uuid;
        public boolean cold;
        public ItemStack saddleStack = ItemStack.EMPTY;
        public float flailAmount;
    }
}
