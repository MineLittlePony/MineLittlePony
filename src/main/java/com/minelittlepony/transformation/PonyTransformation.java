package com.minelittlepony.transformation;

import com.minelittlepony.model.AbstractPonyModel;
import com.minelittlepony.model.BodyPart;

public interface PonyTransformation {
    void transform(AbstractPonyModel model, BodyPart part);
}
