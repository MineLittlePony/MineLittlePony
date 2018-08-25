package com.minelittlepony.mixin;

import com.minelittlepony.MineLittlePony;
import com.minelittlepony.PonyManager;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.minecraft.MinecraftProfileTexture.Type;
import com.voxelmodpack.hdskins.INetworkPlayerInfo;
import net.minecraft.client.network.NetworkPlayerInfo;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = NetworkPlayerInfo.class, priority = 999)
public abstract class MixinNetworkPlayerInfo implements INetworkPlayerInfo {

    @Shadow private String skinType;

    @Shadow @Final private GameProfile gameProfile;

    @Inject(method = "getSkinType()Ljava/lang/String;", at = @At("RETURN"), cancellable = true)
    private void getSkinType(CallbackInfoReturnable<String> info) {
        info.setReturnValue(MineLittlePony.getInstance().getManager()
                .getPony((NetworkPlayerInfo) (Object) this)
                .getRace(false)
                .getModel()
                .getId(usesSlimArms()));
    }

    private boolean usesSlimArms() {
        if (skinType == null) {

            return getProfileTexture(Type.SKIN)
                    .map(profile -> profile.getMetadata("model"))
                    .filter("slim"::equals)
                    .isPresent() || PonyManager.isSlimSkin(this.gameProfile.getId());

        }

        return "slim".equals(skinType);
    }
}
