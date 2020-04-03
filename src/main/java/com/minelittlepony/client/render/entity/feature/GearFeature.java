package com.minelittlepony.client.render.entity.feature;

import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.LivingEntity;

import com.minelittlepony.api.pony.meta.Wearable;
import com.minelittlepony.client.model.IPonyModel;
import com.minelittlepony.client.model.ModelType;
import com.minelittlepony.client.render.IPonyRenderContext;
import com.minelittlepony.model.BodyPart;
import com.minelittlepony.model.gear.IGear;
import com.minelittlepony.model.gear.IStackable;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

public class GearFeature<T extends LivingEntity, M extends EntityModel<T> & IPonyModel<T>> extends AbstractPonyFeature<T, M> {

    private final Map<Wearable, IGear> gears;

    public GearFeature(IPonyRenderContext<T, M> renderer) {
        super(renderer);

        gears = ModelType.getWearables().collect(Collectors.toMap(
                Map.Entry::getKey,
                e -> e.getValue().createModel()
        ));
    }

    @Override
    public void render(MatrixStack stack, VertexConsumerProvider renderContext, int lightUv, T entity, float limbDistance, float limbAngle, float tickDelta, float age, float headYaw, float headPitch) {

        if (entity.isInvisible()) {
            return;
        }

        M model = getModelWrapper().getBody();

        Map<BodyPart, Float> renderStackingOffsets = new HashMap<>();

        for (Map.Entry<Wearable, IGear> entry : gears.entrySet()) {
            Wearable wearable = entry.getKey();
            IGear gear = entry.getValue();

            if (getContext().shouldRender(model, entity, wearable, gear)) {
                stack.push();
                model.transform(gear.getGearLocation(), stack);
                model.getBodyPart(gear.getGearLocation()).rotate(stack);

                if (gear instanceof IStackable) {
                    BodyPart part = gear.getGearLocation();
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

        gear.setLivingAnimations(model, entity);
        gear.setRotationAndAngles(model.getAttributes().isGoingFast, entity.getUuid(), limbDistance, limbAngle, model.getWobbleAmount(), tickDelta);

        RenderLayer layer = RenderLayer.getEntityTranslucent(gear.getTexture(entity, getContext()));

        VertexConsumer vertexConsumer = renderContext.getBuffer(layer);
        gear.renderPart(stack, vertexConsumer, lightUv, OverlayTexture.DEFAULT_UV, 1, 1, 1, 1, entity.getUuid());
    }
}
