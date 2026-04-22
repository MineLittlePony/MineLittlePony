package com.minelittlepony.client.render;

import com.minelittlepony.api.config.*;

import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.block.entity.BlockEntityType;

import com.minelittlepony.client.MineLittlePony;
import com.minelittlepony.client.render.entity.*;
import com.minelittlepony.client.render.entity.npc.*;
import com.minelittlepony.common.util.settings.Setting;

import java.util.*;
import java.util.function.*;

/**
 * Central location where new entity renderers are registered and applied.
 */
public record MobRenderers (Identifier id, Set<BiConsumer<MobPonifyCategory, Context>> changers) implements MobPonifyCategory {
    public static final Map<Identifier, MobRenderers> REGISTRY = new HashMap<>();

    public static MobPonifyCategory getOrCreate(Identifier id) {
        return REGISTRY.computeIfAbsent(id, _ -> new MobRenderers(id, new HashSet<>()));
    }

    public void apply(Context context) {
        changers.forEach(change -> change.accept(this, context));
    }

    public static MobPonifyCategory register(String name, BiConsumer<MobPonifyCategory, Context> changer) {
        return getOrCreate(MineLittlePony.id(name)).appendAction(changer);
    }

    public static final MobPonifyCategory VILLAGER = register("villagers", (state, registry) -> {
        registry.registerEntityRenderer(EntityType.VILLAGER, state, VillagerPonyRenderer::new);
        registry.registerEntityRenderer(EntityType.WITCH, state, WitchRenderer::new);
        registry.registerEntityRenderer(EntityType.ZOMBIE_VILLAGER, state, ZomponyVillagerRenderer::new);
        registry.registerEntityRenderer(EntityType.WANDERING_TRADER, state, TraderRenderer::new);
    });
    public static final MobPonifyCategory ILLAGER = register("illagers", (state, registry) -> {
        registry.registerEntityRenderer(EntityType.VEX, state, VexRenderer::new);
        registry.registerEntityRenderer(EntityType.EVOKER, state, IllagerPonyRenderer::evoker);
        registry.registerEntityRenderer(EntityType.VINDICATOR, state, IllagerPonyRenderer::vindicator);
        registry.registerEntityRenderer(EntityType.ILLUSIONER, state, IllusionistPonyRenderer::new);
        registry.registerEntityRenderer(EntityType.PILLAGER, state, IllagerPonyRenderer::pillager);
    });
    public static final MobPonifyCategory ZOMBIE = register("zombies", (state, registry) -> {
        registry.registerEntityRenderer(EntityType.ZOMBIE, state, ZomponyRenderer::zombie);
        registry.registerEntityRenderer(EntityType.HUSK, state, ZomponyRenderer::husk);
        registry.registerEntityRenderer(EntityType.GIANT, state, ZomponyRenderer::giant);
        registry.registerEntityRenderer(EntityType.DROWNED, state, ZomponyRenderer::drowned);
    });
    public static final MobPonifyCategory PIGLIN = register("pigzombies", (state, registry) -> {
        registry.registerEntityRenderer(EntityType.PIGLIN, state, PonyPiglinRenderer::piglin);
        registry.registerEntityRenderer(EntityType.PIGLIN_BRUTE, state, PonyPiglinRenderer::brute);
        registry.registerEntityRenderer(EntityType.ZOMBIFIED_PIGLIN, state, PonyPiglinRenderer::zombified);
        registry.registerEntityRenderer(EntityType.PIG, _ -> state.option().get() && !PonyConfig.getInstance().noFun.get(), PonyPigRenderer::new);
    });
    public static final MobPonifyCategory SKELETON = register("skeletons", (state, registry) -> {
        registry.registerEntityRenderer(EntityType.SKELETON, state, SkeleponyRenderer::skeleton);
        registry.registerEntityRenderer(EntityType.STRAY, state, SkeleponyRenderer::stray);
        registry.registerEntityRenderer(EntityType.BOGGED, state, SkeleponyRenderer::bogged);
        registry.registerEntityRenderer(EntityType.PARCHED, state, SkeleponyRenderer::parched);
        registry.registerEntityRenderer(EntityType.WITHER_SKELETON, state, SkeleponyRenderer::wither);
    });
    public static final MobPonifyCategory GUARDIAN = register("guardians", (state, registry) -> {
        registry.registerEntityRenderer(EntityType.GUARDIAN, state, SeaponyRenderer::guardian);
        registry.registerEntityRenderer(EntityType.ELDER_GUARDIAN, state, SeaponyRenderer::elder);
    });
    public static final MobPonifyCategory ENDERMAN = register("endermen", (state, registry) -> {
        registry.registerEntityRenderer(EntityType.ENDERMAN, state, EnderStallionRenderer::new);
    });
    public static final MobPonifyCategory INANIMATE = register("inanimates", (state, registry) -> {
       registry.registerEntityRenderer(EntityType.ARMOR_STAND, e -> state.option().get() && PonyStandRenderer.isPonyStand(e), PonyStandRenderer::new);
    });
    public static final MobPonifyCategory STRIDER = register("striders", (state, registry) -> {
        registry.registerEntityRenderer(EntityType.STRIDER, state, StriderRenderer::new);
    });
    public static final MobPonifyCategory ALLAY = register("allays", (state, registry) -> {
        registry.registerEntityRenderer(EntityType.ALLAY, state, AllayRenderer::new);
    });
    public static final MobPonifyCategory COPPER_GOLEMS = register("copper_golems", (state, registry) -> {
        registry.registerEntityRenderer(EntityType.COPPER_GOLEM, state, CopperPonyRenderer::new);
        registry.registerBlockRenderer(BlockEntityType.COPPER_GOLEM_STATUE, _ -> state.option().get(), CopperPonyBlockEntityRenderer::new);
    });
    public static final MobPonifyCategory MANNEQUINE = register("mannequines", (_, _) -> {

    });

    @Override
    public Setting<Boolean> option() {
        return PonyConfig.getInstance().getCategory("entities").<Boolean>get(name());
    }

    @Override
    public boolean test(Entity entity) {
        return PonyDisplayTags.of(entity).shouldPonify(option().get());
    }

    public MobPonifyCategory appendAction(BiConsumer<MobPonifyCategory, Context> changer) {
        changers.add(changer);
        return this;
    }
}