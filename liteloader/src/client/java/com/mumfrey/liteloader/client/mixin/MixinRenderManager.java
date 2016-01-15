package com.mumfrey.liteloader.client.mixin;

import java.util.Map;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import com.mumfrey.liteloader.client.ClientProxy;
import com.mumfrey.liteloader.client.ducks.IRenderManager;

import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.Entity;

@Mixin(RenderManager.class)
public abstract class MixinRenderManager implements IRenderManager
{
    @Shadow private Map<Class<? extends Entity>, Render> entityRenderMap;
    
    @Override
    public Map<Class<? extends Entity>, Render> getRenderMap()
    {
        return this.entityRenderMap;
    }
    
    @Redirect(method = "doRenderEntity(Lnet/minecraft/entity/Entity;DDDFFZ)Z", at = @At(
        value = "INVOKE",
        target = "Lnet/minecraft/client/renderer/entity/Render;doRender(Lnet/minecraft/entity/Entity;DDDFF)V"
    ))
    private void onRenderEntity(Render render, Entity entity, double x, double y, double z, float entityYaw, float partialTicks)
    {
        RenderManager source = (RenderManager)(Object)this;
        ClientProxy.onRenderEntity(source, render, entity, x, y, z, entityYaw, partialTicks);
        render.doRender(entity, x, y, z, entityYaw, partialTicks);
        ClientProxy.onPostRenderEntity(source, render, entity, x, y, z, entityYaw, partialTicks);
    }
}
