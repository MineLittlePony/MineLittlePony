package com.minelittlepony.render.ponies;

import com.minelittlepony.MineLittlePony;
import com.minelittlepony.PonyConfig;
import com.minelittlepony.PonyRenderManager;
import com.minelittlepony.settings.Value;
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

import java.util.function.Function;

/**
 * Central location where new entity renderers are registered and applied.
 *
 * Due to the limitations in Mumfrey's framework, needs to be paired with a field in PonyConfig.
 */
public enum MobRenderers implements Value<Boolean> {
    VILLAGERS(PonyConfig::getVillagers) {
        @Override
        public void register(boolean state, PonyRenderManager pony, RenderManager manager) {
            pony.switchRenderer(state, manager, EntityVillager.class, new RenderPonyVillager(manager));
            pony.switchRenderer(state, manager, EntityWitch.class, new RenderPonyWitch(manager));
            pony.switchRenderer(state, manager, EntityZombieVillager.class, new RenderPonyZombieVillager(manager));
        }
    },
    ZOMBIES(PonyConfig::getZombies) {
        @Override
        public void register(boolean state, PonyRenderManager pony, RenderManager manager) {
            pony.switchRenderer(state, manager, EntityZombie.class, new RenderPonyZombie<>(manager));
            pony.switchRenderer(state, manager, EntityHusk.class, new RenderPonyZombie.Husk(manager));
            pony.switchRenderer(state, manager, EntityGiantZombie.class, new RenderPonyZombie.Giant(manager));
        }
    },
    PIGZOMBIES(PonyConfig::getPigzombies) {
        @Override
        public void register(boolean state, PonyRenderManager pony, RenderManager manager) {
            pony.switchRenderer(state, manager, EntityPigZombie.class, new RenderPonyPigman(manager));
        }
    },
    SKELETONS(PonyConfig::getSkeletons) {
        @Override
        public void register(boolean state, PonyRenderManager pony, RenderManager manager) {
            pony.switchRenderer(state, manager, EntitySkeleton.class, new RenderPonySkeleton<>(manager));
            pony.switchRenderer(state, manager, EntityStray.class, new RenderPonySkeleton.Stray(manager));
            pony.switchRenderer(state, manager, EntityWitherSkeleton.class, new RenderPonySkeleton.Wither(manager));
        }
    },
    ILLAGERS(PonyConfig::getIllagers) {
        @Override
        public void register(boolean state, PonyRenderManager pony, RenderManager manager) {
            pony.switchRenderer(state, manager, EntityVex.class, new RenderPonyVex(manager));
            pony.switchRenderer(state, manager, EntityEvoker.class, new RenderPonyIllager.Evoker(manager));
            pony.switchRenderer(state, manager, EntityVindicator.class, new RenderPonyIllager.Vindicator(manager));
            pony.switchRenderer(state, manager, EntityIllusionIllager.class, new RenderPonyIllager.Illusionist(manager));
        }
    },
    GUARDIANS(PonyConfig::getGuardians) {
        @Override
        public void register(boolean state, PonyRenderManager pony, RenderManager manager) {
            pony.switchRenderer(state, manager, EntityGuardian.class, new RenderPonyGuardian(manager));
            pony.switchRenderer(state, manager, EntityElderGuardian.class, new RenderPonyGuardian.Elder(manager));
        }
    };

    private final Value<Boolean> setting;

    MobRenderers(Function<PonyConfig, Value<Boolean>> setting) {
        this.setting = setting.apply(MineLittlePony.getConfig());
    }

    @Override
    public Boolean get() {
        return setting.get();
    }

    @Override
    public void set(Boolean value) {
        setting.set(value);
        apply(MineLittlePony.getInstance().getRenderManager(), Minecraft.getMinecraft().getRenderManager());
    }

    public void apply(PonyRenderManager pony, RenderManager manager) {
        boolean state = setting.get();
        register(state, pony, manager);
        if (state) {
            MineLittlePony.logger.info(name() + " are now ponies.");
        } else {
            MineLittlePony.logger.info(name() + " are no longer ponies.");
        }
    }

    public abstract void register(boolean state, PonyRenderManager pony, RenderManager manager);
}
