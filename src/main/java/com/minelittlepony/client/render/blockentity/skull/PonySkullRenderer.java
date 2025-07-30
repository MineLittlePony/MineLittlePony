package com.minelittlepony.client.render.blockentity.skull;

import com.minelittlepony.api.config.PonyConfig;
import com.minelittlepony.api.pony.Pony;
import com.minelittlepony.client.model.ModelType;
import com.minelittlepony.client.model.armour.ArmourRendererPlugin;
import com.minelittlepony.client.render.MobRenderers;
import com.minelittlepony.client.render.entity.*;
import com.minelittlepony.client.render.entity.state.PonyRenderState;

import net.minecraft.block.SkullBlock;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.equipment.EquipmentModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.component.type.ProfileComponent;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;
import net.minecraft.util.math.ColorHelper;
import net.minecraft.util.math.Direction;

import java.util.function.Function;

import org.jetbrains.annotations.Nullable;

/**
 * PonySkullRenderer! It renders ponies as skulls, or something...
 */
public class PonySkullRenderer {
    public static final PonySkullRenderer INSTANCE = new PonySkullRenderer();

    private Function<SkullBlock.SkullType, ISkull> skulls;

    private PonySkullRenderer() {
        reload();
    }

    public void reload() {
        skulls = Util.memoize(type -> type instanceof SkullBlock.Type t ? switch (t) {
            case SKELETON -> new MobSkull<>(SkeleponyRenderer.SKELETON, MobRenderers.SKELETON, ModelType.SKELETON, SkeleponyRenderer.State::new);
            case WITHER_SKELETON -> new MobSkull<>(SkeleponyRenderer.WITHER, MobRenderers.SKELETON, ModelType.SKELETON, SkeleponyRenderer.State::new);
            case ZOMBIE -> new MobSkull<>(ZomponyRenderer.ZOMBIE, MobRenderers.ZOMBIE, ModelType.ZOMBIE, PonyRenderState::new);
            case PIGLIN -> new MobSkull<>(PonyPiglinRenderer.PIGLIN, MobRenderers.PIGLIN, ModelType.PIGLIN, PonyPiglinRenderer.State::new);
            case PLAYER -> new PlayerPonySkull();
            default -> null;
        } : null);
    }

    @Nullable
    public Data getSkullState(SkullBlock.SkullType skullType, @Nullable ProfileComponent profile) {
        @Nullable
        ISkull skull = skulls.apply(skullType);

        if (skull == null) {
            return null;
        }

        Identifier texture = skull.getSkinResource(profile);
        return new Data(skull, RenderLayer.getEntityTranslucent(texture), Pony.getManager().getPony(texture));
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

    public interface Proxy {
        void setPonySkullData(Data data);
    }

    public record Data(ISkull model, RenderLayer layer, Pony pony) {
        public boolean render(@Nullable Direction direction, float yaw, float animationProgress, MatrixStack matrices, VertexConsumerProvider vertices, int light) {
            if (!model.canRender(PonyConfig.getInstance()) || !model.bindPony(pony)) {
                return false;
            }

            matrices.push();

            if (direction == null) {
                matrices.translate(0.5, 0, 0.5);
            } else {
                final float offset = 0.25F;
                matrices.translate(
                        0.5F - direction.getOffsetX() * offset,
                        offset,
                        0.5F - direction.getOffsetZ() * offset
                );
            }
            matrices.scale(-1, -1, 1);

            model.setAngles(yaw, animationProgress);
            model.render(matrices, vertices.getBuffer(layer), light, OverlayTexture.DEFAULT_UV, ColorHelper.fromFloats(ArmourRendererPlugin.INSTANCE.get().getArmourAlpha(EquipmentSlot.HEAD, EquipmentModel.LayerType.HUMANOID), 1, 1, 1));

            matrices.pop();

            return true;
        }
    }
}
