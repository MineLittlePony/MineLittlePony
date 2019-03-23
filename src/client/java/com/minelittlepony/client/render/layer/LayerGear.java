package com.minelittlepony.client.render.layer;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.RenderLivingBase;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.ResourceLocation;

import org.lwjgl.opengl.GL11;

import com.google.common.collect.Lists;
import com.minelittlepony.client.ducks.IRenderPony;
import com.minelittlepony.client.model.AbstractPonyModel;
import com.minelittlepony.client.model.IClientModel;
import com.minelittlepony.client.model.gear.ChristmasHat;
import com.minelittlepony.client.model.gear.IGear;
import com.minelittlepony.client.model.gear.IStackable;
import com.minelittlepony.client.model.gear.Muffin;
import com.minelittlepony.client.model.gear.SaddleBags;
import com.minelittlepony.client.model.gear.Stetson;
import com.minelittlepony.client.model.gear.WitchHat;
import com.minelittlepony.model.BodyPart;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class LayerGear<T extends EntityLivingBase> extends AbstractPonyLayer<T> {

    private static List<IGear> gears = Lists.newArrayList(
            new SaddleBags(),
            new WitchHat(),
            new Muffin(),
            new Stetson(),
            new ChristmasHat()
    );

    public <R extends RenderLivingBase<T> & IRenderPony<T>> LayerGear(R renderer) {
        super(renderer);
    }

    @Override
    public void doRenderLayer(T entity, float move, float swing, float partialTicks, float ticks, float headYaw, float headPitch, float scale) {

        if (entity.isInvisible()) {
            return;
        }

        AbstractPonyModel model = getPlayerModel();

        Map<BodyPart, Float> renderStackingOffsets = new HashMap<>();

        for (IGear gear : gears) {
            if (gear.canRender(model, entity)) {
                GlStateManager.pushMatrix();
                model.transform(gear.getGearLocation());
                gear.getOriginBodyPart(model).postRender(scale);

                if (gear instanceof IStackable) {
                    BodyPart part = gear.getGearLocation();
                    renderStackingOffsets.compute(part, (k, v) -> {
                        float offset = ((IStackable)gear).getStackingOffset();
                       if (v != null) {
                           GlStateManager.translate(0, -v, 0);
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

    private void renderGear(IClientModel model, T entity, IGear gear, float move, float swing, float scale, float ticks) {
        GL11.glPushAttrib(GL11.GL_ALL_ATTRIB_BITS);

        ResourceLocation texture = gear.getTexture(entity);
        if (texture == null) {
            texture = getPonyRenderer().getTexture(entity);
        }

        getRenderer().bindTexture(texture);

        gear.setLivingAnimations(model, entity);
        gear.setRotationAndAngles(model.isGoingFast(), entity.getUniqueID(), move, swing, model.getWobbleAmount(), ticks);
        gear.renderPart(scale, entity.getUniqueID());

        GL11.glPopAttrib();
    }
}
