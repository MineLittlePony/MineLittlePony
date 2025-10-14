package com.minelittlepony.client.model.gear;

import net.minecraft.client.model.ModelPart;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.command.OrderedRenderCommandQueue;
import net.minecraft.client.render.entity.state.BipedEntityRenderState;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.MathHelper;

import com.minelittlepony.api.model.BodyPart;
import com.minelittlepony.api.model.PonyModel;
import com.minelittlepony.api.model.gear.WearableGear;
import com.minelittlepony.api.pony.meta.Wearable;

import java.util.Calendar;

public class DeerAntlers<T extends BipedEntityRenderState & PonyModel.AttributedHolder> extends WearableGear<T> {
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
    public void setAngles(GearRenderState<T> state) {
        super.setAngles(state);
        float pi = MathHelper.PI * (float) Math.pow(state.limbAngle, 16);

        float mve = state.limbDistance * 0.6662f;
        float srt = state.limbAngle / 10;

        float bodySwing = MathHelper.cos(mve + pi) * srt;

        bodySwing += 0.1F;


        left.roll = bodySwing;
        right.roll = -bodySwing;
    }

    @Override
    public void render(MatrixStack matrices, GearRenderState<T> state, OrderedRenderCommandQueue queue, RenderLayer layer, int overlay, int light, int color) {
        int tint = state.entityState.getAttributes().metadata.glowColor();
        super.render(matrices, state, queue, layer, overlay, light, tint != 0 ? tint : color);
    }
}
