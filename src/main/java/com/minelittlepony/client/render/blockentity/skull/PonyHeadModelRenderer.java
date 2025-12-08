package com.minelittlepony.client.render.blockentity.skull;

import net.minecraft.client.render.command.OrderedRenderCommandQueue;
import net.minecraft.client.render.item.model.special.HeadModelRenderer;
import net.minecraft.client.render.item.model.special.SpecialModelRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemDisplayContext;
import net.minecraft.item.ItemStack;

import org.joml.Vector3fc;

import com.minelittlepony.api.config.PonyConfig;
import com.minelittlepony.client.render.blockentity.skull.PonySkullRenderer.Data;

import java.util.function.Consumer;

public class PonyHeadModelRenderer implements SpecialModelRenderer<PonySkullRenderer.Data> {

    private final HeadModelRenderer renderer;
    private final PonySkullRenderer.Data data;

    public PonyHeadModelRenderer(HeadModelRenderer renderer, PonySkullRenderer.Data data) {
        this.data = data;
        this.renderer = renderer;
    }

    @Override
    public PonySkullRenderer.Data getData(ItemStack stack) {
        if (data == null || !data.model().canRender(PonyConfig.getInstance())) {
            return null;
        }
        return data;
    }

    @Override
    public void render(Data data, ItemDisplayContext displayContext, MatrixStack matrices, OrderedRenderCommandQueue queue, int light, int overlay, boolean glint, int i) {
        if (data == null || !data.render(null, 180, 0, matrices, queue, light, 0, null)) {
            renderer.render(null, displayContext, matrices, queue, light, overlay, glint, i);
        }
    }

    @Override
    public void collectVertices(Consumer<Vector3fc> vertices) {
        renderer.collectVertices(vertices);
    }
}
