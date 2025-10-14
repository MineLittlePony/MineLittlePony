package com.minelittlepony.client.render.entity.feature;

import com.minelittlepony.api.model.BodyPart;
import com.minelittlepony.client.model.ClientPonyModel;
import com.minelittlepony.client.model.armour.ArmourRendererPlugin;
import com.minelittlepony.client.render.PonyRenderContext;
import com.minelittlepony.client.render.entity.state.PonyRenderState;
import com.minelittlepony.client.render.entity.state.PonyRenderState.EquippedHeadRenderState;

import java.util.function.Function;

import net.minecraft.block.SkullBlock;
import net.minecraft.client.render.*;
import net.minecraft.client.render.block.entity.SkullBlockEntityModel;
import net.minecraft.client.render.block.entity.SkullBlockEntityRenderer;
import net.minecraft.client.render.command.OrderedRenderCommandQueue;
import net.minecraft.client.render.entity.equipment.EquipmentModel;
import net.minecraft.client.render.entity.feature.HeadFeatureRenderer;
import net.minecraft.client.render.entity.model.LoadedEntityModels;
import net.minecraft.client.texture.PlayerSkinCache;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.component.type.ProfileComponent;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.util.Util;

public class SkullFeature<
        S extends PonyRenderState,
        M extends ClientPonyModel<S>
    > extends AbstractPonyFeature<S, M> {

    private final HeadFeatureRenderer.HeadTransformation headTransformation;
    private final Function<SkullBlock.SkullType, SkullBlockEntityModel> headModels;

    private final PlayerSkinCache skinCache;

    public SkullFeature(PonyRenderContext<?, S, M> context, PlayerSkinCache skinCache, LoadedEntityModels models, HeadFeatureRenderer.HeadTransformation headTransformation, boolean scaleForChild) {
        super(context);
        this.skinCache = skinCache;
        this.headTransformation = headTransformation;
        this.headModels = Util.memoize(type -> SkullBlockEntityRenderer.getModels(models, type));
    }

    @Override
    public void render(MatrixStack matrices, OrderedRenderCommandQueue queue, int light, S state, float limbAngle, float limbDistance) {
        for (EquippedHeadRenderState headState : state.equippedHeads) {
            matrices.push();

            M model = getModelWrapper().body();

            model.transform(state, BodyPart.HEAD, matrices);
            model.getHead().applyTransform(matrices);

            float f = 1.1F;
            matrices.scale(f, f, f);

            if (headState.skullType() != null) {
                float n = 1.1875F;
                matrices.scale(n, -n, -n);
                matrices.translate(0, -0.1F, 0.1F);
                matrices.translate(-0.5, 0, -0.5);
                SkullBlockEntityRenderer.render(null, 180, state.headItemAnimationProgress, matrices, queue, light,
                        headModels.apply(headState.skullType()),
                        getRenderLayer(headState),
                        state.outlineColor,
                        null
                );
            } else {
                matrices.translate(0, 0.1F, -0.1F);
                HeadFeatureRenderer.translate(matrices, headTransformation);
                headState.item().render(matrices, queue, light, OverlayTexture.DEFAULT_UV, state.outlineColor);
            }

            matrices.pop();
        }

        ArmourRendererPlugin.INSTANCE.get().onArmourRendered(state, matrices, queue, EquipmentSlot.BODY, EquipmentModel.LayerType.HUMANOID, ArmourRendererPlugin.ArmourType.SKULL);
    }

    private RenderLayer getRenderLayer(EquippedHeadRenderState state) {
        if (state.skullType() == SkullBlock.Type.PLAYER) {
            ProfileComponent profileComponent = state.wearingSkullProfile();
            if (profileComponent != null) {
                return skinCache.get(profileComponent).getRenderLayer();
            }
        }

        return SkullBlockEntityRenderer.getCutoutRenderLayer(state.skullType(), null);
    }
}
