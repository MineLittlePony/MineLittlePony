package com.minelittlepony.client.mixin;

import net.minecraft.client.renderer.debug.*;
import net.minecraft.world.entity.Entity;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.minelittlepony.client.render.DebugBoundingBoxRenderer;

@Mixin(EntityHitboxDebugRenderer.class)
abstract class MixinEntityHitboxDebugRenderer implements DebugRenderer.SimpleDebugRenderer {
    @Inject(method = "showHitboxes", at = @At("RETURN"))
    private void onDrawHitbox(Entity entity, float tickDelta, boolean isServerEntity, CallbackInfo info) {
        if (!isServerEntity) {
            DebugBoundingBoxRenderer.drawHitboxes(entity, tickDelta);
            DebugBoundingBoxRenderer.drawFillyCamRays(entity, tickDelta);
        }
    }
}
