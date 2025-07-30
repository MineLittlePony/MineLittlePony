package com.minelittlepony.client.mixin;

import net.minecraft.block.SkullBlock;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.entity.SkullBlockEntityModel;
import net.minecraft.client.render.item.model.special.*;
import net.minecraft.client.texture.PlayerSkinProvider;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.item.ItemDisplayContext;
import net.minecraft.item.ItemStack;

import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.minelittlepony.client.render.blockentity.skull.PonySkullRenderer;

import java.util.*;

@Mixin(value = PlayerHeadModelRenderer.class, priority = 3000)
abstract class MixinPlayerHeadModelRenderer {
    @Shadow
    private @Final PlayerHeadModelRenderer.Data data;

    private final Map<PlayerHeadModelRenderer.Data, PonySkullRenderer.Data> ponyData = new HashMap<>();

    @Inject(method = "<init>", at = @At("TAIL"))
    private void onInit(PlayerSkinProvider playerSkinProvider, SkullBlockEntityModel model, PlayerHeadModelRenderer.Data data, CallbackInfo info) {
        ponyData.put(data, PonySkullRenderer.INSTANCE.getSkullState(SkullBlock.Type.PLAYER, null));
    }

    @Inject(method = "render", at = @At("HEAD"), cancellable = true)
    private void onRender(@Nullable PlayerHeadModelRenderer.Data data, ItemDisplayContext context, MatrixStack matrices, VertexConsumerProvider vertices, int light, int overlay, boolean glint, CallbackInfo info) {
        var state = ponyData.get(Objects.requireNonNullElse(data, this.data));
        if (state != null && state.render(null, 180, 0, matrices, vertices, light)) {
            info.cancel();
        }
    }

    @Inject(method = "getData(Lnet/minecraft/item/ItemStack;)Lnet/minecraft/client/render/item/model/special/PlayerHeadModelRenderer$Data;", at = @At("RETURN"), cancellable = true)
    private void onGetData(ItemStack stack, CallbackInfoReturnable<PlayerHeadModelRenderer.Data> info) {
        var data = info.getReturnValue();
        if (data != null) {
            ponyData.put(data, PonySkullRenderer.INSTANCE.getSkullState(SkullBlock.Type.PLAYER, stack.get(DataComponentTypes.PROFILE)));
        }
    }
}
