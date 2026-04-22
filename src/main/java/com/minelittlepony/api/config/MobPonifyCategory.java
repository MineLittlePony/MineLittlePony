package com.minelittlepony.api.config;

import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;

import org.jetbrains.annotations.Nullable;

import com.minelittlepony.client.MineLittlePony;
import com.minelittlepony.client.render.MobRenderers;
import com.minelittlepony.common.util.settings.Setting;

import java.util.function.*;

/**
 * Represents the categories of mobs that can be ponified.
 * <p>
 * Mods are allowed to create their own categories by registering them or add their own mobs to an existing category.
 */
public interface MobPonifyCategory extends Predicate<Entity> {
    @Nullable
    public static MobPonifyCategory get(Identifier id) {
        return MobRenderers.REGISTRY.get(id);
    }

    public static MobPonifyCategory getOrCreate(Identifier id) {
        return MobRenderers.getOrCreate(id);
    }

    Identifier id();

    Setting<Boolean> option();

    default String name() {
        return id().getNamespace().equals(MineLittlePony.DEFAULT_NAMESPACE) ? id().getPath() : id().toDebugFileName();
    }

    MobPonifyCategory appendAction(BiConsumer<MobPonifyCategory, Context> changer);

    interface Context {
        <T extends Entity, R extends EntityRenderer<?, ?>> void registerEntityRenderer(EntityType<T> type, Predicate<? super T> condition, Function<EntityRendererProvider.Context, R> constructor);

        <P extends BlockEntity, R extends BlockEntityRenderer<?, ?>> void registerBlockRenderer(BlockEntityType<P> type, Predicate<? super P> condition, Function<BlockEntityRendererProvider.Context, R> constructor);
    }

}
