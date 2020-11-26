package com.minelittlepony.client.model.entity.race;

import net.minecraft.client.model.ModelPart;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.LivingEntity;

import com.minelittlepony.client.model.armour.PonyArmourModel;
import com.minelittlepony.client.model.armour.ArmourWrapper;
import com.minelittlepony.model.BodyPart;
import com.minelittlepony.model.armour.IEquestrianArmour;

public class ZebraModel<T extends LivingEntity> extends EarthPonyModel<T> {

    public ZebraModel(ModelPart tree, boolean useSmallArms) {
        super(tree, useSmallArms);
    }

    @Override
    public IEquestrianArmour<?> createArmour() {
        return new ArmourWrapper<>(Armour::new);
    }

    @Override
    public void transform(BodyPart part, MatrixStack stack) {
        applyLongNeck(part, stack);
        super.transform(part, stack);
    }

    class Armour extends PonyArmourModel<T> {
        public Armour(ModelPart tree) {
            super(tree);
        }

        @Override
        public void transform(BodyPart part, MatrixStack stack) {
            applyLongNeck(part, stack);
            super.transform(part, stack);
        }
    }

    static void applyLongNeck(BodyPart part, MatrixStack stack) {
        if (part == BodyPart.HEAD || part == BodyPart.NECK) {
            stack.translate(0, -0.1F, 0);
        }
        if (part == BodyPart.NECK) {
             stack.scale(1, 1.3F, 1);
        }
    }
}
