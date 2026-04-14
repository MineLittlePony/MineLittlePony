package com.minelittlepony.client.model;

import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.object.skull.SkullModel;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.resources.Identifier;
import net.minecraft.util.ARGB;
import net.minecraft.util.CommonColors;

import com.minelittlepony.api.model.BodyPart;
import com.minelittlepony.api.model.PonyModel;
import com.minelittlepony.api.model.gear.Gear;
import com.minelittlepony.api.pony.meta.Wearable;
import com.minelittlepony.client.render.entity.state.PlayerPonyRenderState;
import com.minelittlepony.client.render.entity.state.PonyRenderState;
import com.mojang.blaze3d.vertex.PoseStack;

public class DJPon3EarsModel extends SkullModel implements Gear<PonyRenderState> {
    public static final float DEFAULT_SCALE = 1.3333334F;

    public DJPon3EarsModel(ModelPart tree) {
        super(tree);
    }

    public void setVisible(boolean show) {
        head.visible = show;
    }

    @Override
    public boolean canRender(PonyModel<?> model, PonyRenderState entity) {
        return entity.isWearing(Wearable.MOUSE_EARS);
    }

    @Override
    public BodyPart getGearLocation() {
        return BodyPart.HEAD;
    }

    @Override
    public Identifier getTexture(PonyRenderState state, Context<PonyRenderState, ?> context) {
        if (state instanceof PlayerPonyRenderState player) {
            if (player.wearabledTextures.containsKey(Wearable.MOUSE_EARS)) {
                return player.wearabledTextures.get(Wearable.MOUSE_EARS);
            }
        }

        return Wearable.MOUSE_EARS.getDefaultTexture();
    }

    @Override
    public void render(PoseStack stack, GearRenderState<PonyRenderState> state, SubmitNodeCollector frame, RenderType renderType, int overlay, int light, int color) {
        if (!(state.entityState instanceof PlayerPonyRenderState player && player.wearabledTextures.containsKey(Wearable.MOUSE_EARS))) {
            color = state.entityState.glowColor;
            color = color == 0 ? CommonColors.WHITE : ARGB.opaque(state.entityState.glowColor);
        }

        stack.pushPose();
        stack.scale(DEFAULT_SCALE, DEFAULT_SCALE, DEFAULT_SCALE);
        stack.translate(0, 0.15F, 0);
        frame.submitModelPart(root(), stack, renderType, light, overlay, null, color, null);
        stack.popPose();
    }
}
