package com.minelittlepony.client.render.layer;

import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.LivingEntity;

import com.google.common.collect.Lists;
import com.minelittlepony.client.model.IPonyModel;
import com.minelittlepony.client.model.gear.ChristmasHat;
import com.minelittlepony.client.model.gear.Muffin;
import com.minelittlepony.client.model.gear.SaddleBags;
import com.minelittlepony.client.model.gear.Stetson;
import com.minelittlepony.client.model.gear.WitchHat;
import com.minelittlepony.client.render.IPonyRender;
import com.minelittlepony.model.BodyPart;
import com.minelittlepony.model.gear.IGear;
import com.minelittlepony.model.gear.IStackable;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class LayerGear<T extends LivingEntity, M extends EntityModel<T> & IPonyModel<T>> extends AbstractPonyLayer<T, M> {

    public static final IGear SADDLE_BAGS = new SaddleBags();
    public static final IGear WITCH_HAT = new WitchHat();
    public static final IGear MUFFIN = new Muffin();
    public static final IGear STETSON = new Stetson();
    public static final IGear ANTLERS = new ChristmasHat();

    private static List<IGear> gears = Lists.newArrayList(
            SADDLE_BAGS,
            WITCH_HAT,
            MUFFIN,
            STETSON,
            ANTLERS
    );

    public LayerGear(IPonyRender<T, M> renderer) {
        super(renderer);
    }

    @Override
    public void render(MatrixStack stack, VertexConsumerProvider renderContext, int lightUv, T entity, float limbDistance, float limbAngle, float tickDelta, float age, float headYaw, float headPitch) {

        if (entity.isInvisible()) {
            return;
        }

        M model = getPlayerModel();

        Map<BodyPart, Float> renderStackingOffsets = new HashMap<>();

        for (IGear gear : gears) {
            if (getContext().shouldRender(model, entity, gear)) {
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

        VertexConsumer vertexConsumer = renderContext.getBuffer(RenderLayer.getEntitySolid(gear.getTexture(entity, getContext())));
        gear.renderPart(stack, vertexConsumer, OverlayTexture.DEFAULT_UV, lightUv, limbDistance, limbAngle, tickDelta, 1F, entity.getUuid());
    }
}
