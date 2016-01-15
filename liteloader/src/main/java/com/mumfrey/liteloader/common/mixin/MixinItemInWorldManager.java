package com.mumfrey.liteloader.common.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.mumfrey.liteloader.core.Proxy;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.server.management.ItemInWorldManager;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;

@Mixin(ItemInWorldManager.class)
public abstract class MixinItemInWorldManager
{
    @Inject(
        method = "onBlockClicked(Lnet/minecraft/util/BlockPos;Lnet/minecraft/util/EnumFacing;)V",
        cancellable = true,
        at = @At("HEAD")
    )
    private void onBlockClicked(BlockPos pos, EnumFacing side, CallbackInfo ci)
    {
        Proxy.onBlockClicked(ci, (ItemInWorldManager)(Object)this, pos, side);
    }
    
    @Inject(
        method = "activateBlockOrUseItem(Lnet/minecraft/entity/player/EntityPlayer;Lnet/minecraft/world/World;Lnet/minecraft/item/ItemStack;"
                + "Lnet/minecraft/util/BlockPos;Lnet/minecraft/util/EnumFacing;FFF)Z",
        cancellable = true,
        at = @At("HEAD")
    )
    private void onUseItem(EntityPlayer player, World worldIn, ItemStack stack, BlockPos pos, EnumFacing side, float offsetX, float offsetY,
            float offsetZ, CallbackInfoReturnable<Boolean> cir)
    {
        Proxy.onUseItem(cir, player, worldIn, stack, pos, side, offsetX, offsetY, offsetZ);
    }
}
