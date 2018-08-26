package com.minelittlepony.mixin;

import com.minelittlepony.MineLittlePony;
import com.voxelmodpack.hdskins.INetworkPlayerInfo;
import net.minecraft.client.network.NetworkPlayerInfo;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(NetworkPlayerInfo.class)
public abstract class MixinNetworkPlayerInfo implements INetworkPlayerInfo {

    @Inject(method = "getSkinType()Ljava/lang/String;", at = @At("RETURN"), cancellable = true)
    private void getSkinType(CallbackInfoReturnable<String> info) {
        info.setReturnValue(MineLittlePony.getInstance().getManager()
                .getPony((NetworkPlayerInfo) (Object) this)
                .getRace(false)
                .getModel()
                .getId("slim".equals(info.getReturnValue())));
    }
}
