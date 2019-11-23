package com.minelittlepony.client.render.tileentities.skull;

import com.google.common.collect.Maps;
import com.minelittlepony.client.MineLittlePony;
import com.minelittlepony.client.render.LevitatingItemRenderer;
import com.minelittlepony.pony.IPony;
import com.minelittlepony.settings.PonyConfig;
import com.mojang.authlib.GameProfile;

import net.fabricmc.fabric.api.client.rendereregistry.v1.BlockEntityRendererRegistry;
import net.minecraft.block.AbstractSkullBlock;
import net.minecraft.block.BlockState;
import net.minecraft.block.SkullBlock;
import net.minecraft.block.WallSkullBlock;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.block.entity.SkullBlockEntity;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.entity.BlockEntityRenderDispatcher;
import net.minecraft.client.render.block.entity.SkullBlockEntityRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;
import net.minecraft.util.math.Direction;

import java.util.Map;
import javax.annotation.Nullable;

/**
 * PonySkullRenderer! It renders ponies as skulls, or something...
 */
public class PonySkullRenderer extends SkullBlockEntityRenderer {

    private static final Map<SkullBlock.SkullType, ISkull> skullMap = Util.create(Maps.newHashMap(), (skullMap) -> {
        skullMap.put(SkullBlock.Type.SKELETON, new SkeletonSkullRenderer());
        skullMap.put(SkullBlock.Type.WITHER_SKELETON, new WitherSkullRenderer());
        skullMap.put(SkullBlock.Type.ZOMBIE, new ZombieSkullRenderer());
        skullMap.put(SkullBlock.Type.PLAYER, new PlayerSkullRenderer());
    });

    /**
     * Resolves the games skull renderer to either a special pony skull renderer
     * or some other skull renderer depending on the ponyskull's state.
     */
    public static void resolve(boolean ponySkulls) {
        if (ponySkulls) {
            BlockEntityRendererRegistry.INSTANCE.register(BlockEntityType.SKULL, new PonySkullRenderer(BlockEntityRenderDispatcher.INSTANCE));
        } else {
            BlockEntityRendererRegistry.INSTANCE.register(BlockEntityType.SKULL, new SkullBlockEntityRenderer(BlockEntityRenderDispatcher.INSTANCE));
        }
    }

    public PonySkullRenderer(BlockEntityRenderDispatcher dispatcher) {
        super(dispatcher);
    }

    @Override
    public void render(SkullBlockEntity skullBlockEntity, float f, MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i, int j) {
        float poweredTicks = skullBlockEntity.getTicksPowered(f);
        BlockState state = skullBlockEntity.getCachedState();

        boolean onWalll = state.getBlock() instanceof WallSkullBlock;

        Direction direction = onWalll ? (Direction)state.get(WallSkullBlock.FACING) : null;

        float angle = 22.5F * (direction != null ? (2 + direction.getHorizontal()) * 4F : (Integer)state.get(SkullBlock.ROTATION));

        render(direction,
                angle,
                ((AbstractSkullBlock)state.getBlock()).getSkullType(),
                skullBlockEntity.getOwner(), poweredTicks,
                matrixStack, vertexConsumerProvider, i);
    }

    public static void render(@Nullable Direction direction, float angle,
            SkullBlock.SkullType skullType, @Nullable GameProfile profile, float poweredTicks,
            MatrixStack stack, VertexConsumerProvider renderContext, int lightUv) {

        ISkull skull = skullMap.get(skullType);

        if (skull == null || !skull.canRender(MineLittlePony.getInstance().getConfig())) {
            SkullBlockEntityRenderer.render(direction, angle, skullType, profile, poweredTicks, stack, renderContext, lightUv);

            return;
        }

        Identifier skin = skull.getSkinResource(profile);

        skull.bindPony(MineLittlePony.getInstance().getManager().getPony(skin));

        stack.push();

        handleRotation(stack, direction);

        stack.scale(-1, -1, 1);
        skull.preRender(LevitatingItemRenderer.usesTransparency());


        VertexConsumer vertices = renderContext.getBuffer(RenderLayer.getEntityTranslucent(skin));

        skull.render(stack, vertices, lightUv, OverlayTexture.DEFAULT_UV, 1, 1, 1, 1);

        stack.pop();
    }

    static void handleRotation(MatrixStack stack, @Nullable Direction direction) {
        if (direction == null) {
            stack.translate(0.5, 0, 0.5);
            return;
        }

        switch (direction) {
            case NORTH:
                stack.translate(0.5, 0.25, 0.74);
                break;
            case SOUTH:
                stack.translate(0.5, 0.25, 0.26);
                break;
            case WEST:
                stack.translate(0.74, 0.25, 0.5);
                break;
            case EAST:
            default:
                stack.translate(0.26, 0.25, 0.5);
                break;
        }
    }

    /**
     * A skull, just a skull.
     *
     * Implement this interface if you want to extend our behaviour, modders.
     */
    public interface ISkull {

        void preRender(boolean transparency);

        void render(MatrixStack stack, VertexConsumer vertices, int lightUv, int overlayUv, float red, float green, float blue, float alpha);

        boolean canRender(PonyConfig config);

        Identifier getSkinResource(@Nullable GameProfile profile);

        void bindPony(IPony pony);
    }
}
