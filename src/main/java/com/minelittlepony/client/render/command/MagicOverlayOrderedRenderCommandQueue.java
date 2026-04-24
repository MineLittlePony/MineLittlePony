package com.minelittlepony.client.render.command;

import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import net.minecraft.client.renderer.OrderedSubmitNodeCollector;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.rendertype.RenderType;

import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Map;
import java.util.function.Function;

public class MagicOverlayOrderedRenderCommandQueue extends MagicOverlayRenderCommandQueue implements SubmitNodeCollector {
    private final Map<OrderedSubmitNodeCollector, MagicOverlayRenderCommandQueue> queues = new Object2ObjectOpenHashMap<>();

    public MagicOverlayOrderedRenderCommandQueue(SubmitNodeCollector parent, Function<RenderType, @Nullable RenderType> layer, int color, List<Pass> passes) {
        super(parent, parent, layer, color, passes);
    }

    @Override
    public OrderedSubmitNodeCollector order(int order) {
        return this.queues.computeIfAbsent(unwrap().order(order), this::createQueue);
    }

    protected MagicOverlayRenderCommandQueue createQueue(OrderedSubmitNodeCollector parent) {
        return new MagicOverlayRenderCommandQueue(this, parent, layer, color, passes);
    }
}
