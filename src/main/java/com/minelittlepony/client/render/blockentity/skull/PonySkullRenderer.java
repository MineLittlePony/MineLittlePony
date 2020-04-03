package com.minelittlepony.client.render.blockentity.skull;

import com.google.common.collect.Maps;
import com.minelittlepony.api.pony.IPony;
import com.minelittlepony.client.MineLittlePony;
import com.minelittlepony.client.render.LevitatingItemRenderer;
import com.minelittlepony.client.render.MobRenderers;
import com.minelittlepony.client.render.entity.SkeleponyRenderer;
import com.minelittlepony.client.render.entity.ZomponyRenderer;
import com.minelittlepony.mson.api.Mson;
import com.minelittlepony.settings.PonyConfig;
import com.mojang.authlib.GameProfile;

import net.minecraft.block.SkullBlock;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.client.render.OverlayTexture;
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

    private static PonySkullRenderer INSTANCE;

    public static void resolve(boolean ponySkulls) {
        Mson.getInstance().getEntityRendererRegistry().registerBlockRenderer(BlockEntityType.SKULL,
                ponySkulls ? PonySkullRenderer::new : SkullBlockEntityRenderer::new
        );
    }

    private final Map<SkullBlock.SkullType, ISkull> skullMap = Util.make(Maps.newHashMap(), (skullMap) -> {
        skullMap.put(SkullBlock.Type.SKELETON, new MobSkull(SkeleponyRenderer.SKELETON, MobRenderers.SKELETON));
        skullMap.put(SkullBlock.Type.WITHER_SKELETON, new MobSkull(SkeleponyRenderer.WITHER, MobRenderers.SKELETON));
        skullMap.put(SkullBlock.Type.ZOMBIE, new MobSkull(ZomponyRenderer.ZOMBIE, MobRenderers.ZOMBIE));
        skullMap.put(SkullBlock.Type.PLAYER, new PonySkull());
    });

    public PonySkullRenderer(BlockEntityRenderDispatcher dispatcher) {
        super(dispatcher);

        INSTANCE = this;
    }

    public static boolean renderPonySkull(@Nullable Direction direction, float angle,
            SkullBlock.SkullType skullType, @Nullable GameProfile profile, float poweredTicks,
            MatrixStack stack, VertexConsumerProvider renderContext, int lightUv) {
        if (INSTANCE != null) {
            return INSTANCE.renderSkull(direction, angle, skullType, profile, poweredTicks, stack, renderContext, lightUv);
        }
        return false;
    }

    boolean renderSkull(@Nullable Direction direction, float angle,
            SkullBlock.SkullType skullType, @Nullable GameProfile profile, float poweredTicks,
            MatrixStack stack, VertexConsumerProvider renderContext, int lightUv) {

        ISkull skull = skullMap.get(skullType);

        if (skull == null || !skull.canRender(MineLittlePony.getInstance().getConfig())) {
            return false;
        }

        Identifier skin = skull.getSkinResource(profile);

        skull.bindPony(MineLittlePony.getInstance().getManager().getPony(skin));

        stack.push();

        handleRotation(stack, direction);

        stack.scale(-1, -1, 1);

        VertexConsumer vertices = renderContext.getBuffer(LevitatingItemRenderer.getRenderLayer(skin));

        skull.setAngles(angle, poweredTicks);
        skull.render(stack, vertices, lightUv, OverlayTexture.DEFAULT_UV, 1, 1, 1, 1);

        stack.pop();

        return true;
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

        void setAngles(float angle, float poweredTicks);

        void render(MatrixStack stack, VertexConsumer vertices, int lightUv, int overlayUv, float red, float green, float blue, float alpha);

        boolean canRender(PonyConfig config);

        Identifier getSkinResource(@Nullable GameProfile profile);

        void bindPony(IPony pony);
    }
}
