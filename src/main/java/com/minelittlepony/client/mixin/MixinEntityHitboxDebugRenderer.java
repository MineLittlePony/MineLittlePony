package com.minelittlepony.client.mixin;

import net.minecraft.client.render.debug.DebugRenderer;
import net.minecraft.client.render.debug.EntityHitboxDebugRenderer;
import net.minecraft.entity.Entity;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.minelittlepony.client.render.DebugBoundingBoxRenderer;

@Mixin(EntityHitboxDebugRenderer.class)
abstract class MixinEntityHitboxDebugRenderer implements DebugRenderer.Renderer {
    @Inject(method = "drawHitbox", at = @At("RETURN"))
    private void onDrawHitbox(Entity entity, float tickProgress, boolean inLocalServer, CallbackInfo info) {
        DebugBoundingBoxRenderer.drawHitboxes(entity, tickProgress, inLocalServer);
    }
}
