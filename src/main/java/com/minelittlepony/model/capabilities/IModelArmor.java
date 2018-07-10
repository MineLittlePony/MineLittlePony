package com.minelittlepony.model.capabilities;

import net.minecraft.client.model.ModelBiped;

public interface IModelArmor extends IModel {
    <T extends ModelBiped & IModel> void synchroniseLegs(T model);

    void showFeet(boolean show);

    void showLegs(boolean isPony);

    void showSaddle(boolean isPony);

    void showHead(boolean show);
}
