package com.minelittlepony.client.gui.hdskins;

import com.minelittlepony.MineLittlePony;
import com.minelittlepony.client.model.ClientPonyModel;
import com.minelittlepony.client.model.ModelWrapper;
import com.minelittlepony.client.model.races.PlayerModels;
import com.minelittlepony.client.pony.Pony;
import com.minelittlepony.client.render.IPonyRender;
import com.minelittlepony.client.render.RenderPony;
import com.minelittlepony.client.render.layer.LayerGear;
import com.minelittlepony.client.render.layer.LayerPonyElytra;
import com.minelittlepony.hdskins.gui.RenderPlayerModel;
import com.minelittlepony.pony.IPony;
import com.minelittlepony.pony.meta.Race;
import com.mojang.authlib.minecraft.MinecraftProfileTexture.Type;
import com.mojang.blaze3d.platform.GlStateManager;

import net.minecraft.client.render.entity.EntityRenderDispatcher;
import net.minecraft.client.render.entity.feature.FeatureRenderer;
import net.minecraft.client.render.entity.model.ElytraEntityModel;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.util.Identifier;

/**
 * Renderer used for the dummy pony model when selecting a skin.
 */
public class RenderPonyModel extends RenderPlayerModel<EntityPonyModel, ClientPonyModel<EntityPonyModel>> implements IPonyRender<EntityPonyModel, ClientPonyModel<EntityPonyModel>> {

    boolean renderingAsHuman = false;

    protected final RenderPony<EntityPonyModel, ClientPonyModel<EntityPonyModel>> renderPony = new RenderPony<>(this);

    public RenderPonyModel(EntityRenderDispatcher manager) {
        super(manager);
        addFeature(new LayerGear<>(this));
    }

    private ModelWrapper<EntityPonyModel, ClientPonyModel<EntityPonyModel>> playerModel;

    @Override
    public ModelWrapper<EntityPonyModel, ClientPonyModel<EntityPonyModel>> getModelWrapper() {
        return playerModel;
    }

    @Override
    public IPony getEntityPony(EntityPonyModel entity) {
        return MineLittlePony.getInstance().getManager().getPony(getTexture(entity));
    }

    @Override
    protected void scale(EntityPonyModel entity, float ticks) {
        if (renderingAsHuman) {
            super.scale(entity, ticks);
        } else {
            renderPony.preRenderCallback(entity, ticks);

            GlStateManager.translatef(0, 0, -entity.getWidth() / 2); // move us to the center of the shadow
        }
    }

    @SuppressWarnings("unchecked")
    @Override
    public ClientPonyModel<EntityPonyModel> getEntityModel(EntityPonyModel playermodel) {
        renderingAsHuman = true;

        Identifier loc = getTexture(playermodel);
        if (loc == null || Pony.getBufferedImage(loc) == null) {
            return super.getEntityModel(playermodel);
        }

        boolean slim = playermodel.usesThinSkin();

        IPony thePony = MineLittlePony.getInstance().getManager().getPony(loc);

        Race race = thePony.getRace(false);

        if (race.isHuman()) {
            return super.getEntityModel(playermodel);
        }

        boolean canWet = playermodel.wet && (loc == playermodel.getBlankSkin(Type.SKIN) || race == Race.SEAPONY);

        playerModel = canWet ? PlayerModels.SEAPONY.getModel(slim) : PlayerModels.forRace(thePony.getRace(true)).getModel(slim);
        playerModel.apply(thePony.getMetadata());

        renderPony.setPonyModel(playerModel);

        renderingAsHuman = false;

        return playerModel.getBody();
    }

    @Override
    protected FeatureRenderer<EntityPonyModel, ClientPonyModel<EntityPonyModel>> getElytraLayer() {
        return new LayerPonyElytra<EntityPonyModel, ClientPonyModel<EntityPonyModel>>(this) {
            private final ElytraEntityModel<EntityPonyModel> modelElytra = new ElytraEntityModel<>();

            @Override
            protected void preRenderCallback() {
                if (!renderingAsHuman) {
                    super.preRenderCallback();
                }
            }

            @Override
            protected EntityModel<EntityPonyModel> getElytraModel() {
                return renderingAsHuman ? modelElytra : super.getElytraModel();
            }

            @Override
            protected Identifier getElytraTexture(EntityPonyModel entity) {
                return entity.getLocal(Type.ELYTRA).getTexture();
            }
        };
    }

    @Override
    public RenderPony<EntityPonyModel, ClientPonyModel<EntityPonyModel>> getInternalRenderer() {
        return renderPony;
    }

    @Override
    public Identifier findTexture(EntityPonyModel entity) {
        return getTexture(entity);
    }
}
