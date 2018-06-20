package com.minelittlepony.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.minelittlepony.render.PonySkullRenderer;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.GlStateManager.DestFactor;
import net.minecraft.client.renderer.GlStateManager.SourceFactor;

@Mixin(GlStateManager.class)
public abstract class MixinGlStateManager {

    @Inject(method = "enableBlendProfile(Lnet/minecraft/client/renderer/GlStateManager$Profile;)V", at = @At("HEAD"), cancellable = true)
    private static void enableBlendProfile(GlStateManager.Profile profile, CallbackInfo info) {
        if (profile == GlStateManager.Profile.PLAYER_SKIN && PonySkullRenderer.ponyInstance.usesTransparency()) {
            GlStateManager.enableBlend();
            GlStateManager.tryBlendFuncSeparate(SourceFactor.CONSTANT_COLOR, DestFactor.ONE, SourceFactor.ONE, DestFactor.ZERO);
            info.cancel();
        }
    }
}
