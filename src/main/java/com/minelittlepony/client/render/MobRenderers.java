package com.minelittlepony.client.render;

import com.minelittlepony.client.MineLittlePony;
import com.minelittlepony.client.render.entity.*;
import com.minelittlepony.client.render.entity.npc.*;
import com.minelittlepony.common.util.settings.Setting;

import java.util.HashMap;
import java.util.Map;
import java.util.function.BiConsumer;

import net.minecraft.entity.EntityType;

/**
 * Central location where new entity renderers are registered and applied.
 */
public final class MobRenderers {
    public static final Map<String, MobRenderers> REGISTRY = new HashMap<>();

    public static final MobRenderers VILLAGER = register("villagers", (state, pony) -> {
        pony.switchRenderer(state, EntityType.VILLAGER, VillagerPonyRenderer::new);
        pony.switchRenderer(state, EntityType.WITCH, WitchRenderer::new);
        pony.switchRenderer(state, EntityType.ZOMBIE_VILLAGER, ZomponyVillagerRenderer::new);
        pony.switchRenderer(state, EntityType.WANDERING_TRADER, TraderRenderer::new);
    });
    public static final MobRenderers ILLAGER = register("illagers", (state, pony) -> {
        pony.switchRenderer(state, EntityType.VEX, VexRenderer::new);
        pony.switchRenderer(state, EntityType.EVOKER, IllagerPonyRenderer.Evoker::new);
        pony.switchRenderer(state, EntityType.VINDICATOR, IllagerPonyRenderer.Vindicator::new);
        pony.switchRenderer(state, EntityType.ILLUSIONER, IllagerPonyRenderer.Illusionist::new);
        pony.switchRenderer(state, EntityType.PILLAGER, PillagerRenderer::new);
    });
    public static final MobRenderers ZOMBIE = register("zombies", (state, pony) -> {
        pony.switchRenderer(state, EntityType.ZOMBIE, ZomponyRenderer::new);
        pony.switchRenderer(state, EntityType.HUSK, ZomponyRenderer.Husk::new);
        pony.switchRenderer(state, EntityType.GIANT, ZomponyRenderer.Giant::new);
        pony.switchRenderer(state, EntityType.DROWNED, ZomponyRenderer.Drowned::new);
    });
    public static final MobRenderers ZOMBIE_PIGMAN = register("pigzombies", (state, pony) -> {
        pony.switchRenderer(state, EntityType.ZOMBIFIED_PIGLIN, ZomponyRenderer.Piglin::new);
    });
    public static final MobRenderers SKELETON = register("skeletons", (state, pony) -> {
        pony.switchRenderer(state, EntityType.SKELETON, SkeleponyRenderer::new);
        pony.switchRenderer(state, EntityType.STRAY, SkeleponyRenderer.Stray::new);
        pony.switchRenderer(state, EntityType.WITHER_SKELETON, SkeleponyRenderer.Wither::new);
    });
    public static final MobRenderers GUARDIAN = register("guardians", (state, pony) -> {
        pony.switchRenderer(state, EntityType.GUARDIAN, SeaponyRenderer::new);
        pony.switchRenderer(state, EntityType.ELDER_GUARDIAN, SeaponyRenderer.Elder::new);
    });
    public static final MobRenderers ENDERMAN = register("endermen", (state, pony) -> {
        pony.switchRenderer(state, EntityType.ENDERMAN, EnderStallionRenderer::new);
    });
    public static final MobRenderers INANIMATE = register("inanimates", (state, pony) -> {
       pony.switchRenderer(state, EntityType.ARMOR_STAND, PonyStandRenderer::new);
    });

    private final BiConsumer<Boolean, PonyRenderDispatcher> changer;

    public final String name;

    private boolean lastState;

    private MobRenderers(String name, BiConsumer<Boolean, PonyRenderDispatcher> changer) {
        this.name = name;
        this.changer = changer;
    }

    public Setting<Boolean> option() {
        return MineLittlePony.getInstance().getConfig().<Boolean>get(name);
    }

    public boolean set(boolean value) {
        value = option().set(value);
        apply(PonyRenderDispatcher.getInstance());
        return value;
    }

    public boolean get() {
        return option().get();
    }

    public static MobRenderers register(String name, BiConsumer<Boolean, PonyRenderDispatcher> changer) {
        return REGISTRY.computeIfAbsent(name, n -> new MobRenderers(name, changer));
    }

    void apply(PonyRenderDispatcher dispatcher) {
        boolean state = get();
        if (state != lastState) {
            MineLittlePony.logger.info(String.format("Ponify %s [%B] -> [%B]", name, lastState, state));
            lastState = state;
            changer.accept(state, dispatcher);
        }
    }
}