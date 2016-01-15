package com.mumfrey.liteloader.client.mixin;

import java.util.Map;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import com.mumfrey.liteloader.client.ducks.ITileEntityRendererDispatcher;

import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.tileentity.TileEntity;

@Mixin(TileEntityRendererDispatcher.class)
public abstract class MixinTileEntityRendererDispatcher implements ITileEntityRendererDispatcher
{
    @Shadow private Map<Class<? extends TileEntity>, TileEntitySpecialRenderer> mapSpecialRenderers;
    
    @Override
    public Map<Class<? extends TileEntity>, TileEntitySpecialRenderer> getSpecialRenderMap()
    {
        return this.mapSpecialRenderers;
    }
}
