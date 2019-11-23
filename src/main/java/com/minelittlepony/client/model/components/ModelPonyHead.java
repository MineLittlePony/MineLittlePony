package com.minelittlepony.client.model.components;

import com.minelittlepony.client.pony.PonyData;
import com.minelittlepony.client.util.render.Part;
import com.minelittlepony.model.ICapitated;
import com.minelittlepony.pony.IPonyData;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.entity.model.SkullOverlayEntityModel;
import net.minecraft.client.util.math.MatrixStack;

public class ModelPonyHead extends SkullOverlayEntityModel implements ICapitated<ModelPart> {

    private PonySnout snout;

    private UnicornHorn horn;

    private ModelPart ears;

    public IPonyData metadata = new PonyData();

    public ModelPonyHead() {
        super();

        snout = new PonySnout(this, 0, -3, 2);
        horn = new UnicornHorn(this, 0, 0, 0, -1, 4);

        snout.init(0, 0);

        ears = new Part(this, 0, 0)
                .offset(0, -3, 2).around(0, 0, -2)
                .tex(12, 16).box(-3.999F, -6, 1, 2, 2, 2, 0)
                     .flip().box( 1.999F, -6, 1, 2, 2, 2, 0);

        getHead().addChild(ears);
    }

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
        snout.isHidden = metadata.getRace().isHuman();
        ears.visible = !snout.isHidden;

        snout.setGender(metadata.getGender());

        super.render(stack, vertices, overlayUv, lightUv, red, green, blue, alpha);

        if (metadata.hasHorn()) {
            getHead().rotate(stack);
            horn.renderPart(stack, vertices, overlayUv, lightUv, red, green, blue, alpha, null);
        }
    }
}
