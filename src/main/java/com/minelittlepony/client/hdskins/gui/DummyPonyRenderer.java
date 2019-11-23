package com.minelittlepony.client.hdskins.gui;

import com.minelittlepony.client.MineLittlePony;
import com.minelittlepony.client.model.ClientPonyModel;
import com.minelittlepony.client.model.ModelWrapper;
import com.minelittlepony.client.model.races.PlayerModels;
import com.minelittlepony.client.render.IPonyRender;
import com.minelittlepony.client.render.RenderPony;
import com.minelittlepony.client.render.layer.LayerGear;
import com.minelittlepony.client.render.layer.LayerHeldPonyItemMagical;
import com.minelittlepony.client.render.layer.LayerPonyArmor;
import com.minelittlepony.client.render.layer.LayerPonyElytra;
import com.minelittlepony.hdskins.dummy.DummyPlayerRenderer;
import com.minelittlepony.hdskins.profile.SkinType;
import com.minelittlepony.pony.IPony;
import com.minelittlepony.pony.meta.Race;

import net.fabricmc.fabric.api.client.rendereregistry.v1.EntityRendererRegistry;
import net.minecraft.client.render.entity.EntityRenderDispatcher;
import net.minecraft.client.render.entity.feature.FeatureRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;

/**
 * Renderer used for the dummy pony model when selecting a skin.
 */
public class DummyPonyRenderer extends DummyPlayerRenderer<DummyPony, ClientPonyModel<DummyPony>> implements IPonyRender<DummyPony, ClientPonyModel<DummyPony>> {

    protected final RenderPony<DummyPony, ClientPonyModel<DummyPony>> renderPony = new RenderPony<>(this);

    public DummyPonyRenderer(EntityRenderDispatcher manager, EntityRendererRegistry.Context context) {
        super(manager, context);
        addFeature(new LayerGear<>(this));

        renderPony.setSkipBlend();
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
    protected void scale(DummyPony entity, MatrixStack stack, float ticks) {
        renderPony.preRenderCallback(entity, stack, ticks);

        if (entity.isSwimming()) {
            if (entity.getVelocity().x < 100) {
                entity.addVelocity(100, 0, 0);
            }

            model.getAttributes().motionPitch = 70;
        } else {
            model.getAttributes().motionPitch = 0;
        }

        if (entity.hasVehicle()) {
            stack.translate(0, entity.getHeightOffset(), 0);
        }
    }

    @Override
    public ClientPonyModel<DummyPony> getEntityModel(DummyPony playermodel) {
        Identifier loc = getTexture(playermodel);

        boolean slim = playermodel.getTextures().usesThinSkin();

        IPony thePony = MineLittlePony.getInstance().getManager().getPony(loc);

        Race race = thePony.getRace(false);

        boolean canWet = playermodel.wet && (loc == playermodel.getTextures().getBlankSkin(SkinType.SKIN) || race == Race.SEAPONY);

        playerModel = canWet ? PlayerModels.SEAPONY.getWrappedModel(slim) : PlayerModels.forRace(thePony.getRace(true)).getWrappedModel(slim);
        playerModel.apply(thePony.getMetadata());

        renderPony.setPonyModel(playerModel);

        return playerModel.getBody();
    }

    @Override
    protected FeatureRenderer<DummyPony, ClientPonyModel<DummyPony>> getArmourLayer() {
        return new LayerPonyArmor<>(this);
    }

    @Override
    protected FeatureRenderer<DummyPony, ClientPonyModel<DummyPony>> getHeldItemLayer() {
        return new LayerHeldPonyItemMagical<>(this);
    }

    @Override
    protected FeatureRenderer<DummyPony, ClientPonyModel<DummyPony>> getElytraLayer() {
        return new LayerPonyElytra<DummyPony, ClientPonyModel<DummyPony>>(this) {
            @Override
            protected Identifier getElytraTexture(DummyPony entity) {
                return entity.getTextures().get(SkinType.ELYTRA).getId();
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
