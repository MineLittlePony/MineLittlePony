package com.minelittlepony.client.render.entities;

import com.google.common.collect.Lists;
import com.minelittlepony.client.MineLittlePony;
import com.minelittlepony.client.PonyRenderManager;
import com.minelittlepony.settings.Config;
import com.minelittlepony.settings.Config.Setting;

import java.util.List;

import net.minecraft.entity.mob.*;
import net.minecraft.entity.passive.*;

/**
 * Central location where new entity renderers are registered and applied.
 */
public enum MobRenderers implements Setting<Boolean> {
    VILLAGERS {
        @Override
        void register(boolean state, PonyRenderManager pony) {
            pony.switchRenderer(state, VillagerEntity.class, RenderPonyVillager::new);
            pony.switchRenderer(state, WitchEntity.class, RenderPonyWitch::new);
            pony.switchRenderer(state, ZombieVillagerEntity.class, RenderPonyZombieVillager::new);
            pony.switchRenderer(state, WanderingTraderEntity.class, RenderPonyTrader::new);
            pony.switchRenderer(state, PillagerEntity.class, RenderPonyPillager::new);
        }
    },
    ZOMBIES {
        @Override
        void register(boolean state, PonyRenderManager pony) {
            pony.switchRenderer(state, ZombieEntity.class, RenderPonyZombie::new);
            pony.switchRenderer(state, HuskEntity.class, RenderPonyZombie.Husk::new);
            pony.switchRenderer(state, GiantEntity.class, RenderPonyZombie.Giant::new);
            pony.switchRenderer(state, DrownedEntity.class, RenderPonyZombie.Drowned::new);
        }
    },
    PIGZOMBIES {
        @Override
        void register(boolean state, PonyRenderManager pony) {
            pony.switchRenderer(state, ZombiePigmanEntity.class, RenderPonyZombie.Pigman::new);
        }
    },
    SKELETONS {
        @Override
        void register(boolean state, PonyRenderManager pony) {
            pony.switchRenderer(state, SkeletonEntity.class, RenderPonySkeleton::new);
            pony.switchRenderer(state, StrayEntity.class, RenderPonySkeleton.Stray::new);
            pony.switchRenderer(state, WitherSkeletonEntity.class, RenderPonySkeleton.Wither::new);
        }
    },
    ILLAGERS {
        @Override
        void register(boolean state, PonyRenderManager pony) {
            pony.switchRenderer(state, VexEntity.class, RenderPonyVex::new);
            pony.switchRenderer(state, EvokerEntity.class, RenderPonyIllager.Evoker::new);
            pony.switchRenderer(state, VindicatorEntity.class, RenderPonyIllager.Vindicator::new);
            pony.switchRenderer(state, IllusionerEntity.class, RenderPonyIllager.Illusionist::new);
        }
    },
    GUARDIANS {
        @Override
        void register(boolean state, PonyRenderManager pony) {
            pony.switchRenderer(state, GuardianEntity.class, RenderPonyGuardian::new);
            pony.switchRenderer(state, ElderGuardianEntity.class, RenderPonyGuardian.Elder::new);
        }
    },
    ENDERMEN {
        @Override
        void register(boolean state, PonyRenderManager pony) {
            pony.switchRenderer(state, EndermanEntity.class, RenderEnderStallion::new);
        }
    };

    public static final List<MobRenderers> registry = Lists.newArrayList(values());

    @Override
    public Boolean getDefault() {
        return true;
    }

    @Override
    public void set(Boolean value) {
        Setting.super.set(value);
        apply(PonyRenderManager.getInstance());
    }

    @Override
    public Config config() {
        return MineLittlePony.getInstance().getConfig();
    }

    public void apply(PonyRenderManager pony) {
        boolean state = get();
        register(state, pony);
        if (state) {
            MineLittlePony.logger.info(name() + " are now ponies.");
        } else {
            MineLittlePony.logger.info(name() + " are no longer ponies.");
        }
    }

    abstract void register(boolean state, PonyRenderManager pony);
}