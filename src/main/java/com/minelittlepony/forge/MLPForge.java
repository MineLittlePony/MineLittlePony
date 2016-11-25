package com.minelittlepony.forge;

import com.minelittlepony.MineLittlePony;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;

@Mod(
        modid = "minelp_forge",
        name = "MineLP Forge Hooks",
        version = MineLittlePony.MOD_VERSION,
        clientSideOnly = true)
public class MLPForge {

    @SuppressWarnings("unused")
    @EventHandler
    public void init(FMLPostInitializationEvent init) {
        MLPCommonProxy.getInstance().setPonyArmors(new PonyArmors());
    }
}
