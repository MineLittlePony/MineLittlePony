package com.minelittlepony.client.mixin;

import net.minecraft.block.SkullBlock;
import net.minecraft.client.render.command.OrderedRenderCommandQueue;
import net.minecraft.client.render.item.model.special.*;
import net.minecraft.client.texture.PlayerSkinCache;
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
    private final Map<PlayerSkinCache.Entry, PonySkullRenderer.Data> ponyData = new HashMap<>();

    @Inject(method = "render", at = @At("HEAD"), cancellable = true)
    private void onRender(@Nullable PlayerSkinCache.Entry data, ItemDisplayContext context, MatrixStack matrices, OrderedRenderCommandQueue queue, int light, int overlay, boolean glint, int k, CallbackInfo info) {
        var state = data == null ? PonySkullRenderer.INSTANCE.getSkullState(SkullBlock.Type.PLAYER, null) : ponyData.get(data);
        if (state != null && state.render(null, 180, 0, matrices, queue, light, 0, null)) {
            info.cancel();
        }
    }

    @Inject(method = "getData(Lnet/minecraft/item/ItemStack;)Lnet/minecraft/client/texture/PlayerSkinCache$Entry;", at = @At("RETURN"), cancellable = true)
    private void onGetData(ItemStack stack, CallbackInfoReturnable<PlayerSkinCache.Entry> info) {
        var data = info.getReturnValue();
        if (data != null) {
            ponyData.put(data, PonySkullRenderer.INSTANCE.getSkullState(SkullBlock.Type.PLAYER, stack.get(DataComponentTypes.PROFILE)));
        }
    }
}
