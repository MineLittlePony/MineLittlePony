package com.minelittlepony.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.minelittlepony.MineLittlePony;
import com.minelittlepony.Pony;
import com.minelittlepony.ducks.IPlayerInfo;

import net.minecraft.client.network.NetworkPlayerInfo;
import net.minecraft.util.ResourceLocation;

@Mixin(NetworkPlayerInfo.class)
public abstract class MixinNetworkPlayerInfo implements IPlayerInfo {

    @Shadow
    private String skinType;

    @Shadow
    public abstract ResourceLocation getLocationSkin();

    @Inject(method = "getSkinType()Ljava/lang/String;", at = @At("RETURN"))
    private void getSkinType(CallbackInfoReturnable<String> info) {
        ResourceLocation skin = getLocationSkin();
        if (skin != null) {
            info.setReturnValue(getPony(skin).getRace(false).getModel().getId(usesSlimArms()));
        }
    }

    protected Pony getPony(ResourceLocation skin) {
        return MineLittlePony.getInstance().getManager().getPony(skin, usesSlimArms());
    }

    @Override
    public Pony getPony() {
        return getPony(getLocationSkin());
    }

    @Override
    public boolean usesSlimArms() {
        return skinType == "slim";
    }
}
