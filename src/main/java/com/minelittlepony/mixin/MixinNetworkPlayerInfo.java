package com.minelittlepony.mixin;

import com.minelittlepony.MineLittlePony;
import com.minelittlepony.PonyManager;
import com.minelittlepony.avatar.impl.INetworkPlayerInfo;
import com.minelittlepony.avatar.texture.TextureData;
import com.minelittlepony.avatar.texture.TextureType;
import com.minelittlepony.ducks.IPlayerInfo;
import net.minecraft.client.network.NetworkPlayerInfo;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(NetworkPlayerInfo.class)
public abstract class MixinNetworkPlayerInfo implements IPlayerInfo, INetworkPlayerInfo {

    @Shadow
    private String skinType;

    @Inject(method = "getSkinType()Ljava/lang/String;", at = @At("RETURN"), cancellable = true)
    private void getSkinType(CallbackInfoReturnable<String> info) {
        info.setReturnValue(MineLittlePony.getInstance().getManager().getPony(unwrap()).getRace(false).getModel().getId(usesSlimArms()));
    }

    @Override
    public boolean usesSlimArms() {
        if (skinType == null) {
            TextureData skin = this.getPlayerTexture(TextureType.SKIN).orElse(null);

            if (skin != null) {
                return "slim".equals(skin.getProfile().getMetadata("model").orElse(null));
            }

            return PonyManager.isSlimSkin(unwrap().getGameProfile().getId());
        }

        return "slim".equals(skinType);
    }
}
