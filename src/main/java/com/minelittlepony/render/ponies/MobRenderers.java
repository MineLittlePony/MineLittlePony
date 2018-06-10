package com.minelittlepony.render.ponies;

import com.minelittlepony.MineLittlePony;
import com.minelittlepony.PonyRenderManager;
import com.minelittlepony.settings.MobConfig;
import com.minelittlepony.settings.Setting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.entity.RenderManager;
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
import net.minecraftforge.fml.client.registry.RenderingRegistry;

/**
 * Central location where new entity renderers are registered and applied.
 *
 * Due to the limitations in Mumfrey's framework, needs to be paired with a field in PonyConfig.
 */
public enum MobRenderers implements Setting {
    VILLAGERS {
        @Override
        public void register(boolean state, PonyRenderManager pony) {
            pony.switchRenderer(state, EntityVillager.class, RenderPonyVillager::new);
            pony.switchRenderer(state, EntityWitch.class, RenderPonyWitch::new);
            pony.switchRenderer(state, EntityZombieVillager.class, RenderPonyZombieVillager::new);
        }
    },
    ZOMBIES {
        @Override
        public void register(boolean state, PonyRenderManager pony) {
            pony.switchRenderer(state, EntityZombie.class, RenderPonyZombie::new);
            pony.switchRenderer(state, EntityHusk.class, RenderPonyZombie.Husk::new);
            pony.switchRenderer(state, EntityGiantZombie.class, RenderPonyZombie.Giant::new);
        }
    },
    PIGZOMBIES {
        @Override
        public void register(boolean state, PonyRenderManager pony) {
            pony.switchRenderer(state, EntityPigZombie.class, RenderPonyPigman::new);
        }
    },
    SKELETONS {
        @Override
        public void register(boolean state, PonyRenderManager pony) {
            pony.switchRenderer(state, EntitySkeleton.class, RenderPonySkeleton::new);
            pony.switchRenderer(state, EntityStray.class, RenderPonySkeleton.Stray::new);
            pony.switchRenderer(state, EntityWitherSkeleton.class, RenderPonySkeleton.Wither::new);
        }
    },
    ILLAGERS {
        @Override
        public void register(boolean state, PonyRenderManager pony) {
            pony.switchRenderer(state, EntityVex.class, RenderPonyVex::new);
            pony.switchRenderer(state, EntityEvoker.class, RenderPonyIllager.Evoker::new);
            pony.switchRenderer(state, EntityVindicator.class, RenderPonyIllager.Vindicator::new);
            pony.switchRenderer(state, EntityIllusionIllager.class, RenderPonyIllager.Illusionist::new);
        }
    },
    GUARDIANS {
        @Override
        public void register(boolean state, PonyRenderManager pony) {
            pony.switchRenderer(state, EntityGuardian.class, RenderPonyGuardian::new);
            pony.switchRenderer(state, EntityElderGuardian.class, RenderPonyGuardian.Elder::new);
        }
    };

    @Override
    public Class<?> getEnclosingClass() {
        return MobConfig.class;
    }

    @Override
    public void set(boolean value) {
        Setting.super.set(value);
        apply(value, MineLittlePony.getInstance().getRenderManager(), Minecraft.getMinecraft().getRenderManager());
    }

    public void apply(boolean state, PonyRenderManager pony, RenderManager manager) {
        register(state, pony);

        RenderingRegistry.loadEntityRenderers(manager, manager.entityRenderMap);

        if (state) {
            MineLittlePony.logger.info(name() + " are now ponies.");
        } else {
            MineLittlePony.logger.info(name() + " are no longer ponies.");
        }
    }

    public abstract void register(boolean state, PonyRenderManager pony);
}
