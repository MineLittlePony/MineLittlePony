package com.minelittlepony.client.model.entity.race;

import com.minelittlepony.api.model.IPart;
import com.minelittlepony.client.model.AbstractPonyModel;
import com.minelittlepony.client.model.part.*;
import com.minelittlepony.mson.api.ModelView;

import net.minecraft.client.model.ModelPart;
import net.minecraft.entity.LivingEntity;

public class EarthPonyModel<T extends LivingEntity> extends AbstractPonyModel<T> {

    private final boolean smallArms;

    protected IPart tail;
    protected PonySnout snout;
    protected PonyEars ears;

    public EarthPonyModel(ModelPart tree, boolean smallArms) {
        super(tree);
        this.smallArms = smallArms;
    }

    @Override
    public void init(ModelView context) {
        super.init(context);

        tail = addPart(context.findByName("tail"));
        addPart(context.findByName("snout"));
        addPart(context.findByName("ears"));

        bodyRenderList.add(forPart(tail));
    }

    @Override
    public void setModelAngles(T entity, float move, float swing, float ticks, float headYaw, float headPitch) {
        super.setModelAngles(entity, move, swing, ticks, headYaw, headPitch);
        cape.pivotY = sneaking ? 2 : riding ? -4 : 0;
    }

    @Override
    protected float getLegOutset() {
        if (smallArms) {
            return Math.max(1, super.getLegOutset() - 1);
        }
        return super.getLegOutset();
    }
}
