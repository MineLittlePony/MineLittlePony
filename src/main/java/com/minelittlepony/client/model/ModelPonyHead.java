package com.minelittlepony.client.model;

import com.minelittlepony.client.model.part.PonyEars;
import com.minelittlepony.client.model.part.PonySnout;
import com.minelittlepony.client.model.part.UnicornHorn;
import com.minelittlepony.client.pony.PonyData;
import com.minelittlepony.model.ICapitated;
import com.minelittlepony.pony.IPonyData;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.entity.model.SkullOverlayEntityModel;
import net.minecraft.client.util.math.MatrixStack;

public class ModelPonyHead extends SkullOverlayEntityModel implements ICapitated<ModelPart> {

    private PonySnout snout;

    private UnicornHorn horn;

    private PonyEars ears;

    public IPonyData metadata = new PonyData();

    @Override
    public ModelPart getHead() {
        return skull;
    }

    @Override
    public boolean hasHeadGear() {
        return false;
    }

    @Override
    public void render(MatrixStack stack, VertexConsumer vertices, int overlayUv, int lightUv, float red, float green, float blue, float alpha) {
        snout.setVisible(!metadata.getRace().isHuman());
        ears.setVisible(!metadata.getRace().isHuman());

        snout.setGender(metadata.getGender());

        super.render(stack, vertices, overlayUv, lightUv, red, green, blue, alpha);

        if (metadata.hasHorn()) {
            getHead().rotate(stack);
            horn.renderPart(stack, vertices, overlayUv, lightUv, red, green, blue, alpha, null);
        }
    }
}
