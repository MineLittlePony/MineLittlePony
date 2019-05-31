package com.minelittlepony.client.render.layer;

import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.Identifier;

import org.lwjgl.opengl.GL11;

import com.google.common.collect.Lists;
import com.minelittlepony.client.model.gear.ChristmasHat;
import com.minelittlepony.client.model.gear.Muffin;
import com.minelittlepony.client.model.gear.SaddleBags;
import com.minelittlepony.client.model.gear.Stetson;
import com.minelittlepony.client.model.gear.WitchHat;
import com.minelittlepony.client.render.IPonyRender;
import com.minelittlepony.model.BodyPart;
import com.minelittlepony.model.IPonyModel;
import com.minelittlepony.model.gear.IGear;
import com.minelittlepony.model.gear.IStackable;
import com.mojang.blaze3d.platform.GlStateManager;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class LayerGear<T extends LivingEntity, M extends EntityModel<T> & IPonyModel<T>> extends AbstractPonyLayer<T, M> {

    private static List<IGear> gears = Lists.newArrayList(
            new SaddleBags(),
            new WitchHat(),
            new Muffin(),
            new Stetson(),
            new ChristmasHat()
    );

    public LayerGear(IPonyRender<T, M> renderer) {
        super(renderer);
    }

    @Override
    public void render(T entity, float move, float swing, float partialTicks, float ticks, float headYaw, float headPitch, float scale) {

        if (entity.isInvisible()) {
            return;
        }

        M model = getPlayerModel();

        Map<BodyPart, Float> renderStackingOffsets = new HashMap<>();

        for (IGear gear : gears) {
            if (gear.canRender(model, entity)) {
                GlStateManager.pushMatrix();
                model.transform(gear.getGearLocation());
                model.getBodyPart(gear.getGearLocation()).applyTransform(scale);

                if (gear instanceof IStackable) {
                    BodyPart part = gear.getGearLocation();
                    renderStackingOffsets.compute(part, (k, v) -> {
                        float offset = ((IStackable)gear).getStackingOffset();
                        if (v != null) {
                            GlStateManager.translatef(0, -v, 0);
                            offset += v;
                        }
                        return offset;
                    });
                }

                renderGear(model, entity, gear, move, swing, scale, ticks);
                GlStateManager.popMatrix();
            }
        }
    }

    private void renderGear(M model, T entity, IGear gear, float move, float swing, float scale, float ticks) {
        GL11.glPushAttrib(GL11.GL_ALL_ATTRIB_BITS);

        Identifier texture = gear.getTexture(entity);
        if (texture == null) {
            texture = getContext().findTexture(entity);
        }

        getContext().bindTexture(texture);

        gear.setLivingAnimations(model, entity);
        gear.setRotationAndAngles(model.isGoingFast(), entity.getUuid(), move, swing, model.getWobbleAmount(), ticks);
        gear.renderPart(scale, entity.getUuid());

        GL11.glPopAttrib();
    }
}
