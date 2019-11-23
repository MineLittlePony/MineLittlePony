package com.minelittlepony.client.util.render;

import net.minecraft.client.model.Model;
import net.minecraft.client.model.ModelPart;

import com.minelittlepony.mson.api.model.MsonPart;

@Deprecated
public class Part extends ModelPart implements MsonPart {

    public Part(Model model) {
        super(model);
    }

    public Part(Model model, int texX, int texY) {
        super(model, texX, texY);
    }

}
