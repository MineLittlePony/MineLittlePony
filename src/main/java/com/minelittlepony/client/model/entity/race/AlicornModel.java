package com.minelittlepony.client.model.entity.race;

import com.minelittlepony.api.model.IPart;
import com.minelittlepony.api.model.IPegasus;
import com.minelittlepony.client.model.part.PonyWings;
import com.minelittlepony.mson.api.ModelView;

import net.minecraft.client.model.ModelPart;
import net.minecraft.entity.LivingEntity;

public class AlicornModel<T extends LivingEntity> extends UnicornModel<T> implements IPegasus {

    private PonyWings<AlicornModel<T>> wings;

    public AlicornModel(ModelPart tree, boolean smallArms) {
        super(tree, smallArms);
    }

    @Override
    public void init(ModelView context) {
        super.init(context);
        wings = addPart(context.findByName("wings"));
        bodyRenderList.add(forPart(this::getWings).checked(this::canFly));
    }

    @Override
    public IPart getWings() {
        return wings;
    }
}
