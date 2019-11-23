package com.minelittlepony.client.model.part;

import net.minecraft.client.model.Model;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.util.math.MatrixStack;

import com.minelittlepony.client.util.render.Part;
import com.minelittlepony.model.ICapitated;
import com.minelittlepony.model.IPart;

import java.util.UUID;

public class PonyEars implements IPart {

    private final ModelPart head;
    private final boolean bat;

    private ModelPart right;
    private ModelPart left;

    public <T extends Model & ICapitated<ModelPart>> PonyEars(ModelPart head, boolean bat) {
        this.head = head;
        this.bat = bat;
    }

    @Deprecated
    public void init(float yOffset, float stretch) {
        right = head.child().tex(12, 16).box(-4, -6, 1, 2, 2, 2, stretch);

        if (bat) {
            right.tex(0, 3).box(-3.5F, -6.49F, 1.001F, 1, 1, 1, stretch)
                 .tex(0, 5).box(-2.998F, -6.49F, 2.001F, 1, 1, 1, stretch);
        }

        left = head.child().flip().tex(12, 16).box( 2, -6, 1, 2, 2, 2, stretch);

        if (bat) {
            left.tex(0, 3).box( 2.5F, -6.49F, 1.001F, 1, 1, 1, stretch)
                .tex(0, 5).box( 1.998F, -6.49F, 2.001F, 1, 1, 1, stretch);
        }
    }

    @Override
    public void renderPart(MatrixStack stack, VertexConsumer vertices, int overlayUv, int lightUv, float red, float green, float blue, float alpha, UUID interpolatorId) {
    }

    @Override
    public void setVisible(boolean visible) {
        right.visible = visible;
        left.visible = visible;
    }
}
