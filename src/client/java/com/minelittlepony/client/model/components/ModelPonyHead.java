package com.minelittlepony.client.model.components;

import com.minelittlepony.client.pony.PonyData;
import com.minelittlepony.client.util.render.PonyRenderer;
import com.minelittlepony.model.ICapitated;
import com.minelittlepony.pony.IPonyData;

import javax.annotation.Nullable;

import net.minecraft.client.model.ModelHumanoidHead;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;

public class ModelPonyHead extends ModelHumanoidHead implements ICapitated<ModelRenderer> {

    private PonySnout snout;

    private final UnicornHorn horn;

    private final PonyRenderer ears;

    public IPonyData metadata = new PonyData();

    public ModelPonyHead() {
        super();

        snout = new PonySnout(this, 0, -3, 2);
        horn = new UnicornHorn(this, 0, 0, 0, -1, 4);

        snout.init(0, 0);

        ears = new PonyRenderer(this, 0, 0).offset(0, -3, 2).around(0, 0, -2)
                                    .tex(12, 16).box(-3.999F, -6, 1, 2, 2, 2, 0)
                                         .flip().box( 1.999F, -6, 1, 2, 2, 2, 0);

        skeletonHead.addChild(ears);
    }

    @Override
    public ModelRenderer getHead() {
        return skeletonHead;
    }

    @Nullable
    @Override
    public ModelRenderer getBody() {
        return null;
    }

    @Override
    public boolean hasHeadGear() {
        return false;
    }

    @Override
    public void render(Entity entity, float move, float swing, float ticks, float headYaw, float headPitch, float scale) {
        snout.isHidden = metadata.getRace().isHuman();
        ears.isHidden = snout.isHidden;

        snout.setGender(metadata.getGender());

        super.render(entity, move, swing, ticks, headYaw, headPitch, scale);

        if (metadata.hasMagic()) {
            skeletonHead.postRender(scale);
            horn.renderPart(scale, entity.getUniqueID());
        }
    }
}
