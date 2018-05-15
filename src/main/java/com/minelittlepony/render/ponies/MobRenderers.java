package com.minelittlepony.render.ponies;

import com.minelittlepony.MineLittlePony;
import com.minelittlepony.PonyRenderManager;
import com.minelittlepony.settings.SensibleConfig.Setting;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.monster.*;
import net.minecraft.entity.passive.*;

/**
 * Central location where new entity renderers are registered and applied.
 *
 * Due to the limitations in Mumfrey's framework, needs to be paired with a field in PonyConfig.
 */
public enum MobRenderers implements Setting {
    VILLAGERS {
        @Override
        public void register(boolean state, PonyRenderManager pony, RenderManager manager) {
            pony.switchRenderer(state, manager, EntityVillager.class, new RenderPonyVillager(manager));
            pony.switchRenderer(state, manager, EntityWitch.class, new RenderPonyWitch(manager));
            pony.switchRenderer(state, manager, EntityZombieVillager.class, new RenderPonyZombieVillager(manager));
        }
    },
    ZOMBIES {
        @Override
        public void register(boolean state, PonyRenderManager pony, RenderManager manager) {
            pony.switchRenderer(state, manager, EntityZombie.class, new RenderPonyZombie<>(manager));
            pony.switchRenderer(state, manager, EntityHusk.class, new RenderPonyZombie.Husk(manager));
            pony.switchRenderer(state, manager, EntityGiantZombie.class, new RenderPonyZombie.Giant(manager));
        }
    },
    PIGZOMBIES {
        @Override
        public void register(boolean state, PonyRenderManager pony, RenderManager manager) {
            pony.switchRenderer(state, manager, EntityPigZombie.class, new RenderPonyPigman(manager));
        }
    },
    SKELETONS {
        @Override
        public void register(boolean state, PonyRenderManager pony, RenderManager manager) {
            pony.switchRenderer(state, manager, EntitySkeleton.class, new RenderPonySkeleton<>(manager));
            pony.switchRenderer(state, manager, EntityStray.class, new RenderPonySkeleton.Stray(manager));
            pony.switchRenderer(state, manager, EntityWitherSkeleton.class, new RenderPonySkeleton.Wither(manager));
        }
    },
    ILLAGERS {
        @Override
        public void register(boolean state, PonyRenderManager pony, RenderManager manager) {
            pony.switchRenderer(state, manager, EntityVex.class, new RenderPonyVex(manager));
            pony.switchRenderer(state, manager, EntityEvoker.class, new RenderPonyIllager.Evoker(manager));
            pony.switchRenderer(state, manager, EntityVindicator.class, new RenderPonyIllager.Vindicator(manager));
            pony.switchRenderer(state, manager, EntityIllusionIllager.class, new RenderPonyIllager.Illusionist(manager));
        }
    },
    GUARDIANS {
        @Override
        public void register(boolean state, PonyRenderManager pony, RenderManager manager) {
            pony.switchRenderer(state, manager, EntityGuardian.class, new RenderPonyGuardian(manager));
            pony.switchRenderer(state, manager, EntityElderGuardian.class, new RenderPonyGuardian.Elder(manager));
        }
    };

    @Override
    public void set(boolean value) {
        Setting.super.set(value);
        apply(MineLittlePony.getInstance().getRenderManager(), Minecraft.getMinecraft().getRenderManager());
    }

    public void apply(PonyRenderManager pony, RenderManager manager) {
        boolean state = get();
        register(state, pony, manager);
        if (state) {
            MineLittlePony.logger.info(name() + " are now ponies.");
        } else {
            MineLittlePony.logger.info(name() + " are no longer ponies.");
        }
    }

    public abstract void register(boolean state, PonyRenderManager pony, RenderManager manager);
}