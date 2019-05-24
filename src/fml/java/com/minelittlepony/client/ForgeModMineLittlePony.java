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
import net.minecraftforge.fml.common.gameevent.TickEvent.ClientTickEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.loading.FMLPaths;

public class ForgeModMineLittlePony implements IModUtilities {

    private final MineLPClient mlp = new MineLPClient(this);

    public ForgeModMineLittlePony(Minecraft mc) {
        FMLJavaModLoadingContext.get().getModEventBus().register(this);
        MinecraftForge.EVENT_BUS.register(this);

        mlp.init(Config.of(FMLPaths.CONFIGDIR.get().resolve("minelittlepony.json")));
        mlp.postInit(mc);
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
