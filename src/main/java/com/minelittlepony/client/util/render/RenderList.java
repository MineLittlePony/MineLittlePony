package com.minelittlepony.client.util.render;

import net.minecraft.client.model.ModelPart;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.util.math.MatrixStack;

import java.util.*;
import java.util.function.BooleanSupplier;
import java.util.function.Consumer;

public interface RenderList {
    void accept(MatrixStack stack, VertexConsumer vertices, int overlayUv, int lightUv, float red, float green, float blue, float alpha);

    default RenderList add(RenderList part) {
        return this;
    }

    default RenderList add(Consumer<MatrixStack> action) {
        return add((stack, vertices, overlayUv, lightUv, red, green, blue, alpha) -> action.accept(stack));
    }

    default RenderList add(ModelPart...parts) {
        return add(of(parts));
    }

    default RenderList checked(BooleanSupplier check) {
        RenderList self = this;
        return (stack, vertices, overlayUv, lightUv, red, green, blue, alpha) -> {
            if (check.getAsBoolean()) {
                self.accept(stack, vertices, overlayUv, lightUv, red, green, blue, alpha);
            }
        };
    }

    default void clear() {}

    static RenderList of() {
        return new Impl(List.of());
    }

    static RenderList of(ModelPart...parts) {
        return new Impl(Arrays.stream(parts).map(part -> (RenderList)part::render).toList());
    }

    public class Impl implements RenderList {
        private final List<RenderList> parts;

        Impl(List<RenderList> parts) {
            this.parts = new ArrayList<>(parts);
        }

        @Override
        public RenderList add(RenderList part) {
            parts.add(part);
            return this;
        }

        @Override
        public void clear() {
            parts.clear();
        }

        @Override
        public void accept(MatrixStack stack, VertexConsumer vertices, int overlayUv, int lightUv, float red, float green, float blue, float alpha) {
            parts.forEach(part -> part.accept(stack, vertices, overlayUv, lightUv, red, green, blue, alpha));
        }
    }
}


