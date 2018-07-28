package com.minelittlepony.hdskins.gui;

import com.minelittlepony.MineLittlePony;
import com.minelittlepony.model.ModelWrapper;
import com.minelittlepony.model.player.PlayerModels;
import com.minelittlepony.pony.data.Pony;
import com.minelittlepony.pony.data.PonyRace;
import com.minelittlepony.render.layer.LayerPonyElytra;
import com.voxelmodpack.hdskins.gui.RenderPlayerModel;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelElytra;
import net.minecraft.client.model.ModelPlayer;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.entity.layers.LayerRenderer;
import net.minecraft.entity.EntityLivingBase;
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

        boolean canWet = playermodel.wet && (loc == playermodel.getBlankSkin() || race == PonyRace.SEAPONY);

        ModelWrapper pm = canWet ? PlayerModels.SEAPONY.getModel(slim) : thePony.getModel(true);
        pm.apply(thePony.getMetadata());

        renderingAsHuman = false;

        return pm.getBody();
    }

    @Override
    protected LayerRenderer<EntityLivingBase> getElytraLayer() {
        return new LayerPonyElytra<EntityPonyModel>(this) {
            private final ModelElytra modelElytra = new ModelElytra();

            @Override
            protected void preRenderCallback() {
                if (!renderingAsHuman) {
                    super.preRenderCallback();
                }
            }

            @Override
            protected ModelBase getElytraModel() {
                return renderingAsHuman ? modelElytra : super.getElytraModel();
            }

            @Override
            protected ResourceLocation getElytraTexture(EntityPonyModel entity) {
                return entity.getElytraTexture();
            }
        };
    }
}
