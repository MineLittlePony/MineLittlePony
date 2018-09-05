package com.minelittlepony;

import java.util.Map;

import com.google.common.collect.Maps;
import com.minelittlepony.mixin.MixinRenderManager;
import com.minelittlepony.hdskins.gui.EntityPonyModel;
import com.minelittlepony.hdskins.gui.RenderPonyModel;
import com.minelittlepony.model.player.PlayerModels;
import com.minelittlepony.render.LevitatingItemRenderer;
import com.minelittlepony.render.player.RenderPonyPlayer;
import com.minelittlepony.render.ponies.MobRenderers;
import com.mumfrey.liteloader.util.ModUtilities;

import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.Entity;

/**
 * Render manager responsible for replacing and restoring entity renderers when the client settings change.
 * Old values are persisted internally.
 */
public class PonyRenderManager {

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
    public void initializeMobRenderers(RenderManager manager, IPonyConfig config) {
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
}
