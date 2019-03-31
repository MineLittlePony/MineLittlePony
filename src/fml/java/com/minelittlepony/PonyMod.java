package com.minelittlepony;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

import com.minelittlepony.client.ForgeModMineLittlePony;

/**
 * Proxy MineLP so it doesn't run on the server.
 *
 * We do allow it to be installed on the server, but it won't do anything.
 * Only reason you would need it on that side is as a library for other mods.
 */
@Mod("minelittlepony")
public class PonyMod {
    public PonyMod() {
        DistExecutor.runWhenOn(Dist.CLIENT, () -> this::initOnClient);
    }

    void initOnClient() {
        FMLJavaModLoadingContext.get().getModEventBus().register(new ForgeModMineLittlePony());
    }
}
