package com.minelittlepony.client.render.entity.feature;

import com.minelittlepony.api.model.BodyPart;
import com.minelittlepony.api.model.armour.ArmourRendererPlugin;
import com.minelittlepony.client.model.ClientPonyModel;
import com.minelittlepony.client.render.PonyRenderContext;
import com.minelittlepony.client.render.blockentity.skull.PonySkullRenderer;
import com.minelittlepony.client.render.entity.state.PonyRenderState;
import com.minelittlepony.client.render.entity.state.PonyRenderState.EquippedHeadRenderState;
import com.mojang.blaze3d.vertex.PoseStack;

import java.util.function.Function;

import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.model.object.skull.SkullModelBase;
import net.minecraft.client.renderer.PlayerSkinRenderCache;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.blockentity.SkullBlockRenderer;
import net.minecraft.client.renderer.entity.layers.CustomHeadLayer;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.resources.model.EquipmentClientInfo;
import net.minecraft.util.Util;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.component.ResolvableProfile;
import net.minecraft.world.level.block.SkullBlock;

public class SkullFeature<
        S extends PonyRenderState,
        M extends ClientPonyModel<S>
    > extends AbstractPonyFeature<S, M> {

    private final CustomHeadLayer.Transforms headTransformation;
    private final Function<SkullBlock.Type, SkullModelBase> headModels;

    private final PlayerSkinRenderCache skinCache;

    public SkullFeature(PonyRenderContext<?, S, M> context, PlayerSkinRenderCache skinCache, EntityModelSet models, CustomHeadLayer.Transforms headTransformation, boolean scaleForChild) {
        super(context);
        this.skinCache = skinCache;
        this.headTransformation = headTransformation;
        this.headModels = Util.memoize(type -> SkullBlockRenderer.createModel(models, type));
    }

    @Override
    public void submit(PoseStack matrices, SubmitNodeCollector frame, int light, S state, float limbAngle, float limbDistance) {
        for (EquippedHeadRenderState headState : state.equippedHeads) {
            matrices.pushPose();
            matrices.scale(headTransformation.horizontalScale(), 1, headTransformation.horizontalScale());

            M model = lookupModel(state).body();

            model.transform(state, BodyPart.HEAD, matrices);
            model.getHead().translateAndRotate(matrices);

            float f = 1.1F;
            matrices.scale(f, f, f);

            if (headState.skullType() != null) {
                float n = 1.1875F;
                matrices.translate(0, headTransformation.skullYOffset(), 0);
                matrices.scale(n, n, n);
                matrices.translate(0, 0.1F, -0.1F);
                PonySkullRenderer.INSTANCE.pushState(PonySkullRenderer.INSTANCE.getSkullState(state.wornHeadType, state.wornHeadProfile, null, state.wornHeadAnimationPos));
                SkullBlockRenderer.submitSkull(state.wornHeadAnimationPos, matrices, frame, light,
                        headModels.apply(headState.skullType()),
                        getRenderLayer(headState),
                        state.outlineColor,
                        null
                );
                PonySkullRenderer.INSTANCE.popState();
            } else {
                matrices.translate(0, 0.1F, -0.1F);
                CustomHeadLayer.translateToHead(matrices, headTransformation);
                headState.item().submit(matrices, frame, light, OverlayTexture.NO_OVERLAY, state.outlineColor);
            }

            matrices.popPose();
        }

        ArmourRendererPlugin.INSTANCE.get().onArmourRendered(state, matrices, frame, EquipmentSlot.BODY, EquipmentClientInfo.LayerType.HUMANOID, ArmourRendererPlugin.ArmourType.SKULL);
    }

    private RenderType getRenderLayer(EquippedHeadRenderState state) {
        if (state.skullType() == SkullBlock.Types.PLAYER) {
            ResolvableProfile profileComponent = state.wearingSkullProfile();
            if (profileComponent != null) {
                return skinCache.getOrDefault(profileComponent).renderType();
            }
        }

        return SkullBlockRenderer.getSkullRenderType(state.skullType(), null);
    }
}
