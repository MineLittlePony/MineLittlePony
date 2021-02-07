package com.minelittlepony.client.model.armour;

import net.minecraft.client.model.ModelPart;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.entity.model.BipedEntityModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.LivingEntity;

import com.minelittlepony.api.model.IModel;
import com.minelittlepony.api.model.armour.ArmourVariant;
import com.minelittlepony.api.model.armour.IArmourModel;
import com.minelittlepony.client.model.AbstractPonyModel;

public class PonyArmourModel<T extends LivingEntity> extends AbstractPonyModel<T> implements IArmourModel {

    private ModelPart chestPiece;

    private ModelPart steveRightLeg;
    private ModelPart steveLeftLeg;

    private ArmourVariant variant = ArmourVariant.NORMAL;

    public PonyArmourModel(ModelPart tree) {
        super(tree);
        chestPiece = tree.getChild("chestpiece");
        steveRightLeg = tree.getChild("steve_right_leg");
        steveLeftLeg = tree.getChild("steve_left_leg");
    }

    @Override
    protected void adjustBodyComponents(float rotateAngleX, float rotationPointY, float rotationPointZ) {
        super.adjustBodyComponents(rotateAngleX, rotationPointY, rotationPointZ);

        chestPiece.pitch = rotateAngleX;
        chestPiece.pivotY = rotationPointY;
        chestPiece.pivotZ = rotationPointZ;
    }

    @Override
    protected void renderBody(MatrixStack stack, VertexConsumer vertices, int overlayUv, int lightUv, float limbDistance, float limbAngle, float tickDelta, float alpha) {
        if (variant == ArmourVariant.LEGACY) {
            body.render(stack, vertices, overlayUv, lightUv, limbDistance, limbAngle, tickDelta, alpha);
            upperTorso.render(stack, vertices, overlayUv, lightUv, limbDistance, limbAngle, tickDelta, alpha);
        } else {
            chestPiece.render(stack, vertices, overlayUv, lightUv, limbDistance, limbAngle, tickDelta, alpha);
        }
    }

    @Override
    protected void renderLegs(MatrixStack stack, VertexConsumer vertices, int overlayUv, int lightUv, float red, float green, float blue, float alpha) {
        super.renderLegs(stack, vertices, overlayUv, lightUv, red, green, blue, alpha);
        steveLeftLeg.render(stack, vertices, overlayUv, lightUv, red, green, blue, alpha);
        steveRightLeg.render(stack, vertices, overlayUv, lightUv, red, green, blue, alpha);
    }

    @Override
    public void setVariant(ArmourVariant variant) {
        this.variant = variant;
    }

    @Override
    public void synchroniseAngles(IModel model) {
        if (model instanceof BipedEntityModel) {
            @SuppressWarnings("unchecked")
            BipedEntityModel<T> mainModel = (BipedEntityModel<T>)model;
            head.copyTransform(mainModel.head);
            head.copyTransform(mainModel.head);

            body.copyTransform(mainModel.body);
            rightArm.copyTransform(mainModel.rightArm);
            leftArm.copyTransform(mainModel.leftArm);
            rightLeg.copyTransform(mainModel.rightLeg);
            leftLeg.copyTransform(mainModel.leftLeg);

            steveLeftLeg.copyTransform(mainModel.leftLeg);
            steveRightLeg.copyTransform(mainModel.rightLeg);
        }
    }

    @Override
    public void setInVisible() {
        setVisible(false);
        body.visible = true;
        chestPiece.visible = false;
        head.visible = false;
        neck.visible = false;
        upperTorso.visible = false;
        steveLeftLeg.visible = false;
        steveRightLeg.visible = false;
    }

    @Override
    public void showBoots() {
        rightArm.visible = true;
        leftArm.visible = true;
        rightLeg.visible = variant == ArmourVariant.NORMAL;
        leftLeg.visible = variant == ArmourVariant.NORMAL;
        steveLeftLeg.visible = variant == ArmourVariant.LEGACY;
        steveRightLeg.visible = variant == ArmourVariant.LEGACY;
    }

    @Override
    public void showLeggings() {
        showBoots();
    }

    @Override
    public void showChestplate() {
        chestPiece.visible = true;
        neck.visible = true;
    }

    @Override
    public void showSaddle() {
        chestPiece.visible = true;
        neck.visible = true;

        if (variant == ArmourVariant.LEGACY) {
            upperTorso.visible = true;
        }
    }

    @Override
    public void showHelmet() {
        head.visible = true;
    }
}
