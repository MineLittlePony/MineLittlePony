package com.minelittlepony.client.render.blockentity.skull;

import com.minelittlepony.api.config.PonyConfig;
import com.minelittlepony.api.pony.Pony;
import com.minelittlepony.client.model.ModelType;
import com.minelittlepony.client.model.armour.ArmourRendererPlugin;
import com.minelittlepony.client.render.MobRenderers;
import com.minelittlepony.client.render.entity.*;
import com.minelittlepony.client.render.entity.state.PonyRenderState;
import com.minelittlepony.client.render.entity.state.PonyRenderState.EquippedHeadRenderState;

import net.minecraft.block.SkullBlock;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.entity.SkullBlockEntityModel;
import net.minecraft.client.render.block.entity.SkullBlockEntityRenderer;
import net.minecraft.client.render.entity.equipment.EquipmentModel;
import net.minecraft.client.render.entity.state.LivingEntityRenderState;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.component.type.ProfileComponent;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;
import net.minecraft.util.math.ColorHelper;
import net.minecraft.util.math.Direction;

import java.util.Map;
import java.util.function.Function;

import org.jetbrains.annotations.Nullable;

/**
 * PonySkullRenderer! It renders ponies as skulls, or something...
 */
public class PonySkullRenderer {
    public static final PonySkullRenderer INSTANCE = new PonySkullRenderer();

    private Cache cache = new Cache();

    private ISkull selectedSkull;
    private Identifier selectedSkin;

    public void reload() {
        cache = new Cache();
    }

    record Cache(Function<SkullBlock.SkullType, ISkull> skulls, Function<SkullBlock.SkullType, SkullBlockEntityModel> headModels) {
        public Cache() {
            this(
                    Util.memoize(type -> type instanceof SkullBlock.Type t ? switch (t) {
                        case SKELETON -> new MobSkull<>(SkeleponyRenderer.SKELETON, MobRenderers.SKELETON, ModelType.SKELETON, SkeleponyRenderer.State::new);
                        case WITHER_SKELETON -> new MobSkull<>(SkeleponyRenderer.WITHER, MobRenderers.SKELETON, ModelType.SKELETON, SkeleponyRenderer.State::new);
                        case ZOMBIE -> new MobSkull<>(ZomponyRenderer.ZOMBIE, MobRenderers.ZOMBIE, ModelType.ZOMBIE, PonyRenderState::new);
                        case PIGLIN -> new MobSkull<>(PonyPiglinRenderer.PIGLIN, MobRenderers.PIGLIN, ModelType.PIGLIN, PonyPiglinRenderer.State::new);
                        case PLAYER -> new PlayerPonySkull();
                        default -> null;
                    } : null),
                    Util.memoize(type -> SkullBlockEntityRenderer.getModels(MinecraftClient.getInstance().getLoadedEntityModels(), type))
            );
        }
    }

    public void renderSkull(MatrixStack matrices, VertexConsumerProvider provider, EquippedHeadRenderState headState, LivingEntityRenderState entity, float tickDelta, int light, boolean isPony) {
        SkullBlockEntityRenderer.renderSkull(null, 180, entity.headItemAnimationProgress, matrices, provider, light,
                cache.headModels().apply(headState.skullType()),
                SkullBlockEntityRenderer.getRenderLayer(headState.skullType(), headState.wearingSkullProfile())
        );
    }

    public RenderLayer getSkullRenderLayer(SkullBlock.SkullType skullType, @Nullable ProfileComponent profile, @Nullable Identifier texture) {
        selectedSkull = null;
        selectedSkin = null;

        ISkull skull = cache.skulls().apply(skullType);

        if ((texture != null && skullType != SkullBlock.Type.PLAYER) || skull == null || !skull.canRender(PonyConfig.getInstance())) {
            return null;
        }

        selectedSkull = skull;
        selectedSkin = texture == null ? skull.getSkinResource(profile) : texture;
        return RenderLayer.getEntityTranslucent(selectedSkin);
    }

    public boolean renderSkull(@Nullable Direction direction,
            float yaw, float animationProgress,
            MatrixStack stack, VertexConsumerProvider renderContext, RenderLayer layer,
            int light) {

        if (selectedSkull == null || !selectedSkull.canRender(PonyConfig.getInstance()) || !selectedSkull.bindPony(Pony.getManager().getPony(selectedSkin))) {
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
        selectedSkull.render(stack, vertices, light, OverlayTexture.DEFAULT_UV, ColorHelper.fromFloats(ArmourRendererPlugin.INSTANCE.get().getArmourAlpha(EquipmentSlot.HEAD, EquipmentModel.LayerType.HUMANOID), 1, 1, 1));

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

        void render(MatrixStack stack, VertexConsumer vertices, int light, int overlay, int color);

        boolean canRender(PonyConfig config);

        Identifier getSkinResource(@Nullable ProfileComponent profile);

        boolean bindPony(Pony pony);
    }

    public interface SkullRenderer {
        Map<SkullBlock.SkullType, SkullBlockEntityModel> getModels();
    }
}
