package com.minelittlepony;

import java.util.Map;

import com.google.common.collect.Maps;
import com.minelittlepony.hdskins.gui.EntityPonyModel;
import com.minelittlepony.hdskins.gui.RenderPonyModel;
import com.minelittlepony.model.PMAPI;
import com.minelittlepony.render.player.RenderPonyPlayer;
import com.minelittlepony.render.ponies.RenderPonyIllager;
import com.minelittlepony.render.ponies.RenderPonyPigman;
import com.minelittlepony.render.ponies.RenderPonySkeleton;
import com.minelittlepony.render.ponies.RenderPonyVex;
import com.minelittlepony.render.ponies.RenderPonyVillager;
import com.minelittlepony.render.ponies.RenderPonyZombie;
import com.minelittlepony.render.ponies.RenderPonyZombieVillager;
import com.mumfrey.liteloader.util.ModUtilities;

import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.monster.EntityEvoker;
import net.minecraft.entity.monster.EntityGiantZombie;
import net.minecraft.entity.monster.EntityHusk;
import net.minecraft.entity.monster.EntityIllusionIllager;
import net.minecraft.entity.monster.EntityPigZombie;
import net.minecraft.entity.monster.EntitySkeleton;
import net.minecraft.entity.monster.EntityStray;
import net.minecraft.entity.monster.EntityVex;
import net.minecraft.entity.monster.EntityVindicator;
import net.minecraft.entity.monster.EntityWitherSkeleton;
import net.minecraft.entity.monster.EntityZombie;
import net.minecraft.entity.monster.EntityZombieVillager;
import net.minecraft.entity.passive.EntityVillager;

/**
 * Render manager responsible for replacing and restoring entity renderers when the client settings change.
 * Old values of persisted internally.
 */
public class PonyRenderManager {

    private final Map<Class<? extends Entity>, Render<?>> renderMap = Maps.newHashMap();

    public PonyRenderManager() {

    }

    /**
     * Registers all new player skin types. (currently only pony and slimpony).
     */
    public void initialisePlayerRenderers(RenderManager rm) {
        // Preview on the select skin gui
        ModUtilities.addRenderer(EntityPonyModel.class, new RenderPonyModel(rm));

        new RenderPonyPlayer(rm, false, "pony", PMAPI.pony);
        new RenderPonyPlayer(rm, true, "slimpony", PMAPI.ponySmall);
        //TODO: Add skin types for each species? May require model break up.
    }

    /**
     * Registers all entity model replacements. (except for players).
     */
    public void initializeMobRenderers(RenderManager rm, PonyConfig config) {

        if (config.villagers) {
            pushNewRenderer(rm, EntityVillager.class, new RenderPonyVillager(rm));
            pushNewRenderer(rm, EntityZombieVillager.class, new RenderPonyZombieVillager(rm));
            MineLittlePony.logger.info("Villagers are now ponies.");
        } else {
            restoreRenderer(EntityVillager.class);
            restoreRenderer(EntityZombieVillager.class);
        }

        if (config.zombies) {
            pushNewRenderer(rm, EntityZombie.class, new RenderPonyZombie<>(rm));
            pushNewRenderer(rm, EntityHusk.class, new RenderPonyZombie.Husk(rm));
            pushNewRenderer(rm, EntityGiantZombie.class, new RenderPonyZombie.Giant(rm));
            MineLittlePony.logger.info("Zombies are now ponies.");
        } else {
            restoreRenderer(EntityZombie.class);
            restoreRenderer(EntityHusk.class);
            restoreRenderer(EntityGiantZombie.class);
        }

        if (config.pigzombies) {
            pushNewRenderer(rm, EntityPigZombie.class, new RenderPonyPigman(rm));
            MineLittlePony.logger.info("Zombie pigmen are now ponies.");
        } else {
            restoreRenderer(EntityPigZombie.class);
        }

        if (config.skeletons) {
            pushNewRenderer(rm, EntitySkeleton.class, new RenderPonySkeleton<>(rm));
            pushNewRenderer(rm, EntityStray.class, new RenderPonySkeleton.Stray(rm));
            pushNewRenderer(rm, EntityWitherSkeleton.class, new RenderPonySkeleton.Wither(rm));
            MineLittlePony.logger.info("Skeletons are now ponies.");
        } else {
            restoreRenderer(EntitySkeleton.class);
            restoreRenderer(EntityStray.class);
            restoreRenderer(EntityWitherSkeleton.class);
        }

        if (config.illagers) {
            pushNewRenderer(rm, EntityVex.class, new RenderPonyVex(rm));
            pushNewRenderer(rm, EntityEvoker.class, new RenderPonyIllager.Evoker(rm));
            pushNewRenderer(rm, EntityVindicator.class, new RenderPonyIllager.Vindicator(rm));
            pushNewRenderer(rm, EntityIllusionIllager.class, new RenderPonyIllager.Illusionist(rm));
            MineLittlePony.logger.info("Illagers are now ponies.");
        } else {
            restoreRenderer(EntityVex.class);
            restoreRenderer(EntityEvoker.class);
            restoreRenderer(EntityVindicator.class);
            restoreRenderer(EntityIllusionIllager.class);
        }
    }

    /**
     * Pushes a new renderer replacement storing the original internally. This change can be undone with {@link #restoreRenderer(Class)}
     * @param manager The render manager
     * @param type    The type to replace
     * @param renderer The replacement value
     * @param <T> The entity type
     */
    public <T extends Entity> void pushNewRenderer(RenderManager manager, Class<T> type, Render<T> renderer) {
        if (!renderMap.containsKey(type)) {
            renderMap.put(type, manager.getEntityClassRenderObject(type));
        }
        ModUtilities.addRenderer(type, renderer);
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
}
