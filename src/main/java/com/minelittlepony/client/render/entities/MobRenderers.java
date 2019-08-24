package com.minelittlepony.client.render.entities;

import com.minelittlepony.client.MineLittlePony;
import com.minelittlepony.client.PonyRenderManager;
import com.minelittlepony.common.util.settings.Setting;

import java.util.Arrays;
import java.util.List;
import java.util.function.BiConsumer;

import net.minecraft.entity.mob.*;
import net.minecraft.entity.passive.*;

/**
 * Central location where new entity renderers are registered and applied.
 */
public enum MobRenderers {
    VILLAGERS((state, pony) -> {
        pony.switchRenderer(state, VillagerEntity.class, RenderPonyVillager::new);
        pony.switchRenderer(state, WitchEntity.class, RenderPonyWitch::new);
        pony.switchRenderer(state, ZombieVillagerEntity.class, RenderPonyZombieVillager::new);
        pony.switchRenderer(state, WanderingTraderEntity.class, RenderPonyTrader::new);
    }),
    ILLAGERS((state, pony) -> {
        pony.switchRenderer(state, VexEntity.class, RenderPonyVex::new);
        pony.switchRenderer(state, EvokerEntity.class, RenderPonyIllager.Evoker::new);
        pony.switchRenderer(state, VindicatorEntity.class, RenderPonyIllager.Vindicator::new);
        pony.switchRenderer(state, IllusionerEntity.class, RenderPonyIllager.Illusionist::new);
        pony.switchRenderer(state, PillagerEntity.class, RenderPonyPillager::new);
    }),
    ZOMBIES((state, pony) -> {
        pony.switchRenderer(state, ZombieEntity.class, RenderPonyZombie::new);
        pony.switchRenderer(state, HuskEntity.class, RenderPonyZombie.Husk::new);
        pony.switchRenderer(state, GiantEntity.class, RenderPonyZombie.Giant::new);
        pony.switchRenderer(state, DrownedEntity.class, RenderPonyZombie.Drowned::new);
    }),
    PIGZOMBIES((state, pony) -> {
        pony.switchRenderer(state, ZombiePigmanEntity.class, RenderPonyZombie.Pigman::new);
    }),
    SKELETONS((state, pony) -> {
        pony.switchRenderer(state, SkeletonEntity.class, RenderPonySkeleton::new);
        pony.switchRenderer(state, StrayEntity.class, RenderPonySkeleton.Stray::new);
        pony.switchRenderer(state, WitherSkeletonEntity.class, RenderPonySkeleton.Wither::new);
    }),
    GUARDIANS((state, pony) -> {
        pony.switchRenderer(state, GuardianEntity.class, RenderPonyGuardian::new);
        pony.switchRenderer(state, ElderGuardianEntity.class, RenderPonyGuardian.Elder::new);
    }),
    ENDERMEN((state, pony) -> {
        pony.switchRenderer(state, EndermanEntity.class, RenderEnderStallion::new);
    });

    public static final List<MobRenderers> registry = Arrays.asList(values());

    private final BiConsumer<Boolean, PonyRenderManager> changer;

    MobRenderers(BiConsumer<Boolean, PonyRenderManager> changer) {
        this.changer = changer;
    }

    public Setting<Boolean> option() {
        return MineLittlePony.getInstance().getConfig().<Boolean>get(name().toLowerCase());
    }

    public boolean set(boolean value) {
        value = option().set(value);
        apply(PonyRenderManager.getInstance());
        return value;
    }

    public boolean get() {
        return option().get();
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