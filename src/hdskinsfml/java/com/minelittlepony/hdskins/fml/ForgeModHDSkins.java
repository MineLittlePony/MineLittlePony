package com.minelittlepony.hdskins.fml;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.loading.FMLPaths;

@Mod("hdskins")
public class ForgeModHDSkins {

    private Config hdskins;

    public ForgeModHDSkins() {
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::commonInit);
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::clientInit);
        MinecraftForge.EVENT_BUS.register(this);
    }

    private void commonInit(final FMLCommonSetupEvent event) {

        hdskins = Config.of(FMLPaths.CONFIGDIR.get().resolve("hdskins.json"));

        hdskins.init();
    }

    private void clientInit(FMLClientSetupEvent event) {
        hdskins.initComplete();
    }
}
