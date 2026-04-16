package com.minelittlepony.client.render.blockentity.skull;

import net.minecraft.block.SkullBlock;
import net.minecraft.client.render.command.OrderedRenderCommandQueue;
import net.minecraft.client.render.item.model.special.*;
import net.minecraft.client.texture.PlayerSkinCache;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.component.type.ProfileComponent;
import net.minecraft.item.ItemDisplayContext;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.Pair;

import org.jetbrains.annotations.Nullable;
import org.joml.Vector3fc;

import java.util.Optional;
import java.util.function.Consumer;

public class PonyHeadModelRenderer<T> implements SpecialModelRenderer<Pair<PonySkullRenderer.Data, T>> {

    private final SpecialModelRenderer<T> renderer;

    private final SkullBlock.SkullType kind;
    private final Optional<Identifier> textureOverride;
    private final float animationTicks;

    protected PonyHeadModelRenderer(SpecialModelRenderer<T> renderer, SkullBlock.SkullType kind, Optional<Identifier> textureOverride, float animationTicks) {
        this.renderer = renderer;
        this.kind = kind;
        this.textureOverride = textureOverride;
        this.animationTicks = animationTicks;
    }

    public static SpecialModelRenderer<?> create(SpecialModelRenderer.Unbaked unbaked, SpecialModelRenderer<?> renderer) {
        if (unbaked instanceof HeadModelRenderer.Unbaked a) {
            return renderer instanceof HeadModelRenderer r ? new PonyHeadModelRenderer<>(r, a.kind(), a.textureOverride(), a.animation()) : renderer;
        }
        return renderer instanceof PlayerHeadModelRenderer r ? new PonyHeadModelRenderer<>(r, SkullBlock.Type.PLAYER, Optional.empty(), 0F) : renderer;
    }

    @Override
    public Pair<PonySkullRenderer.Data, T> getData(ItemStack stack) {
        @Nullable
        T humanData = renderer.getData(stack);
        return new Pair<>(
                PonySkullRenderer.INSTANCE.getSkullState(kind, unwrapProfile(humanData), textureOverride.orElse(null), animationTicks),
                humanData
        );
    }

    private ProfileComponent unwrapProfile(T humanData) {
        return humanData instanceof PlayerSkinCache.Entry info ? ProfileComponent.ofStatic(info.getProfile()) : null;
    }

    @Override
    public void render(Pair<PonySkullRenderer.Data, T> data, ItemDisplayContext displayContext, MatrixStack matrices, OrderedRenderCommandQueue queue, int light, int overlay, boolean glint, int outline) {
        if (data.getLeft() == null || !data.getLeft().render(null, 180, matrices, queue, light, outline, null)) {
            renderer.render(data.getRight(), displayContext, matrices, queue, light, overlay, glint, outline);
        }
    }

    @Override
    public void collectVertices(Consumer<Vector3fc> vertices) {
        renderer.collectVertices(vertices);
    }
}
