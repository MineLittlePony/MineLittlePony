package com.minelittlepony.render;

import java.io.IOException;
import javax.annotation.Nullable;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.minelittlepony.MineLittlePony;
import com.minelittlepony.PonyConfig;
import com.minelittlepony.model.components.ModelPonyHead;
import com.minelittlepony.pony.data.Pony;
import com.mojang.authlib.GameProfile;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.renderer.tileentity.TileEntitySkullRenderer;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.tileentity.TileEntitySkull;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;

@Mixin(TileEntitySkullRenderer.class)
public abstract class PonySkullRenderer extends TileEntitySpecialRenderer<TileEntitySkull> {

    private final ModelPonyHead ponyHead = new ModelPonyHead();

    private boolean renderAsPony = false;

    @Inject(method = "renderSkull(FFFLnet/minecraft/util/EnumFacing;FILcom/mojang/authlib/GameProfile;IF)V",
            at = @At("HEAD"))
    private void onRenderSkull(float x, float y, float z, EnumFacing facing, float rotationIn, int skullType, @Nullable GameProfile profile, int destroyStage, float animateTicks, CallbackInfo info) {

        PonyConfig config = MineLittlePony.getConfig();

        switch (skullType)
        {
            default:
            case 0: //skeleton
            case 1: //wither skeleton
                renderAsPony = config.skeletons;
                break;
            case 2: //zombie
                renderAsPony = config.zombies;
                break;
            case 3: // player
                renderAsPony = true;
                break;
            case 4: // creeper
            case 5: // dragon
                renderAsPony = false;
        }
    }

    @Redirect(method = "renderSkull(FFFLnet/minecraft/util/EnumFacing;FILcom/mojang/authlib/GameProfile;IF)V",
                at = @At(value = "INVOKE",
                        target = "Lnet/minecraft/client/model/ModelBase;render(Let/minecraft/entity/Entity;FFFFFFF)V"))
    private void redirectRender(ModelBase self, Entity entity, float ticks, float swing, float swingAmount, float age, float headYaw, float headPitch, float scale) {
        if (renderAsPony) {
            self = ponyHead;
        }

        self.render(entity, swing, swingAmount, age, headYaw, headPitch, scale);
    }

    @Inject(method = "bindTexture(Lnet/minecraft/util/ResourceLocation;)V",
            at = @At("HEAD"), cancellable = true)
    private void onBindTexture(ResourceLocation location, CallbackInfo info) {
        location = resolvePonyResource(location);

        Pony pony = MineLittlePony.getInstance().getManager().getPony(location, false);
        ponyHead.metadata = pony.getMetadata();
        super.bindTexture(location);
    }

    private ResourceLocation resolvePonyResource(ResourceLocation human) {
        String domain = human.getResourceDomain();
        String path = human.getResourcePath();
        if (domain.equals("minecraft")) {
            domain = "minelittlepony";
        }

        ResourceLocation pony = new ResourceLocation(domain, path);

        try {
            Minecraft.getMinecraft().getResourceManager().getResource(pony);
            return pony;
        } catch (IOException e) {
            return human;
        }
    }
}
