package com.minelittlepony.client.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.minelittlepony.client.render.LevitatingItemRenderer;
import com.minelittlepony.client.render.tileentities.skull.PonySkullRenderer;

import net.minecraft.client.renderer.GlStateManager;

@Mixin(GlStateManager.class)
public abstract class MixinGlStateManager {

    @Inject(method = "enableBlendProfile(Lnet/minecraft/client/renderer/GlStateManager$Profile;)V", at = @At("HEAD"), cancellable = true)
    private static void enableBlendProfile(GlStateManager.Profile profile, CallbackInfo info) {
        if (profile == GlStateManager.Profile.PLAYER_SKIN && PonySkullRenderer.ponyInstance.usesTransparency()) {
            LevitatingItemRenderer.enableItemGlowRenderProfile();
            info.cancel();
        }
    }
}
