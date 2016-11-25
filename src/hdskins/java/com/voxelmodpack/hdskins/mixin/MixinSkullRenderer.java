package com.voxelmodpack.hdskins.mixin;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.minecraft.MinecraftProfileTexture.Type;
import com.voxelmodpack.hdskins.HDSkinManager;
import net.minecraft.client.renderer.tileentity.TileEntitySkullRenderer;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.tileentity.TileEntitySkull;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.Optional;

@Mixin(TileEntitySkullRenderer.class)
public abstract class MixinSkullRenderer extends TileEntitySpecialRenderer<TileEntitySkull> {

    @Redirect(
            method = "renderSkull",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/renderer/tileentity/TileEntitySkullRenderer;bindTexture(Lnet/minecraft/util/ResourceLocation;)V",
                    ordinal = 4))
    private void onBindTexture(TileEntitySkullRenderer tesr, ResourceLocation rl, float x, float y, float z, EnumFacing facing, float rotation, int meta,
                               GameProfile profile, int p_180543_8_, float ticks) {
        if (profile != null) {
            Optional<ResourceLocation> skin = HDSkinManager.INSTANCE.getSkinLocation(profile, Type.SKIN, true);
            if (skin.isPresent())
                // rebind
                bindTexture(skin.get());
            else
                bindTexture(rl);
        } else
            bindTexture(rl);
    }
}
