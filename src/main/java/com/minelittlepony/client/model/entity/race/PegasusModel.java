package com.minelittlepony.client.model.entity.race;

import com.minelittlepony.api.model.IPart;
import com.minelittlepony.api.model.IPegasus;
import com.minelittlepony.client.model.part.PonyWings;
import com.minelittlepony.mson.api.ModelView;

import net.minecraft.client.model.ModelPart;
import net.minecraft.entity.LivingEntity;

public class PegasusModel<T extends LivingEntity> extends EarthPonyModel<T> implements IPegasus {

    private PonyWings<PegasusModel<T>> wings;

    public PegasusModel(ModelPart tree, boolean smallArms) {
        super(tree, smallArms);
    }

    @Override
    public void init(ModelView context) {
        super.init(context);
        wings = addPart(context.findByName("wings"));
        bodyRenderList.add(forPart(this::getWings));
    }

    @Override
    public IPart getWings() {
        return wings;
    }
}
