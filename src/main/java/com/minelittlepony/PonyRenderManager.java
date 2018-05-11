package com.minelittlepony;

import java.util.Map;

import com.google.common.collect.Maps;
import com.minelittlepony.mixin.MixinRenderManager;
import com.minelittlepony.hdskins.gui.EntityPonyModel;
import com.minelittlepony.hdskins.gui.RenderPonyModel;
import com.minelittlepony.model.player.PlayerModels;
import com.minelittlepony.render.LevitatingItemRenderer;
import com.minelittlepony.render.player.RenderPonyPlayer;
import com.minelittlepony.render.ponies.RenderPonyGuardian;
import com.minelittlepony.render.ponies.RenderPonyIllager;
import com.minelittlepony.render.ponies.RenderPonyPigman;
import com.minelittlepony.render.ponies.RenderPonySkeleton;
import com.minelittlepony.render.ponies.RenderPonyVex;
import com.minelittlepony.render.ponies.RenderPonyVillager;
import com.minelittlepony.render.ponies.RenderPonyWitch;
import com.minelittlepony.render.ponies.RenderPonyZombie;
import com.minelittlepony.render.ponies.RenderPonyZombieVillager;
import com.mumfrey.liteloader.util.ModUtilities;

import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.monster.EntityElderGuardian;
import net.minecraft.entity.monster.EntityEvoker;
import net.minecraft.entity.monster.EntityGiantZombie;
import net.minecraft.entity.monster.EntityGuardian;
import net.minecraft.entity.monster.EntityHusk;
import net.minecraft.entity.monster.EntityIllusionIllager;
import net.minecraft.entity.monster.EntityPigZombie;
import net.minecraft.entity.monster.EntitySkeleton;
import net.minecraft.entity.monster.EntityStray;
import net.minecraft.entity.monster.EntityVex;
import net.minecraft.entity.monster.EntityVindicator;
import net.minecraft.entity.monster.EntityWitch;
import net.minecraft.entity.monster.EntityWitherSkeleton;
import net.minecraft.entity.monster.EntityZombie;
import net.minecraft.entity.monster.EntityZombieVillager;
import net.minecraft.entity.passive.EntityVillager;

/**
 * Render manager responsible for replacing and restoring entity renderers when the client settings change.
 * Old values of persisted internally.
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

        registerPlayerSkin(manager, PlayerModels.EARTH);
        registerPlayerSkin(manager, PlayerModels.PEGASUS);
        registerPlayerSkin(manager, PlayerModels.ALICORN);
        registerPlayerSkin(manager, PlayerModels.ZEBRA);
    }

    private void registerPlayerSkin(RenderManager manager, PlayerModels playerModel) {
        addPlayerSkin(manager, false, playerModel);
        addPlayerSkin(manager, true, playerModel);
    }

    private void addPlayerSkin(RenderManager manager, boolean slimArms, PlayerModels playerModel) {
        RenderPonyPlayer renderer = new RenderPonyPlayer(manager, slimArms, playerModel.getModel(slimArms));

        ((MixinRenderManager)manager).getSkinMap().put(playerModel.getId(slimArms), renderer);
    }

    /**
     * Registers all entity model replacements. (except for players).
     */
    public void initializeMobRenderers(RenderManager manager, PonyConfig config) {

        if (config.villagers) {
            pushNewRenderer(manager, EntityVillager.class, new RenderPonyVillager(manager));
            pushNewRenderer(manager, EntityWitch.class, new RenderPonyWitch(manager));
            pushNewRenderer(manager, EntityZombieVillager.class, new RenderPonyZombieVillager(manager));
            MineLittlePony.logger.info("Villagers are now ponies.");
        } else {
            restoreRenderer(EntityVillager.class);
            restoreRenderer(EntityWitch.class);
            restoreRenderer(EntityZombieVillager.class);
        }

        if (config.zombies) {
            pushNewRenderer(manager, EntityZombie.class, new RenderPonyZombie<>(manager));
            pushNewRenderer(manager, EntityHusk.class, new RenderPonyZombie.Husk(manager));
            pushNewRenderer(manager, EntityGiantZombie.class, new RenderPonyZombie.Giant(manager));
            MineLittlePony.logger.info("Zombies are now ponies.");
        } else {
            restoreRenderer(EntityZombie.class);
            restoreRenderer(EntityHusk.class);
            restoreRenderer(EntityGiantZombie.class);
        }

        if (config.pigzombies) {
            pushNewRenderer(manager, EntityPigZombie.class, new RenderPonyPigman(manager));
            MineLittlePony.logger.info("Zombie pigmen are now ponies.");
        } else {
            restoreRenderer(EntityPigZombie.class);
        }

        if (config.skeletons) {
            pushNewRenderer(manager, EntitySkeleton.class, new RenderPonySkeleton<>(manager));
            pushNewRenderer(manager, EntityStray.class, new RenderPonySkeleton.Stray(manager));
            pushNewRenderer(manager, EntityWitherSkeleton.class, new RenderPonySkeleton.Wither(manager));
            MineLittlePony.logger.info("Skeletons are now ponies.");
        } else {
            restoreRenderer(EntitySkeleton.class);
            restoreRenderer(EntityStray.class);
            restoreRenderer(EntityWitherSkeleton.class);
        }

        if (config.illagers) {
            pushNewRenderer(manager, EntityVex.class, new RenderPonyVex(manager));
            pushNewRenderer(manager, EntityEvoker.class, new RenderPonyIllager.Evoker(manager));
            pushNewRenderer(manager, EntityVindicator.class, new RenderPonyIllager.Vindicator(manager));
            pushNewRenderer(manager, EntityIllusionIllager.class, new RenderPonyIllager.Illusionist(manager));
            MineLittlePony.logger.info("Illagers are now ponies.");
        } else {
            restoreRenderer(EntityVex.class);
            restoreRenderer(EntityEvoker.class);
            restoreRenderer(EntityVindicator.class);
            restoreRenderer(EntityIllusionIllager.class);
        }

        if (config.guardians) {
            pushNewRenderer(manager, EntityGuardian.class, new RenderPonyGuardian(manager));
            pushNewRenderer(manager, EntityElderGuardian.class, new RenderPonyGuardian.Elder(manager));
        } else {
            restoreRenderer(EntityGuardian.class);
            restoreRenderer(EntityElderGuardian.class);
        }
    }

    /**
     * Pushes a new renderer replacement storing the original internally. This change can be undone with {@link #restoreRenderer(Class)}
     * @param manager The render manager
     * @param type    The type to replace
     * @param renderer The replacement value
     * @param <T> The entity type
     */
    @SuppressWarnings("unchecked")
    public <T extends Entity, V extends T> void pushNewRenderer(RenderManager manager, Class<V> type, Render<T> renderer) {
        if (!renderMap.containsKey(type)) {
            renderMap.put(type, manager.getEntityClassRenderObject(type));
        }
        ModUtilities.addRenderer((Class<T>)type, renderer);
    }

    /**
     * Restores a renderer to its previous value.
     */
    @SuppressWarnings("unchecked")
    public <T extends Entity> void restoreRenderer(Class<T> type) {
        if (renderMap.containsKey(type)) {
            ModUtilities.addRenderer(type, (Render<T>)renderMap.get(type));
        }
    }

    public LevitatingItemRenderer getMagicRenderer() {
        return magicRenderer;
    }
}
