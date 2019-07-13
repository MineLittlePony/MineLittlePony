package com.minelittlepony.client.render.entities;

import com.minelittlepony.client.MineLittlePony;
import com.minelittlepony.client.PonyRenderManager;
import com.minelittlepony.common.config.Value;
import com.minelittlepony.common.util.settings.Setting;

import java.util.Arrays;
import java.util.List;
import java.util.function.BiConsumer;

import com.minelittlepony.settings.PonyConfig;
import net.minecraft.entity.mob.*;
import net.minecraft.entity.passive.*;

/**
 * Central location where new entity renderers are registered and applied.
 */
public enum MobRenderers {
    VILLAGERS(PonyConfig.INSTANCE.villagers, (state, pony) -> {
        pony.switchRenderer(state, VillagerEntity.class, RenderPonyVillager::new);
        pony.switchRenderer(state, WitchEntity.class, RenderPonyWitch::new);
        pony.switchRenderer(state, ZombieVillagerEntity.class, RenderPonyZombieVillager::new);
        pony.switchRenderer(state, WanderingTraderEntity.class, RenderPonyTrader::new);
        pony.switchRenderer(state, PillagerEntity.class, RenderPonyPillager::new);
    }),
    ZOMBIES(PonyConfig.INSTANCE.zombies, (state, pony) -> {
        pony.switchRenderer(state, ZombieEntity.class, RenderPonyZombie::new);
        pony.switchRenderer(state, HuskEntity.class, RenderPonyZombie.Husk::new);
        pony.switchRenderer(state, GiantEntity.class, RenderPonyZombie.Giant::new);
        pony.switchRenderer(state, DrownedEntity.class, RenderPonyZombie.Drowned::new);
    }),
    PIGZOMBIES(PonyConfig.INSTANCE.pigzombies, (state, pony) -> {
        pony.switchRenderer(state, ZombiePigmanEntity.class, RenderPonyZombie.Pigman::new);
    }),
    SKELETONS(PonyConfig.INSTANCE.skeletons, (state, pony) -> {
        pony.switchRenderer(state, SkeletonEntity.class, RenderPonySkeleton::new);
        pony.switchRenderer(state, StrayEntity.class, RenderPonySkeleton.Stray::new);
        pony.switchRenderer(state, WitherSkeletonEntity.class, RenderPonySkeleton.Wither::new);
    }),
    ILLAGERS(PonyConfig.INSTANCE.illagers, (state, pony) -> {
        pony.switchRenderer(state, VexEntity.class, RenderPonyVex::new);
        pony.switchRenderer(state, EvokerEntity.class, RenderPonyIllager.Evoker::new);
        pony.switchRenderer(state, VindicatorEntity.class, RenderPonyIllager.Vindicator::new);
        pony.switchRenderer(state, IllusionerEntity.class, RenderPonyIllager.Illusionist::new);
    }),
    GUARDIANS(PonyConfig.INSTANCE.guardians, (state, pony) -> {
        pony.switchRenderer(state, GuardianEntity.class, RenderPonyGuardian::new);
        pony.switchRenderer(state, ElderGuardianEntity.class, RenderPonyGuardian.Elder::new);
    }),
    ENDERMEN(PonyConfig.INSTANCE.endermen, (state, pony) -> {
        pony.switchRenderer(state, EndermanEntity.class, RenderEnderStallion::new);
    });

    public static final List<MobRenderers> registry = Arrays.asList(values());

    private final Value<Boolean> config;
    private final BiConsumer<Boolean, PonyRenderManager> changer;

    MobRenderers(Value<Boolean> config, BiConsumer<Boolean, PonyRenderManager> changer) {
        this.config = config;
        this.changer = changer;
    }

    public boolean set(boolean value) {
        config.set(value);
        apply(PonyRenderManager.getInstance());
        return config.get();
    }

    public boolean get() {
        return config.get();
    }

    public void apply(PonyRenderManager pony) {
        boolean state = get();
        changer.accept(state, pony);
        if (state) {
            MineLittlePony.logger.info(name() + " are now ponies.");
        } else {
            MineLittlePony.logger.info(name() + " are no longer ponies.");
        }
    }
}