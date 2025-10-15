package com.minelittlepony.client.render;

import com.minelittlepony.api.config.PonyCommandTags;
import com.minelittlepony.api.config.PonyConfig;
import com.minelittlepony.client.render.entity.*;
import com.minelittlepony.client.render.entity.npc.*;
import com.minelittlepony.client.render.entity.state.PonifiedRenderState;
import com.minelittlepony.common.util.settings.Setting;
import com.minelittlepony.mson.api.EntityRendererRegistry;

import java.util.HashMap;
import java.util.Map;
import java.util.function.*;

import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;

/**
 * Central location where new entity renderers are registered and applied.
 */
public record MobRenderers (String name, BiConsumer<MobRenderers, EntityRendererRegistry> changer) implements Predicate<Entity> {
    public static final Map<String, MobRenderers> REGISTRY = new HashMap<>();

    public static MobRenderers register(String name, BiConsumer<MobRenderers, Registry> changer) {
        return REGISTRY.computeIfAbsent(name, n -> new MobRenderers(name, (state, registry) -> {
            changer.accept(state, new Registry() {
                @Override
                public <T extends Entity, R extends EntityRenderer<?, ?>> void registerEntityRenderer(EntityType<T> type, Predicate<? super T> condition, Function<EntityRendererFactory.Context, R> constructor) {
                    registry.registerEntityRenderer(type, condition, constructor);
                    registry.registerEntityStateRenderer(type, s -> s.entityType == type && s instanceof PonifiedRenderState, constructor);
                }
            });
        }));
    }

    interface Registry {
        <T extends Entity, R extends EntityRenderer<?, ?>> void registerEntityRenderer(EntityType<T> type, Predicate<? super T> condition, Function<EntityRendererFactory.Context, R> constructor);
    }

    public static final MobRenderers VILLAGER = register("villagers", (state, registry) -> {
        registry.registerEntityRenderer(EntityType.VILLAGER, state, VillagerPonyRenderer::new);
        registry.registerEntityRenderer(EntityType.WITCH, state, WitchRenderer::new);
        registry.registerEntityRenderer(EntityType.ZOMBIE_VILLAGER, state, ZomponyVillagerRenderer::new);
        registry.registerEntityRenderer(EntityType.WANDERING_TRADER, state, TraderRenderer::new);
    });
    public static final MobRenderers ILLAGER = register("illagers", (state, registry) -> {
        registry.registerEntityRenderer(EntityType.VEX, state, VexRenderer::new);
        registry.registerEntityRenderer(EntityType.EVOKER, state, IllagerPonyRenderer::evoker);
        registry.registerEntityRenderer(EntityType.VINDICATOR, state, IllagerPonyRenderer::vindicator);
        registry.registerEntityRenderer(EntityType.ILLUSIONER, state, IllusionistPonyRenderer::new);
        registry.registerEntityRenderer(EntityType.PILLAGER, state, IllagerPonyRenderer::pillager);
    });
    public static final MobRenderers ZOMBIE = register("zombies", (state, registry) -> {
        registry.registerEntityRenderer(EntityType.ZOMBIE, state, ZomponyRenderer::zombie);
        registry.registerEntityRenderer(EntityType.HUSK, state, ZomponyRenderer::husk);
        registry.registerEntityRenderer(EntityType.GIANT, state, ZomponyRenderer::giant);
        registry.registerEntityRenderer(EntityType.DROWNED, state, ZomponyRenderer::drowned);
    });
    public static final MobRenderers PIGLIN = register("pigzombies", (state, registry) -> {
        registry.registerEntityRenderer(EntityType.PIGLIN, state, PonyPiglinRenderer::piglin);
        registry.registerEntityRenderer(EntityType.PIGLIN_BRUTE, state, PonyPiglinRenderer::brute);
        registry.registerEntityRenderer(EntityType.ZOMBIFIED_PIGLIN, state, PonyPiglinRenderer::zombified);
        registry.registerEntityRenderer(EntityType.PIG, entity -> state.option().get() && !PonyConfig.getInstance().noFun.get(), PonyPigRenderer::new);
    });
    public static final MobRenderers SKELETON = register("skeletons", (state, registry) -> {
        registry.registerEntityRenderer(EntityType.SKELETON, state, SkeleponyRenderer::skeleton);
        registry.registerEntityRenderer(EntityType.STRAY, state, SkeleponyRenderer::stray);
        registry.registerEntityRenderer(EntityType.BOGGED, state, SkeleponyRenderer::bogged);
        registry.registerEntityRenderer(EntityType.WITHER_SKELETON, state, SkeleponyRenderer::wither);
    });
    public static final MobRenderers GUARDIAN = register("guardians", (state, registry) -> {
        registry.registerEntityRenderer(EntityType.GUARDIAN, state, SeaponyRenderer::guardian);
        registry.registerEntityRenderer(EntityType.ELDER_GUARDIAN, state, SeaponyRenderer::elder);
    });
    public static final MobRenderers ENDERMAN = register("endermen", (state, registry) -> {
        registry.registerEntityRenderer(EntityType.ENDERMAN, state, EnderStallionRenderer::new);
    });
    public static final MobRenderers INANIMATE = register("inanimates", (state, registry) -> {
       registry.registerEntityRenderer(EntityType.ARMOR_STAND, e -> state.option().get() && PonyStandRenderer.isPonyStand(e), PonyStandRenderer::new);
    });
    public static final MobRenderers STRIDER = register("striders", (state, registry) -> {
        registry.registerEntityRenderer(EntityType.STRIDER, state, StriderRenderer::new);
    });
    public static final MobRenderers ALLAY = register("allays", (state, registry) -> {
        registry.registerEntityRenderer(EntityType.ALLAY, state, AllayRenderer::new);
    });
    public static final MobRenderers MANNEQUINE = register("mannequine", (state, registry) -> {

    });

    public Setting<Boolean> option() {
        return PonyConfig.getInstance().getCategory("entities").<Boolean>get(name);
    }

    @Override
    public boolean test(Entity entity) {
        return PonyCommandTags.isAPony(entity, option().get());
    }
}