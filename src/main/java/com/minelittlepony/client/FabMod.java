package com.minelittlepony.client;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.keybinding.FabricKeyBinding;
import net.fabricmc.fabric.api.client.keybinding.KeyBindingRegistry;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.options.KeyBinding;
import net.minecraft.client.render.block.entity.BlockEntityRenderDispatcher;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.render.entity.EntityRenderDispatcher;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.util.InputUtil;
import net.minecraft.entity.Entity;
import net.minecraft.util.Identifier;

import com.minelittlepony.client.gui.hdskins.MineLPHDSkins;
import com.minelittlepony.client.mixin.MixinBlockEntityRenderDispatcher;
import com.minelittlepony.client.settings.ClientPonyConfig;
import com.minelittlepony.hdskins.mixin.MixinEntityRenderDispatcher;
import com.minelittlepony.settings.JsonConfig;

import java.nio.file.Path;
import java.util.function.Function;

public class FabMod implements ClientModInitializer, IModUtilities {

    @Override
    public void onInitializeClient() {
        MineLPClient mlp;

        if (FabricLoader.getInstance().isModLoaded("hdskins")) {
            mlp = new MineLPHDSkins(this);
        } else {
            mlp = new MineLPClient(this);
        }

        mlp.init(JsonConfig.of(getConfigDirectory().resolve("minelp.json"), ClientPonyConfig::new));
    }

    @Override
    public KeyBinding registerKeybind(String category, int key, String bindName) {
        // normalize Fabric's behavior
        if (bindName.startsWith("key.")) {
            bindName = bindName.replace("key.", "");
        }

        FabricKeyBinding binding = FabricKeyBinding.Builder.create(new Identifier(bindName) {
            @Override
            public String toString() { return getPath(); }
        }, InputUtil.Type.KEYSYM, key, category).build();

        KeyBindingRegistry.INSTANCE.register(binding);
        return binding;
    }

    @Override
    public <T extends BlockEntity> void addRenderer(Class<T> type, BlockEntityRenderer<T> renderer) {
        MixinBlockEntityRenderDispatcher mx = ((MixinBlockEntityRenderDispatcher)BlockEntityRenderDispatcher.INSTANCE);
        mx.getRenderers().put(type, renderer);
        renderer.setRenderManager(BlockEntityRenderDispatcher.INSTANCE);
    }

    @Override
    public <T extends Entity> void addRenderer(Class<T> type, Function<EntityRenderDispatcher, EntityRenderer<T>> renderer) {
        EntityRenderDispatcher mx = MinecraftClient.getInstance().getEntityRenderManager();
        ((MixinEntityRenderDispatcher)mx).getRenderers().put(type, renderer.apply(mx));
    }

    @Override
    public Path getConfigDirectory() {
        return FabricLoader.getInstance().getConfigDirectory().toPath();
    }

    @Override
    public Path getAssetsDirectory() {
        return FabricLoader.getInstance().getGameDirectory().toPath().resolve("assets");
    }
}