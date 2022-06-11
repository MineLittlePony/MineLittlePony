package com.minelittlepony.client.model;

import com.minelittlepony.api.model.ICapitated;
import com.minelittlepony.api.pony.IPonyData;
import com.minelittlepony.client.model.part.PonyEars;
import com.minelittlepony.client.model.part.PonySnout;
import com.minelittlepony.client.model.part.UnicornHorn;
import com.minelittlepony.client.pony.PonyData;
import com.minelittlepony.mson.api.ModelContext;
import com.minelittlepony.mson.api.MsonModel;

import net.minecraft.client.model.ModelPart;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.entity.model.SkullEntityModel;
import net.minecraft.client.util.math.MatrixStack;

public class PonySkullModel extends SkullEntityModel implements MsonModel, ICapitated<ModelPart> {

    private PonySnout snout;

    private UnicornHorn horn;

    private PonyEars ears;

    private ModelPart hair;

    public IPonyData metadata = PonyData.NULL;

    public PonySkullModel(ModelPart tree) {
        super(tree);
    }

    @Override
    public void init(ModelContext context) {
        hair = context.findByName("hair");
        snout = context.findByName("snout");
        horn = context.findByName("horn");
        ears = context.findByName("ears");
    }

    @Override
    public ModelPart getHead() {
        return head;
    }

    @Override
    public void setHeadRotation(float animationProgress, float yaw, float pitch) {
        super.setHeadRotation(animationProgress, yaw, pitch);
        hair.yaw = head.yaw;
        hair.pitch = head.pitch;
     }

    @Override
    public void render(MatrixStack stack, VertexConsumer vertices, int overlayUv, int lightUv, float red, float green, float blue, float alpha) {
        snout.setVisible(!metadata.getRace().isHuman());
        ears.setVisible(!metadata.getRace().isHuman());

        snout.setGender(metadata.getGender());

        hair.render(stack, vertices, overlayUv, lightUv, red, green, blue, alpha);
        head.render(stack, vertices, overlayUv, lightUv, red, green, blue, alpha);

        if (metadata.hasHorn()) {
            getHead().rotate(stack);
            horn.renderPart(stack, vertices, overlayUv, lightUv, red, green, blue, alpha, null);
        }
    }
}
