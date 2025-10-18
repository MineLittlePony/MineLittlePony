package com.minelittlepony.client.model.entity.race;

import com.minelittlepony.api.model.BodyPart;
import com.minelittlepony.api.model.ModelWithWings;
import com.minelittlepony.client.model.part.PonyWings;
import com.minelittlepony.client.render.entity.state.PonyRenderState;
import com.minelittlepony.mson.api.ModelView;

import net.minecraft.client.model.ModelPart;

public class AlicornModel<T extends PonyRenderState> extends UnicornModel<T> implements ModelWithWings<T> {

    public AlicornModel(ModelPart tree, boolean smallArms) {
        super(tree, smallArms);
    }

    @Override
    public void init(ModelView context) {
        super.init(context);
        bodyRenderList.add(withStage(BodyPart.WINGS).add(addPart(context.<PonyWings<T>>findByName("wings"))));
    }
}
