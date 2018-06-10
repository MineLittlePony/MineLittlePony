package com.voxelmodpack.hdskins;

import com.google.common.base.Joiner;
import com.voxelmodpack.hdskins.gui.EntityPlayerModel;
import com.voxelmodpack.hdskins.gui.GLWindow;
import com.voxelmodpack.hdskins.gui.RenderPlayerModel;
import com.voxelmodpack.hdskins.skins.SkinServer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.IReloadableResourceManager;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.ICrashCallable;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.util.List;

@Mod(modid = "hdskins", name = "HD Skins", version = "5.0.0")
public class HDSkins {

    @Mod.EventHandler
    public void init(FMLPreInitializationEvent event) {

        MinecraftForge.EVENT_BUS.register(this);

        Path json = event.getModConfigurationDirectory().toPath().resolve("skinservers.txt");
        List<String> list = SkinServer.defaultServers;
        try {
            list = Files.readAllLines(json);
        } catch (NoSuchFileException e) {
            // ignore this
        } catch (IOException e) {
            event.getModLog().warn("Error reading skin servers. Using defaults.", e);
        }

        event.getModLog().info("Using skin servers:\n" + Joiner.on('\n').join(list));

        IReloadableResourceManager irrm = (IReloadableResourceManager) Minecraft.getMinecraft().getResourceManager();
        irrm.registerReloadListener(HDSkinManager.INSTANCE);

        RenderingRegistry.registerEntityRenderingHandler(EntityPlayerModel.class, RenderPlayerModel::new);
        // register skin servers.
        for (String s : list) {
            try {
                HDSkinManager.INSTANCE.addSkinServer(SkinServer.from(s));
            } catch (IllegalArgumentException e) {
                e.printStackTrace();
            }
        }

        // Create a bogus crash callable so we can properly close the window in case of a crash.
        FMLCommonHandler.instance().registerCrashCallable(new ICrashCallable() {
            @Override
            public String getLabel() {
                return "HDSkins";
            }

            @Override
            public String call() {
                GLWindow.dispose();
                return "";
            }
        });

    }

    private boolean fullscreen;

    @SubscribeEvent
    public void onFullScreenToggled(TickEvent.ClientTickEvent event) {
        if (event.phase == TickEvent.Phase.START) {
            fullscreen = Minecraft.getMinecraft().isFullScreen();
        } else if (Minecraft.getMinecraft().isFullScreen() != fullscreen) {
            // fullscreen has changed since beginning of tick.
            GLWindow.refresh(Minecraft.getMinecraft().isFullScreen());
        }
    }

}
