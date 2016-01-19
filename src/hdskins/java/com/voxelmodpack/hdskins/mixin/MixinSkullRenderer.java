package com.voxelmodpack.hdskins.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.At.Shift;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.google.common.base.Optional;
import com.mojang.authlib.GameProfile;
import com.voxelmodpack.hdskins.HDSkinManager;

import net.minecraft.client.renderer.tileentity.TileEntitySkullRenderer;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;

@Mixin(TileEntitySkullRenderer.class)
public abstract class MixinSkullRenderer extends TileEntitySpecialRenderer {

    @Inject(method = "renderSkull",
            require = 1,
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/renderer/tileentity/TileEntitySkullRenderer;bindTexture(Lnet/minecraft/util/ResourceLocation;)V",
                    ordinal = 4,
                    shift = Shift.AFTER) )
    private void onBindTexture(float x, float y, float z, EnumFacing facing, float rotation, int meta, GameProfile profile, int p_180543_8_,
            CallbackInfo ci) {
        if (profile != null) {
            Optional<ResourceLocation> skin = HDSkinManager.getSkin(profile);
            if (skin.isPresent())
                // rebind
                bindTexture(skin.get());
        }
    }
}
