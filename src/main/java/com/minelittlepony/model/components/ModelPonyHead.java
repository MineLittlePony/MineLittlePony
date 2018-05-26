package com.minelittlepony.model.components;

import com.minelittlepony.model.capabilities.ICapitated;
import com.minelittlepony.pony.data.IPonyData;
import com.minelittlepony.pony.data.PonyData;

import net.minecraft.client.model.ModelHumanoidHead;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;

public class ModelPonyHead extends ModelHumanoidHead implements ICapitated {

    private final PonySnout snout;

    private final UnicornHorn horn;

    public IPonyData metadata = new PonyData();

    public ModelPonyHead() {
        super();

        snout = new PonySnout(this);
        horn = new UnicornHorn(this, 0, 0);
    }

    @Override
    public ModelRenderer getHead() {
        return skeletonHead;
    }

    @Override
    public void render(Entity entity, float move, float swing, float ticks, float headYaw, float headPitch, float scale) {

        snout.setGender(metadata.getGender());

        super.render(entity, move, swing, ticks, headYaw, headPitch, scale);

        if (metadata.hasMagic()) {
            horn.render(scale);
        }
    }
}
