package com.minelittlepony.client.model.gear;

import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.state.HumanoidRenderState;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.util.Mth;

import com.minelittlepony.api.model.BodyPart;
import com.minelittlepony.api.model.PonyModel;
import com.minelittlepony.api.model.gear.WearableGear;
import com.minelittlepony.api.pony.meta.Wearable;
import com.minelittlepony.client.render.entity.state.PonyRenderState;
import com.mojang.blaze3d.vertex.PoseStack;

import java.util.Calendar;

public class DeerAntlers<T extends HumanoidRenderState & PonyModel.AttributedHolder> extends WearableGear<T> {
    private static boolean dayChecked = false;
    private static boolean dayResult = false;
    private static boolean isChristmasDay() {
        if (!dayChecked) {
            dayChecked = true;
            Calendar cal = Calendar.getInstance();
            dayResult = cal.get(Calendar.MONTH) == Calendar.DECEMBER
                     && Math.abs(cal.get(Calendar.DAY_OF_MONTH) - 25) < 2;
        }

        return dayResult;
    }

    private final ModelPart left;
    private final ModelPart right;

    public DeerAntlers(ModelPart tree) {
        super(tree, Wearable.ANTLERS, BodyPart.HEAD, 0);
        left = tree.getChild("left");
        right = tree.getChild("right");
    }

    @Override
    public boolean canRender(PonyModel<?> model, T entity) {
        return isChristmasDay() || super.canRender(model, entity);
    }

    @Override
    public void setupAnim(GearRenderState<T> state) {
        super.setupAnim(state);
        float pi = Mth.PI * (float) Math.pow(state.limbAngle, 16);

        float mve = state.limbDistance * 0.6662f;
        float srt = state.limbAngle / 10;

        float bodySwing = Mth.cos(mve + pi) * srt;

        bodySwing += 0.1F;


        left.zRot = bodySwing;
        right.zRot = -bodySwing;
    }

    @Override
    public void render(PoseStack matrices, GearRenderState<T> state, SubmitNodeCollector queue, RenderType layer, int overlay, int light, int color) {
        int tint = state.entityState instanceof PonyRenderState s ? s.glowColor : state.entityState.getAttributes().metadata.glowColor();
        super.render(matrices, state, queue, layer, overlay, light, tint != 0 ? tint : color);
    }
}
