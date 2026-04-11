package com.minelittlepony.client.render.blockentity.skull;

import net.minecraft.client.renderer.PlayerSkinRenderCache;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.special.*;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.ResolvableProfile;
import net.minecraft.world.level.block.SkullBlock;

import org.jetbrains.annotations.Nullable;
import org.joml.Vector3fc;

import com.minelittlepony.api.config.PonyConfig;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.datafixers.util.Either;

import java.util.Optional;
import java.util.function.Consumer;

public class PonyHeadModelRenderer implements SpecialModelRenderer<Either<PonySkullRenderer.Data, Object>> {

    private final SpecialModelRenderer<?> renderer;

    private final SkullBlock.Type kind;
    private final Optional<Identifier> textureOverride;
    private final float animationTicks;

    public PonyHeadModelRenderer(SpecialModelRenderer<?> renderer, SkullBlock.Type kind, Optional<Identifier> textureOverride, float animationTicks) {
        this.renderer = renderer;
        this.kind = kind;
        this.textureOverride = textureOverride;
        this.animationTicks = animationTicks;
    }

    @Override
    public Either<PonySkullRenderer.Data, Object> extractArgument(ItemStack stack) {
        @Nullable
        Object humanData = renderer.extractArgument(stack);
        @Nullable
        PonySkullRenderer.Data data = PonySkullRenderer.INSTANCE.getSkullState(kind, unwrapProfile(humanData), textureOverride.orElse(null), animationTicks);
        if (data != null && data.model().canRender(PonyConfig.getInstance())) {
            return Either.left(data);
        }
        return Either.right(humanData);
    }

    private ResolvableProfile unwrapProfile(Object humanData) {
        return humanData instanceof PlayerSkinRenderCache.RenderInfo info ? ResolvableProfile.createResolved(info.gameProfile()) : null;
    }

    @Override
    public void submit(Either<PonySkullRenderer.Data, Object> data, PoseStack matrices, SubmitNodeCollector frame, int light, int overlay, boolean glint, int outline) {
        data
            .ifLeft(ponyModel -> ponyModel.render(matrices, frame, light, outline, null))
            .ifRight(_ -> renderer.submit(null, matrices, frame, light, overlay, glint, outline));
    }

    @Override
    public void getExtents(Consumer<Vector3fc> vertices) {
        renderer.getExtents(vertices);
    }
}
