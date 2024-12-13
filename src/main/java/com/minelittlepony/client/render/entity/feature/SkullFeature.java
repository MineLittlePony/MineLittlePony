package com.minelittlepony.client.render.entity.feature;

import com.minelittlepony.api.model.BodyPart;
import com.minelittlepony.client.model.ClientPonyModel;
import com.minelittlepony.client.model.armour.ArmourRendererPlugin;
import com.minelittlepony.client.render.PonyRenderContext;
import com.minelittlepony.client.render.blockentity.skull.PonySkullRenderer;
import com.minelittlepony.client.render.entity.state.PonyRenderState;

import net.minecraft.block.AbstractSkullBlock;
import net.minecraft.client.render.*;
import net.minecraft.client.render.entity.feature.HeadFeatureRenderer;
import net.minecraft.client.render.entity.model.EntityModelLoader;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.render.model.BakedModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.EquippableComponent;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.item.*;
import net.minecraft.item.equipment.EquipmentModel;

public class SkullFeature<
        S extends PonyRenderState,
        M extends ClientPonyModel<S>
    > extends AbstractPonyFeature<S, M> {
    private final ItemRenderer itemRenderer;

    private final HeadFeatureRenderer.HeadTransformation headTransformation;

    private final boolean scaleForChild;

    public SkullFeature(PonyRenderContext<?, S, M> context, EntityModelLoader entityModelLoader, ItemRenderer itemRenderer,
            HeadFeatureRenderer.HeadTransformation headTransformation, boolean scaleForChild) {
        super(context);
        this.itemRenderer = itemRenderer;
        this.headTransformation = headTransformation;
        this.scaleForChild = scaleForChild;
    }

    @Override
    public void render(MatrixStack matrices, VertexConsumerProvider provider, int light, S state, float limbAngle, float limbDistance) {
        ArmourRendererPlugin plugin = ArmourRendererPlugin.INSTANCE.get();

        for (ItemStack stack : plugin.getArmorStacks(state, EquipmentSlot.HEAD, EquipmentModel.LayerType.HUMANOID, ArmourRendererPlugin.ArmourType.SKULL)) {

            BakedModel headModel = state.equippedHeadItemModel;

            if (stack.isEmpty() || headModel == null) {
                continue;
            }

            M model = getModelWrapper().body();
            Item item = stack.getItem();
            EquippableComponent equipable = stack.get(DataComponentTypes.EQUIPPABLE);

            matrices.push();

            if (state.baby && scaleForChild) {
                matrices.translate(0, 0.03125F, 0);
                matrices.scale(0.7F, 0.7F, 0.7F);
                matrices.translate(0, 1, 0);
            }

            model.transform(state, BodyPart.HEAD, matrices);
            model.getHead().rotate(matrices);

            float f = 1.1F;
            matrices.scale(f, f, f);

            if (item instanceof BlockItem b && b.getBlock() instanceof AbstractSkullBlock) {
                float n = 1.1875F;
                matrices.scale(n, -n, -n);
                matrices.translate(0, -0.1F, 0.1F);
                matrices.translate(-0.5, 0, -0.5);
                PonySkullRenderer.INSTANCE.renderSkull(matrices, provider, stack, state, state.age, light, true);
            } else if (equipable != null && equipable.slot() != EquipmentSlot.HEAD) {
                matrices.translate(0, 0.1F, -0.1F);
                HeadFeatureRenderer.translate(matrices, headTransformation);
                itemRenderer.renderItem(stack, ModelTransformationMode.HEAD, false, matrices, provider, light, OverlayTexture.DEFAULT_UV, headModel);
            }

            matrices.pop();
        }

        plugin.onArmourRendered(state, matrices, provider, EquipmentSlot.BODY, EquipmentModel.LayerType.HUMANOID, ArmourRendererPlugin.ArmourType.SKULL);
    }
}
