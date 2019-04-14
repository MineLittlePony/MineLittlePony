package com.minelittlepony;

import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
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
        FMLJavaModLoadingContext.get().getModEventBus().register(this);
    }

    @SubscribeEvent
    public void initOnClient(FMLClientSetupEvent event) {
        new ForgeModMineLittlePony();
    }
}
