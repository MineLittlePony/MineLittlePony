package com.minelittlepony.client.model.entity.race;

import com.minelittlepony.api.model.SubModel;
import com.minelittlepony.api.model.WingedPonyModel;
import com.minelittlepony.client.model.part.PonyWings;
import com.minelittlepony.client.render.entity.state.PonyRenderState;
import com.minelittlepony.mson.api.ModelView;

import net.minecraft.client.model.ModelPart;

public class AlicornModel<T extends PonyRenderState> extends UnicornModel<T> implements WingedPonyModel<T> {

    private PonyWings<T> wings;

    public AlicornModel(ModelPart tree, boolean smallArms) {
        super(tree, smallArms);
    }

    @Override
    public void init(ModelView context) {
        super.init(context);
        wings = addPart(context.findByName("wings"));
        bodyRenderList.add(forPart(this::getWings).checked(() -> currentState.race.hasWings()));
    }

    @Override
    public SubModel<T> getWings() {
        return wings;
    }
}
