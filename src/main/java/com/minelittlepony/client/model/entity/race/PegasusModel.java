package com.minelittlepony.client.model.entity.race;

import net.minecraft.client.model.geom.ModelPart;

import com.minelittlepony.api.model.ModelWithWings;
import com.minelittlepony.client.model.part.PonyWings;
import com.minelittlepony.client.render.entity.state.PonyRenderState;
import com.minelittlepony.mson.api.ModelView;

public class PegasusModel<T extends PonyRenderState> extends EarthPonyModel<T> implements ModelWithWings<T> {

    public PegasusModel(ModelPart tree, boolean smallArms) {
        super(tree, smallArms);
    }

    @Override
    public void init(ModelView context) {
        super.init(context);
        bodyRenderList.add(addPart(context.<PonyWings<T>>findByName("wings")));
    }
}
