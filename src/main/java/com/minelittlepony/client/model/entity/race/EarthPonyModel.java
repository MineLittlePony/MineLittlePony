package com.minelittlepony.client.model.entity.race;

import com.minelittlepony.api.model.SubModel;
import com.minelittlepony.client.model.AbstractPonyModel;
import com.minelittlepony.client.model.part.*;
import com.minelittlepony.client.render.entity.state.PonyRenderState;
import com.minelittlepony.mson.api.ModelView;

import net.minecraft.client.model.ModelPart;

public class EarthPonyModel<T extends PonyRenderState> extends AbstractPonyModel<T> {
    protected SubModel<T> tail;
    protected PonySnout snout;
    protected PonyEars ears;

    private final ModelPart mane;
    private final ModelPart nose;
    private final ModelPart tailStub;

    public EarthPonyModel(ModelPart tree, boolean smallArms) {
        super(tree, smallArms);
        mane = neck.getChild("mane");
        nose = head.getChild("nose");
        tailStub = body.getChild("tail_stub");
    }

    @Override
    public void init(ModelView context) {
        super.init(context);

        tail = addPart(context.findByName("tail"));
        snout = addPart(context.findByName("snout"));
        ears = addPart(context.findByName("ears"));

        bodyRenderList.add(tail);
    }

    protected void setModelVisibilities(T state) {
        super.setModelVisibilities(state);
        mane.visible = state.attributes.isHorsey;
        nose.visible = state.attributes.isHorsey;
        tailStub.visible = !state.attributes.isHorsey;
    }

    @Override
    public void setVisible(boolean visible) {
        super.setVisible(visible);
        mane.visible = visible;
        nose.visible = visible;
        tailStub.visible = visible;
    }
}
