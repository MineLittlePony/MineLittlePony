package com.minelittlepony.hdskins.gui;

import com.minelittlepony.MineLittlePony;
import com.minelittlepony.model.ModelWrapper;
import com.minelittlepony.model.capabilities.IModel;
import com.minelittlepony.model.components.PonyElytra;
import com.minelittlepony.model.player.PlayerModels;
import com.minelittlepony.pony.data.Pony;
import com.minelittlepony.pony.data.PonyRace;
import com.minelittlepony.render.layer.AbstractPonyLayer;
import com.mojang.authlib.minecraft.MinecraftProfileTexture.Type;
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

    public RenderPonyModel(RenderManager manager) {
        super(manager);
    }

    @Override
    public ModelPlayer getEntityModel(EntityPonyModel playermodel) {
        renderingAsHuman = true;

        ResourceLocation loc = getEntityTexture(playermodel);
        if (loc == null) {
            return super.getEntityModel(playermodel);
        }

        boolean slim = playermodel.usesThinSkin();

        Pony thePony = MineLittlePony.getInstance().getManager().getPony(loc, slim);

        PonyRace race = thePony.getRace(false);

        if (race.isHuman()) {
            return super.getEntityModel(playermodel);
        }

        boolean canWet = playermodel.wet && (loc == playermodel.getBlankSkin(Type.SKIN) || race == PonyRace.SEAPONY);

        ModelWrapper pm = canWet ? PlayerModels.SEAPONY.getModel(slim) : thePony.getModel(true);
        pm.apply(thePony.getMetadata());

        renderingAsHuman = false;

        return pm.getBody();
    }

    @Override
    protected LayerRenderer<EntityLivingBase> getElytraLayer() {
        return new AbstractPonyLayer<EntityPonyModel>(this) {
            final PonyElytra ponyElytra = new PonyElytra();
            final ModelElytra modelElytra = new ModelElytra();

            @Override
            public void doPonyRender(EntityPonyModel entity, float move, float swing, float partialTicks, float ticks, float headYaw, float headPitch, float scale) {
                ItemStack itemstack = entity.getItemStackFromSlot(EntityEquipmentSlot.CHEST);

                if (itemstack.getItem() == Items.ELYTRA) {
                    GlStateManager.color(1, 1, 1, 1);

                    bindTexture(entity.getLocal(Type.ELYTRA).getTexture());

                    GlStateManager.pushMatrix();

                    ModelBase model = renderingAsHuman ? modelElytra : ponyElytra;

                    if (!renderingAsHuman) {
                        GlStateManager.translate(0, ((IModel)getMainModel()).getRiderYOffset(), 0.125F);
                    }

                    model.setRotationAngles(move, swing, ticks, headYaw, headPitch, scale, entity);
                    model.render(entity, move, swing, ticks, headYaw, headPitch, scale);

                    GlStateManager.popMatrix();
                }
            }

        };
    }
}
