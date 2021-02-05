package com.minelittlepony.client.render.entity.feature;

import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.LivingEntity;

import com.google.common.collect.Streams;
import com.minelittlepony.api.pony.meta.Wearable;
import com.minelittlepony.client.model.IPonyModel;
import com.minelittlepony.client.model.ModelType;
import com.minelittlepony.client.render.IPonyRenderContext;
import com.minelittlepony.model.BodyPart;
import com.minelittlepony.model.gear.IGear;
import com.minelittlepony.model.gear.IStackable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class GearFeature<T extends LivingEntity, M extends EntityModel<T> & IPonyModel<T>> extends AbstractPonyFeature<T, M> {

    private static final List<Entry> MOD_GEARS = new ArrayList<>();

    public static IGear addModGear(IGear gear) {
        MOD_GEARS.add(new Entry(gear, Wearable.NONE));
        return gear;
    }

    private final List<Entry> gears;

    public GearFeature(IPonyRenderContext<T, M> renderer) {
        super(renderer);

        gears = Streams.concat(ModelType.getWearables().map(e -> new Entry(e.getValue().createModel(), e.getKey())), MOD_GEARS.stream()).collect(Collectors.toList());
    }

    @Override
    public void render(MatrixStack stack, VertexConsumerProvider renderContext, int lightUv, T entity, float limbDistance, float limbAngle, float tickDelta, float age, float headYaw, float headPitch) {

        if (entity.isInvisible()) {
            return;
        }

        final M model = getModelWrapper().getBody();

        final Map<BodyPart, Float> renderStackingOffsets = new HashMap<>();

        for (Entry entry : gears) {
            final IGear gear = entry.gear;

            if (getContext().shouldRender(model, entity, entry.wearable, gear)) {
                stack.push();
                BodyPart part = gear.getGearLocation();
                model.transform(part, stack);
                model.getBodyPart(part).rotate(stack);

                if (gear instanceof IStackable) {
                    renderStackingOffsets.compute(part, (k, v) -> {
                        float offset = ((IStackable)gear).getStackingOffset();
                        if (v != null) {
                            stack.translate(0, -v, 0);
                            offset += v;
                        }
                        return offset;
                    });
                }

                renderGear(model, entity, gear, stack, renderContext, lightUv, limbDistance, limbAngle, tickDelta);
                stack.pop();
            }
        }
    }

    private void renderGear(M model, T entity, IGear gear, MatrixStack stack, VertexConsumerProvider renderContext, int lightUv, float limbDistance, float limbAngle, float tickDelta) {

        gear.setModelAttributes(model, entity);
        gear.pose(model.getAttributes().isGoingFast, entity.getUuid(), limbDistance, limbAngle, model.getWobbleAmount(), tickDelta);

        RenderLayer layer = RenderLayer.getEntityTranslucent(gear.getTexture(entity, getContext()));

        VertexConsumer vertexConsumer = renderContext.getBuffer(layer);
        gear.render(stack, vertexConsumer, lightUv, OverlayTexture.DEFAULT_UV, 1, 1, 1, 1, entity.getUuid());
    }

    static class Entry {
        IGear gear;
        Wearable wearable;

        Entry(IGear gear, Wearable wearable) {
            this.gear = gear;
            this.wearable = wearable;
        }
    }
}
