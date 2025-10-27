package com.minelittlepony.client.render.command;

import it.unimi.dsi.fastutil.objects.Object2ObjectAVLTreeMap;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.command.*;

import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.function.Function;

public class MagicOverlayOrderedRenderCommandQueue extends MagicOverlayRenderCommandQueue implements OrderedRenderCommandQueue {
    private final Object2ObjectAVLTreeMap<RenderCommandQueue, MagicOverlayRenderCommandQueue> queues = new Object2ObjectAVLTreeMap<>();

    public MagicOverlayOrderedRenderCommandQueue(OrderedRenderCommandQueue parent, Function<RenderLayer, @Nullable RenderLayer> layer, int color, List<Pass> passes) {
        super(parent, parent, layer, color, passes);
    }

    @Override
    public RenderCommandQueue getBatchingQueue(int order) {
        return this.queues.computeIfAbsent(unwrap().getBatchingQueue(order), this::createQueue);
    }

    protected MagicOverlayRenderCommandQueue createQueue(RenderCommandQueue parent) {
        return new MagicOverlayRenderCommandQueue(this, parent, layer, color, passes);
    }
}
