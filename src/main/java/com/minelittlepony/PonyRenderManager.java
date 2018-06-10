package com.minelittlepony;

import com.google.common.collect.Maps;
import com.minelittlepony.hdskins.gui.EntityPonyModel;
import com.minelittlepony.hdskins.gui.RenderPonyModel;
import com.minelittlepony.model.player.PlayerModels;
import com.minelittlepony.render.LevitatingItemRenderer;
import com.minelittlepony.render.player.RenderPonyPlayer;
import com.minelittlepony.render.ponies.MobRenderers;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.entity.RenderPlayer;
import net.minecraft.entity.Entity;
import net.minecraftforge.fml.client.registry.IRenderFactory;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import net.minecraftforge.fml.relauncher.ReflectionHelper;

import java.util.Map;

/**
 * Render manager responsible for replacing and restoring entity renderers when the client settings change.
 * Old values are persisted internally.
 */
public class PonyRenderManager {

    private LevitatingItemRenderer magicRenderer = new LevitatingItemRenderer();

    private final Map<Class<? extends Entity>, Render<?>> renderMap = Maps.newHashMap();

    private final RenderManager manager;
    private final Map<String, RenderPlayer> skinMap;

    public PonyRenderManager(RenderManager manager) {
        this.manager = manager;
        skinMap = ReflectionHelper.getPrivateValue(RenderManager.class, manager, "field_178636_l", "skinMap");
    }

    /**
     * Registers all new player skin types. (currently only pony and slimpony).
     */
    public void initialisePlayerRenderers() {
        // Preview on the select skin gui
        RenderingRegistry.registerEntityRenderingHandler(EntityPonyModel.class, RenderPonyModel::new);

        for (PlayerModels i : PlayerModels.values()) {
            if (i != PlayerModels.HUMAN) {
                registerPlayerSkin(i);
            }
        }
    }

    private void registerPlayerSkin(PlayerModels playerModel) {
        addPlayerSkin(false, playerModel);
        addPlayerSkin(true, playerModel);
    }

    private void addPlayerSkin(boolean slimArms, PlayerModels playerModel) {
        RenderPonyPlayer renderer = new RenderPonyPlayer(manager, slimArms, playerModel.getModel(slimArms));

        Map<String, RenderPlayer> skinMap = ReflectionHelper.getPrivateValue(RenderManager.class, manager, "field_178636_l", "skinMap");

        skinMap.put(playerModel.getId(slimArms), renderer);
    }

    /**
     * Registers all entity model replacements. (except for players).
     */
    public void initializeMobRenderers() {
        for (MobRenderers i : MobRenderers.values()) {
            boolean state = i.get();
            i.register(state, this);
            if (state) {
                MineLittlePony.logger.info(i.name() + " are now ponies.");
            }
        }

        RenderingRegistry.loadEntityRenderers(manager, manager.entityRenderMap);
    }

    /**
     * Replaces an entity renderer depending on whether we want ponies or not.
     *
     * @param state True if we want ponies (the original will be stored)
     * @param type The type to replace
     * @param renderer The replacement value
     * @param <T> The entity type
     */
    @SuppressWarnings("unchecked")
    public <T extends Entity> void switchRenderer(boolean state, Class<T> type, IRenderFactory<T> renderer) {
        // always put the original, regardless of state
        if (!renderMap.containsKey(type)) {
            renderMap.put(type, manager.getEntityClassRenderObject(type));
        }
        if (state) {
            RenderingRegistry.registerEntityRenderingHandler(type, renderer);
        } else if (renderMap.containsKey(type)) {
            RenderingRegistry.registerEntityRenderingHandler(type, mngr -> (Render<T>) renderMap.get(type));
        }

    }

    public LevitatingItemRenderer getMagicRenderer() {
        return magicRenderer;
    }
}
