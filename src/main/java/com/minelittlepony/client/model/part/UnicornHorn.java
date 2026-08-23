package com.minelittlepony.client.model.part;

import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.util.*;

import com.minelittlepony.api.model.*;
import com.minelittlepony.client.render.MagicGlow;
import com.minelittlepony.client.render.entity.state.PonyRenderState;
import com.minelittlepony.api.model.PonyModel;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;


public class UnicornHorn<T extends PonyRenderState> implements SubModel<T> {
    private final ModelPart horn;
    private final ModelPart glow;

    private int tint;

    public UnicornHorn(ModelPart tree) {
        horn = tree.getChild("bone");
        glow = tree.getChild("corona");
    }

    @Override
    public void accept(PoseStack matrices, VertexConsumer vertices, int overlay, int light, int color) {
        horn.render(matrices, vertices, overlay, light, color);
    }

    @Override
    public void render(PonyModel<T> model, T state, PoseStack matrices, SubmitNodeCollector frame) {
        if (tint != 0) {
            matrices.pushPose();
            model.transformAccessory(state, BodyPart.HEAD, matrices);
            frame.submitModelPart(glow, matrices, MagicGlow.getRenderLayer(),
                    LightCoordsUtil.FULL_BRIGHT,
                    OverlayTexture.NO_OVERLAY, null, ARGB.color(1F, tint),
                    null, 0);
            matrices.popPose();
        }
    }

    @Override
    public void setAngles(PonyModel<T> model, T state) {
        horn.resetPose();
        glow.resetPose();
        tint = !state.isSpectator && state.hasMagicGlow() && state.headVisible && state.hornGlowVisible ? state.glowColor : 0;
        horn.visible = !state.isSpectator && state.race.hasHorn() && state.headVisible;
        glow.visible = tint != 0;
        // setting horn length
        if (horn.visible) {
            horn.getChild("stub").visible = false;
            horn.getChild("foal").visible = false;
            horn.getChild("short").visible = false;
            horn.getChild("full").visible = false;
            horn.getChild("long").visible = false;
            switch (state.attributes.metadata.hornLength()) {
                case STUB:
                    horn.getChild("stub").visible = true;
                    glow.yScale = 0.25F;
                    break;
                case FOAL:
                    horn.getChild("foal").visible = true;
                    glow.yScale = 0.5F;
                    break;
                case SHORT:
                    horn.getChild("short").visible = true;
                    glow.yScale = 0.75F;
                    break;
                case LONG:
                    horn.getChild("long").visible = true;
                    glow.yScale = 1.25F;
                    break;
                default:
                    horn.getChild("full").visible = true;
                    break;
            }
        }
        state.transformation.transform(state.attributes, BodyPart.HORN, horn);
        state.transformation.transform(state.attributes, BodyPart.HORN, glow);
    }

    public void setHidden() {
        horn.visible = false;
        glow.visible = false;
    }
}
