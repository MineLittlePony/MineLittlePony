package com.brohoof.minelittlepony.forge;

import com.brohoof.minelittlepony.forge.MLPCommonProxy;

import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;

@Mod(modid = "minelp_forge", name = "Mine Little Pony Forge Hooks", version = "1.8")
public class MLPForge {

    public void init(FMLPostInitializationEvent init) {
        MLPCommonProxy.getInstance().setPonyArmors(new PonyArmors());
    }
}
