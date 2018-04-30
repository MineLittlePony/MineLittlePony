package com.minelittlepony.hdskins.gui;

import com.minelittlepony.MineLittlePony;
import com.minelittlepony.PonyManager;
import com.minelittlepony.model.ModelWrapper;
import com.minelittlepony.model.components.PonyElytra;
import com.minelittlepony.pony.data.Pony;
import com.minelittlepony.render.layer.AbstractPonyLayer;
import com.voxelmodpack.hdskins.gui.RenderPlayerModel;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelElytra;
import net.minecraft.client.model.ModelPlayer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.entity.layers.LayerRenderer;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.Items;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

/**
 * Renderer used for the dummy pony model when selecting a skin.
 */
public class RenderPonyModel extends RenderPlayerModel<EntityPonyModel> {

    boolean renderingAsHuman = false;

    public RenderPonyModel(RenderManager renderer) {
        super(renderer);
    }

    @Override
    public ModelPlayer getEntityModel(EntityPonyModel playermodel) {
        renderingAsHuman = true;

        ResourceLocation loc = getEntityTexture(playermodel);
        if (loc == null) {
            return super.getEntityModel(playermodel);
        }

        // TODO: We can't find out whether to use thin arms just by the texture.
        //       Maybe a trigger pixel for thin arms? #FutureThoughts
        Pony thePony = MineLittlePony.getInstance().getManager().getPony(loc, PonyManager.isSlimSkin(playermodel.profile.getId()));

        if (thePony.getRace(false).isHuman()) {
            return super.getEntityModel(playermodel);
        }

        ModelWrapper pm = thePony.getModel(true);
        pm.apply(thePony.getMetadata());

        renderingAsHuman = false;

        return pm.getModel();
    }

    @Override
    protected LayerRenderer<EntityLivingBase> getElytraLayer() {
        return new AbstractPonyLayer<EntityPonyModel>(this) {
            final PonyElytra ponyElytra = new PonyElytra();
            final ModelElytra modelElytra = new ModelElytra();

            @Override
            public void doPonyRender(EntityPonyModel entity, float swing, float swingAmount, float ticks, float age, float yaw, float head, float scale) {
                ItemStack itemstack = entity.getItemStackFromSlot(EntityEquipmentSlot.CHEST);

                if (itemstack.getItem() == Items.ELYTRA) {
                    GlStateManager.color(1, 1, 1, 1);

                    bindTexture(entity.getElytraTexture());

                    GlStateManager.pushMatrix();

                    ModelBase model = renderingAsHuman ? modelElytra : ponyElytra;

                    if (!renderingAsHuman) {
                        GlStateManager.translate(0, 0.25F, 0.125F);
                    }

                    model.setRotationAngles(swing, swingAmount, age, yaw, head, scale, entity);
                    model.render(entity, swing, swingAmount, age, yaw, head, scale);

                    GlStateManager.popMatrix();
                }
            }

        };
    }
}
