package com.minelittlepony.client.model.components;

import net.minecraft.client.model.Cuboid;
import net.minecraft.client.model.Model;

import com.minelittlepony.client.util.render.PonyRenderer;
import com.minelittlepony.model.ICapitated;
import com.minelittlepony.model.IPart;

import java.util.UUID;

public class PonyEars implements IPart {

    private final PonyRenderer head;
    private final boolean bat;

    private PonyRenderer right;
    private PonyRenderer left;

    public <T extends Model & ICapitated<Cuboid>> PonyEars(PonyRenderer head, boolean bat) {
        this.head = head;
        this.bat = bat;
    }

    @Override
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
    public void renderPart(float scale, UUID interpolatorId) {
    }

    @Override
    public void setVisible(boolean visible) {
        right.visible = visible;
        left.visible = visible;
    }
}
