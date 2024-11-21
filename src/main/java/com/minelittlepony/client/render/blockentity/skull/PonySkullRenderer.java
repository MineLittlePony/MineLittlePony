package com.minelittlepony.client.render.blockentity.skull;

import com.minelittlepony.api.config.PonyConfig;
import com.minelittlepony.api.pony.Pony;
import com.minelittlepony.client.model.ModelType;
import com.minelittlepony.client.model.armour.ArmourRendererPlugin;
import com.minelittlepony.client.render.MobRenderers;
import com.minelittlepony.client.render.entity.*;

import net.minecraft.block.AbstractSkullBlock;
import net.minecraft.block.SkullBlock;
import net.minecraft.block.SkullBlock.SkullType;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.entity.SkullBlockEntityModel;
import net.minecraft.client.render.block.entity.SkullBlockEntityRenderer;
import net.minecraft.client.render.entity.state.LivingEntityRenderState;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.ProfileComponent;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.equipment.EquipmentModel.LayerType;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;
import net.minecraft.util.math.ColorHelper;
import net.minecraft.util.math.Direction;

import java.util.HashMap;
import java.util.Map;
import org.jetbrains.annotations.Nullable;

/**
 * PonySkullRenderer! It renders ponies as skulls, or something...
 */
public class PonySkullRenderer {
    public static final PonySkullRenderer INSTANCE = new PonySkullRenderer();

    private Map<SkullBlock.SkullType, ISkull> skulls = Map.of();
    private Map<SkullBlock.SkullType, SkullBlockEntityModel> headModels = Map.of();

    private ISkull selectedSkull;
    private Identifier selectedSkin;

    boolean isBeingWorn;
    boolean isPony;

    public void reload() {
        skulls = Util.make(new HashMap<>(), skullMap -> {
            skullMap.put(SkullBlock.Type.SKELETON, new MobSkull(SkeleponyRenderer.SKELETON, MobRenderers.SKELETON, ModelType.SKELETON));
            skullMap.put(SkullBlock.Type.WITHER_SKELETON, new MobSkull(SkeleponyRenderer.WITHER, MobRenderers.SKELETON, ModelType.SKELETON));
            skullMap.put(SkullBlock.Type.ZOMBIE, new MobSkull(ZomponyRenderer.ZOMBIE, MobRenderers.ZOMBIE, ModelType.ZOMBIE));
            skullMap.put(SkullBlock.Type.PIGLIN, new MobSkull(PonyPiglinRenderer.PIGLIN, MobRenderers.PIGLIN, ModelType.PIGLIN));
            skullMap.put(SkullBlock.Type.PLAYER, new PlayerPonySkull());
        });
        headModels = SkullBlockEntityRenderer.getModels(MinecraftClient.getInstance().getEntityModelLoader());
    }

    public void renderSkull(MatrixStack matrices, VertexConsumerProvider provider, ItemStack stack, LivingEntityRenderState entity, float tickDelta, int light, boolean isPony) {
        isBeingWorn = true;
        this.isPony = isPony;
        SkullType type = ((AbstractSkullBlock) ((BlockItem) stack.getItem()).getBlock()).getSkullType();
        SkullBlockEntityModel skullBlockEntityModel = headModels.get(type);
        RenderLayer renderLayer = SkullBlockEntityRenderer.getRenderLayer(type, stack.get(DataComponentTypes.PROFILE));
        SkullBlockEntityRenderer.renderSkull(null, 180, entity.headItemAnimationProgress, matrices, provider, light, skullBlockEntityModel, renderLayer);
        isBeingWorn = false;
        this.isPony = false;
    }

    public RenderLayer getSkullRenderLayer(SkullBlock.SkullType skullType, @Nullable ProfileComponent profile) {
        selectedSkull = null;
        selectedSkin = null;

        ISkull skull = skulls.get(skullType);

        if (skull == null || !skull.canRender(PonyConfig.getInstance())) {
            return null;
        }

        selectedSkull = skull;
        selectedSkin = skull.getSkinResource(profile);
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
        selectedSkull.render(stack, vertices, light, OverlayTexture.DEFAULT_UV, ColorHelper.fromFloats(ArmourRendererPlugin.INSTANCE.get().getArmourAlpha(EquipmentSlot.HEAD, LayerType.HUMANOID), 1, 1, 1));

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
