package com.minelittlepony.client.render.blockentity.skull;

import com.google.common.collect.Maps;
import com.minelittlepony.api.pony.IPony;
import com.minelittlepony.client.MineLittlePony;
import com.minelittlepony.client.model.ModelType;
import com.minelittlepony.client.render.LevitatingItemRenderer;
import com.minelittlepony.client.render.MobRenderers;
import com.minelittlepony.client.render.entity.SkeleponyRenderer;
import com.minelittlepony.client.render.entity.ZomponyRenderer;
import com.minelittlepony.settings.PonyConfig;
import com.mojang.authlib.GameProfile;

import net.minecraft.block.SkullBlock;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Direction;

import java.util.Map;
import org.jetbrains.annotations.Nullable;

/**
 * PonySkullRenderer! It renders ponies as skulls, or something...
 */
public class PonySkullRenderer {

    private static final Map<SkullBlock.SkullType, ISkull> SKULLS = Maps.newHashMap();

    private static ISkull selectedSkull;
    private static Identifier selectedSkin;

    public static void reload() {
        SKULLS.clear();
        loadSkulls(SKULLS);
    }

    private static void loadSkulls(Map<SkullBlock.SkullType, ISkull> skullMap) {
        skullMap.put(SkullBlock.Type.SKELETON, new MobSkull(SkeleponyRenderer.SKELETON, MobRenderers.SKELETON, ModelType.SKELETON));
        skullMap.put(SkullBlock.Type.WITHER_SKELETON, new MobSkull(SkeleponyRenderer.WITHER, MobRenderers.SKELETON, ModelType.ENDERMAN));
        skullMap.put(SkullBlock.Type.ZOMBIE, new MobSkull(ZomponyRenderer.ZOMBIE, MobRenderers.ZOMBIE, ModelType.ZOMBIE));
        skullMap.put(SkullBlock.Type.PLAYER, new PlayerPonySkull());
    }

    public static RenderLayer getSkullRenderLayer(SkullBlock.SkullType skullType, @Nullable GameProfile profile) {
        selectedSkull = null;
        selectedSkin = null;

        ISkull skull = SKULLS.get(skullType);

        if (skull == null || !skull.canRender(MineLittlePony.getInstance().getConfig())) {
            return null;
        }

        selectedSkull = skull;
        selectedSkin = skull.getSkinResource(profile);
        return LevitatingItemRenderer.getRenderLayer(selectedSkin);
    }

    public static boolean renderSkull(@Nullable Direction direction,
            float yaw, float animationProgress,
            MatrixStack stack, VertexConsumerProvider renderContext, RenderLayer layer,
            int lightUv) {

        if (selectedSkull == null || !selectedSkull.canRender(MineLittlePony.getInstance().getConfig())) {
            return false;
        }

        if (!selectedSkull.bindPony(MineLittlePony.getInstance().getManager().getPony(selectedSkin))) {
            return false;
        }

        stack.push();

        if (direction == null) {
            stack.translate(0.5, 0, 0.5);
        } else {

            final float offset = 0.25F;
            stack.translate(
                    0.5F - direction.getOffsetX() * offset,
                    offset,
                    0.5F - direction.getOffsetZ() * offset
            );
        }
        stack.scale(-1, -1, 1);

        VertexConsumer vertices = renderContext.getBuffer(layer);

        selectedSkull.setAngles(yaw, animationProgress);
        selectedSkull.render(stack, vertices, lightUv, OverlayTexture.DEFAULT_UV, 1, 1, 1, 1);

        stack.pop();

        return true;
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

        boolean bindPony(IPony pony);
    }
}
