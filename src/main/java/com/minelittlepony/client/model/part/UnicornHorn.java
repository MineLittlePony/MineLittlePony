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
    protected final ModelPart horn;
    protected final ModelPart glow;
    protected final ModelPart[] hornLength;

    //TODO: make a custom glow for changeling antlers that doesn't look like GARBAGE!!!

    protected int tint;

    public UnicornHorn(ModelPart tree) {
        horn = tree.getChild("bone");
        glow = tree.getChild("corona");
        // the following values correspond to what's set in HornLength.java
        hornLength = new ModelPart[] {
            horn.getChild("stub"),
            horn.getChild("foal"),
            horn.getChild("short"),
            horn.getChild("full"),
            horn.getChild("long")
        };
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
            for (ModelPart modelPart : hornLength) {
                modelPart.visible = false;
            }
            hornLength[state.attributes.metadata.hornLength().getValue()].visible = true;
            glow.yScale = state.attributes.metadata.hornLength().getGlowSize();
        }
        state.transformation.transform(state.attributes, BodyPart.HORN, horn);
        state.transformation.transform(state.attributes, BodyPart.HORN, glow);
    }

    public void setHidden() {
        horn.visible = glow.visible = false;
    }
}
