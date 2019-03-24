package com.minelittlepony.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.tileentity.TileEntityRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.gameevent.TickEvent.ClientTickEvent;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

import com.minelittlepony.client.IModUtilities;
import com.minelittlepony.client.MineLPClient;

import java.io.File;

@Mod("minelittlepony")
public class ForgeModMineLittlePony implements IModUtilities {

    private final MineLPClient mlp = new MineLPClient(this);

    public ForgeModMineLittlePony() {
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::init);
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::posInit);
        MinecraftForge.EVENT_BUS.register(this);
    }

    private void init(final FMLCommonSetupEvent event) {

        // TODO: I don't know what forge did with `event.getModConfigurationDirectory()` but it's not where it used to be.

        File configDirectory = new File(Minecraft.getInstance().getFileResourcePacks().getParentFile(), "config");
        File configFile = new File(configDirectory, "minelittlepony.json");

        mlp.init(Config.of(configFile));
    }

    private void posInit(FMLClientSetupEvent event) {
        mlp.postInit(event.getMinecraftSupplier().get());
    }

    @SubscribeEvent
    public void onClientTick(ClientTickEvent event) {
        mlp.onTick(Minecraft.getInstance(), Minecraft.getInstance().world != null);
    }

    @Override
    public <T extends TileEntity> void addRenderer(Class<T> type, TileEntityRenderer<T> renderer) {
        ClientRegistry.bindTileEntitySpecialRenderer(type, renderer);
    }

    @Override
    public <T extends Entity> void addRenderer(Class<T> type, Render<T> renderer) {
        RenderingRegistry.registerEntityRenderingHandler(type, rm -> renderer);
    }

    @Override
    public boolean hasFml() {
        return true;
    }

    @Override
    public float getRenderPartialTicks() {
        return Minecraft.getInstance().getRenderPartialTicks();
    }
}
