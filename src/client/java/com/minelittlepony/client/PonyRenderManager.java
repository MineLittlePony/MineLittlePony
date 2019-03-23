package com.minelittlepony.client;

import java.util.Map;

import com.google.common.collect.Maps;
import com.minelittlepony.client.ducks.IRenderPony;
import com.minelittlepony.client.mixin.MixinRenderManager;
import com.minelittlepony.client.model.races.PlayerModels;
import com.minelittlepony.client.render.LevitatingItemRenderer;
import com.minelittlepony.client.render.entities.MobRenderers;
import com.minelittlepony.client.render.entities.player.RenderPonyPlayer;
import com.minelittlepony.common.settings.PonyConfig;
import com.minelittlepony.hdskins.client.gui.RenderPonyModel;
import com.minelittlepony.hdskins.entity.EntityPonyModel;
import com.mumfrey.liteloader.util.ModUtilities;

import javax.annotation.Nullable;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderLivingBase;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;

/**
 * Render manager responsible for replacing and restoring entity renderers when the client settings change.
 * Old values are persisted internally.
 */
public class PonyRenderManager {

    private static final PonyRenderManager renderManager = new PonyRenderManager();

    /**
     * Gets the static pony render manager responsible for all entity renderers.
     */
    public static PonyRenderManager getInstance() {
        return renderManager;
    }

    private LevitatingItemRenderer magicRenderer = new LevitatingItemRenderer();


    private final Map<Class<? extends Entity>, Render<?>> renderMap = Maps.newHashMap();

    /**
     * Registers all new player skin types. (currently only pony and slimpony).
     */
    public void initialisePlayerRenderers(RenderManager manager) {
        // Preview on the select skin gui
        ModUtilities.addRenderer(EntityPonyModel.class, new RenderPonyModel(manager));

        PlayerModels[] models = PlayerModels.values();

        for (int i = 1; i < models.length; i++) {
            registerPlayerSkin(manager, models[i]);
        }
    }

    private void registerPlayerSkin(RenderManager manager, PlayerModels playerModel) {
        addPlayerSkin(manager, false, playerModel);
        addPlayerSkin(manager, true, playerModel);
    }

    private void addPlayerSkin(RenderManager manager, boolean slimArms, PlayerModels playerModel) {
        RenderPonyPlayer renderer = playerModel.createRenderer(manager, slimArms);

        ((MixinRenderManager)manager).getSkinMap().put(playerModel.getId(slimArms), renderer);
    }

    /**
     * Registers all entity model replacements. (except for players).
     */
    public void initializeMobRenderers(RenderManager manager, PonyConfig config) {
        for (MobRenderers i : MobRenderers.values()) {
            i.apply(this, manager);
        }
    }

    /**
     *
     * Replaces an entity renderer depending on whether we want ponies or not.
     *
     * @param state   True if we want ponies (the original will be stored)
     * @param manager The render manager
     * @param type    The type to replace
     * @param renderer The replacement value
     * @param <T> The entity type
     */
    @SuppressWarnings("unchecked")
    public <T extends Entity, V extends T> void switchRenderer(boolean state, RenderManager manager, Class<V> type, Render<T> renderer) {
        if (state) {
            if (!renderMap.containsKey(type)) {
                renderMap.put(type, manager.getEntityClassRenderObject(type));
            }
            ModUtilities.addRenderer((Class<T>)type, renderer);
        } else {
            if (renderMap.containsKey(type)) {
                ModUtilities.addRenderer(type, (Render<V>)renderMap.get(type));
            }
        }
    }

    public LevitatingItemRenderer getMagicRenderer() {
        return magicRenderer;
    }

    @SuppressWarnings("unchecked")
    @Nullable
    public <T extends EntityLivingBase, R extends RenderLivingBase<T> & IRenderPony<T>> R getPonyRenderer(@Nullable Entity entity) {
        if (entity == null || !(entity instanceof EntityLivingBase)) {
            return null;
        }

        Render<Entity> renderer = Minecraft.getMinecraft().getRenderManager().getEntityRenderObject(entity);

        if (renderer instanceof RenderLivingBase && renderer instanceof IRenderPony) {
            return (R)(Object)renderer;
        }

        return null;
    }
}
