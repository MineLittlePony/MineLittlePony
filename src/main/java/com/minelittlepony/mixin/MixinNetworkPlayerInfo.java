package com.minelittlepony.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.minelittlepony.MineLittlePony;
import com.minelittlepony.PonyManager;
import com.minelittlepony.ducks.IPlayerInfo;
import com.mojang.authlib.minecraft.MinecraftProfileTexture;
import com.mojang.authlib.minecraft.MinecraftProfileTexture.Type;
import com.voxelmodpack.hdskins.HDSkinManager;

import net.minecraft.client.network.NetworkPlayerInfo;

@Mixin(NetworkPlayerInfo.class)
public abstract class MixinNetworkPlayerInfo implements IPlayerInfo {

    @Shadow
    private String skinType;

    @Inject(method = "getSkinType()Ljava/lang/String;", at = @At("RETURN"), cancellable = true)
    private void getSkinType(CallbackInfoReturnable<String> info) {
        info.setReturnValue(MineLittlePony.getInstance().getManager().getPony(unwrap()).getRace(false).getModel().getId(usesSlimArms()));
    }

    @Override
    public boolean usesSlimArms() {
        if (skinType == null) {
            MinecraftProfileTexture skin = HDSkinManager.INSTANCE.getProfileData(unwrap().getGameProfile()).get(Type.SKIN);

            if (skin != null) {
                return "slim".equals(skin.getMetadata("model"));
            }

            return PonyManager.isSlimSkin(unwrap().getGameProfile().getId());
        }

        return "slim".equals(skinType);
    }
}
