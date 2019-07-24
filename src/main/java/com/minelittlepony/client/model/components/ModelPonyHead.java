package com.minelittlepony.client.model.components;

import com.minelittlepony.client.pony.PonyData;
import com.minelittlepony.client.util.render.PonyRenderer;
import com.minelittlepony.model.ICapitated;
import com.minelittlepony.pony.IPonyData;

import net.minecraft.client.model.Cuboid;
import net.minecraft.client.render.entity.model.SkullOverlayEntityModel;

public class ModelPonyHead extends SkullOverlayEntityModel implements ICapitated<Cuboid> {

    private PonySnout snout;

    private final UnicornHorn horn;

    private final PonyRenderer ears;

    public IPonyData metadata = new PonyData();

    public ModelPonyHead() {
        super();

        snout = new PonySnout(this, 0, -3, 2);
        horn = new UnicornHorn(this, 0, 0, 0, -1, 4);

        snout.init(0, 0);

        ears = new PonyRenderer(this, 0, 0)
                .offset(0, -3, 2).around(0, 0, -2)
                .tex(12, 16).box(-3.999F, -6, 1, 2, 2, 2, 0)
                     .flip().box( 1.999F, -6, 1, 2, 2, 2, 0);

        getHead().addChild(ears);
    }

    @Override
    public Cuboid getHead() {
        return skull;
    }

    @Override
    public boolean hasHeadGear() {
        return false;
    }

    @Override
    public void render(float move, float swing, float ticks, float headYaw, float headPitch, float scale) {
        snout.isHidden = metadata.getRace().isHuman();
        ears.field_3664 = snout.isHidden;

        snout.setGender(metadata.getGender());

        super.render(move, swing, ticks, headYaw, headPitch, scale);

        if (metadata.hasHorn()) {
            getHead().applyTransform(scale);
            horn.renderPart(scale, null);
        }
    }
}
