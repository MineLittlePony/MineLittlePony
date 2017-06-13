package com.minelittlepony.hdskins.gui;

import com.minelittlepony.MineLittlePony;
import com.minelittlepony.Pony;
import com.minelittlepony.model.AbstractPonyModel;
import com.minelittlepony.model.BodyPart;
import com.minelittlepony.model.ModelPonyElytra;
import com.minelittlepony.model.PlayerModel;
import com.minelittlepony.renderer.layer.AbstractPonyLayer;
import com.voxelmodpack.hdskins.gui.RenderPlayerModel;
import net.minecraft.client.model.ModelPlayer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.entity.layers.LayerRenderer;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.Items;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;

public class RenderPonyModel extends RenderPlayerModel<EntityPonyModel> {

    public RenderPonyModel(RenderManager renderer) {
        super(renderer);
    }

    @Override
    public ModelPlayer getEntityModel(EntityPonyModel playermodel) {
        Pony thePony = MineLittlePony.getInstance().getManager().getPonyFromResourceRegistry(this.getEntityTexture(playermodel));
        thePony.invalidateSkinCheck();
        thePony.checkSkin();

        // TODO small arms
        PlayerModel pm = thePony.getModel(true, false);
        pm.apply(thePony.metadata);

        return pm.getModel();
    }

    @Override
    protected LayerRenderer<EntityLivingBase> getElytraLayer() {
        final LayerRenderer<EntityLivingBase> elytra = super.getElytraLayer();
        final ModelPonyElytra modelElytra = new ModelPonyElytra();
        return new AbstractPonyLayer<EntityLivingBase>(this, elytra) {

            @Override
            public void doPonyRender(EntityLivingBase entityBase, float swing, float swingAmount, float ticks, float age, float yaw, float head,
                    float scale) {

                EntityPonyModel entity = (EntityPonyModel) entityBase;

                ItemStack itemstack = entity.getItemStackFromSlot(EntityEquipmentSlot.CHEST);

                if (itemstack.getItem() == Items.ELYTRA) {
                    GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);

                    bindTexture(entity.getElytraTexture());

                    GlStateManager.pushMatrix();
                    GlStateManager.translate(0.0F, 0.25F, 0.125F);
                    ((AbstractPonyModel) mainModel).transform(BodyPart.BODY);

                    modelElytra.setRotationAngles(swing, swingAmount, age, yaw, head, scale, entity);
                    modelElytra.render(entity, swing, swingAmount, age, yaw, head, scale);


                    GlStateManager.popMatrix();
                }
            }

        };
    }
}
