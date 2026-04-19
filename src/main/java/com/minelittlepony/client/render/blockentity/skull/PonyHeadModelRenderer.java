package com.minelittlepony.client.render.blockentity.skull;

import net.minecraft.client.renderer.PlayerSkinRenderCache;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.special.*;
import net.minecraft.resources.Identifier;
import net.minecraft.util.Tuple;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.ResolvableProfile;
import net.minecraft.world.level.block.SkullBlock;

import org.jetbrains.annotations.Nullable;
import org.joml.Vector3fc;

import com.minelittlepony.client.render.CopperPonyBlockEntityRenderer;
import com.mojang.blaze3d.vertex.PoseStack;

import java.util.Optional;
import java.util.function.Consumer;

public class PonyHeadModelRenderer<T> implements SpecialModelRenderer<Tuple<PonySkullRenderer.Data<?>, T>> {
    private final SpecialModelRenderer<T> renderer;

    private final SkullBlock.Type kind;
    private final Optional<Identifier> textureOverride;
    private final float animationTicks;

    protected PonyHeadModelRenderer(SpecialModelRenderer<T> renderer, SkullBlock.Type kind, Optional<Identifier> textureOverride, float animationTicks) {
        this.renderer = renderer;
        this.kind = kind;
        this.textureOverride = textureOverride;
        this.animationTicks = animationTicks;
    }

    public static SpecialModelRenderer<?> create(SpecialModelRenderer.Unbaked<?> unbaked, SpecialModelRenderer<?> renderer) {
        if (unbaked instanceof CopperGolemStatueSpecialRenderer.Unbaked a && renderer instanceof CopperGolemStatueSpecialRenderer r) {
            return new CopperPonyBlockEntityRenderer.CopperPonyModelRenderer<>(r, a.texture(), a.pose());
        }
        if (unbaked instanceof SkullSpecialRenderer.Unbaked a && renderer instanceof SkullSpecialRenderer r) {
            return new PonyHeadModelRenderer<>(r, a.kind(), a.textureOverride(), a.animation());
        }
        if (renderer instanceof PlayerHeadSpecialRenderer r) {
            return new PonyHeadModelRenderer<>(r, SkullBlock.Types.PLAYER, Optional.empty(), 0F);
        }
        return renderer;
    }

    @Override
    public Tuple<PonySkullRenderer.Data<?>, T> extractArgument(ItemStack stack) {
        @Nullable
        T humanData = renderer.extractArgument(stack);
        return new Tuple<>(
                PonySkullRenderer.INSTANCE.getSkullState(kind, unwrapProfile(humanData), textureOverride.orElse(null), animationTicks),
                humanData
        );
    }

    private ResolvableProfile unwrapProfile(T humanData) {
        return humanData instanceof PlayerSkinRenderCache.RenderInfo info ? ResolvableProfile.createResolved(info.gameProfile()) : null;
    }

    @Override
    public void submit(Tuple<PonySkullRenderer.Data<?>, T> data, PoseStack matrices, SubmitNodeCollector frame, int light, int overlay, boolean glint, int outline) {
        if (data.getA() == null || !data.getA().submit(matrices, frame, light, outline, null)) {
            renderer.submit(data.getB(), matrices, frame, light, overlay, glint, outline);
        }
    }

    @Override
    public void getExtents(Consumer<Vector3fc> vertices) {
        renderer.getExtents(vertices);
    }
}
