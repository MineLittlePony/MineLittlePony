package com.minelittlepony.client.gui.hdskins;

import com.minelittlepony.MineLittlePony;
import com.minelittlepony.client.model.ClientPonyModel;
import com.minelittlepony.client.model.ModelWrapper;
import com.minelittlepony.client.model.races.PlayerModels;
import com.minelittlepony.client.render.IPonyRender;
import com.minelittlepony.client.render.RenderPony;
import com.minelittlepony.client.render.layer.LayerGear;
import com.minelittlepony.client.render.layer.LayerPonyElytra;
import com.minelittlepony.hdskins.dummy.RenderDummyPlayer;
import com.minelittlepony.pony.IPony;
import com.minelittlepony.pony.meta.Race;
import com.mojang.authlib.minecraft.MinecraftProfileTexture.Type;
import com.mojang.blaze3d.platform.GlStateManager;

import net.minecraft.client.render.entity.EntityRenderDispatcher;
import net.minecraft.client.render.entity.feature.FeatureRenderer;
import net.minecraft.util.Identifier;

/**
 * Renderer used for the dummy pony model when selecting a skin.
 */
public class RenderDummyPony extends RenderDummyPlayer<DummyPony, ClientPonyModel<DummyPony>> implements IPonyRender<DummyPony, ClientPonyModel<DummyPony>> {

    protected final RenderPony<DummyPony, ClientPonyModel<DummyPony>> renderPony = new RenderPony<>(this);

    public RenderDummyPony(EntityRenderDispatcher manager) {
        super(manager);
        addFeature(new LayerGear<>(this));
    }

    private ModelWrapper<DummyPony, ClientPonyModel<DummyPony>> playerModel;

    @Override
    public ModelWrapper<DummyPony, ClientPonyModel<DummyPony>> getModelWrapper() {
        return playerModel;
    }

    @Override
    public IPony getEntityPony(DummyPony entity) {
        return MineLittlePony.getInstance().getManager().getPony(getTexture(entity));
    }

    @Override
    protected void scale(DummyPony entity, float ticks) {
        renderPony.preRenderCallback(entity, ticks);

        GlStateManager.translatef(0, 0, -entity.getWidth() / 2); // move us to the center of the shadow
    }

    @SuppressWarnings("unchecked")
    @Override
    public ClientPonyModel<DummyPony> getEntityModel(DummyPony playermodel) {
        Identifier loc = getTexture(playermodel);

        boolean slim = playermodel.getTextures().usesThinSkin();

        IPony thePony = MineLittlePony.getInstance().getManager().getPony(loc);

        Race race = thePony.getRace(false);

        boolean canWet = playermodel.wet && (loc == playermodel.getTextures().getBlankSkin(Type.SKIN) || race == Race.SEAPONY);

        playerModel = canWet ? PlayerModels.SEAPONY.getWrappedModel(slim) : PlayerModels.forRace(thePony.getRace(true)).getWrappedModel(slim);
        playerModel.apply(thePony.getMetadata());

        renderPony.setPonyModel(playerModel);

        return playerModel.getBody();
    }

    @Override
    protected FeatureRenderer<DummyPony, ClientPonyModel<DummyPony>> getElytraLayer() {
        return new LayerPonyElytra<DummyPony, ClientPonyModel<DummyPony>>(this) {
            @Override
            protected Identifier getElytraTexture(DummyPony entity) {
                return entity.getTextures().get(Type.ELYTRA).getId();
            }
        };
    }

    @Override
    public RenderPony<DummyPony, ClientPonyModel<DummyPony>> getInternalRenderer() {
        return renderPony;
    }

    @Override
    public Identifier findTexture(DummyPony entity) {
        return getTexture(entity);
    }
}
