package com.minelittlepony.client.mixin;

import com.minelittlepony.client.IPreviewModel;
import com.minelittlepony.client.MineLittlePony;
import com.minelittlepony.client.model.entity.race.PlayerModels;

import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.render.entity.EntityRenderDispatcher;
import net.minecraft.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(EntityRenderDispatcher.class)
abstract class MixinEntityRenderDispatcher {
    @Redirect(
            method = "getRenderer(Lnet/minecraft/entity/Entity;)Lnet/minecraft/client/render/entity/EntityRenderer;",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/network/AbstractClientPlayerEntity;getModel()Ljava/lang/String;"))
    private String getPlayerModel(AbstractClientPlayerEntity player, Entity entity) {
        if (player instanceof IPreviewModel) {
            return player.getModel();
        }
        return PlayerModels.forRace(MineLittlePony.getInstance().getManager()
                .getPony(player)
                .getRace(false))
                .getId(player.getModel().contains("slim"));
    }
}
