package com.minelittlepony.client.hdskins;

import com.minelittlepony.client.MineLittlePony;
import com.minelittlepony.client.model.ClientPonyModel;
import com.minelittlepony.client.model.ModelType;
import com.minelittlepony.client.model.ModelWrapper;
import com.minelittlepony.client.render.IPonyRenderContext;
import com.minelittlepony.client.render.EquineRenderManager;
import com.minelittlepony.client.render.entity.feature.GearFeature;
import com.minelittlepony.client.render.entity.feature.GlowingItemFeature;
import com.minelittlepony.client.render.entity.feature.ArmourFeature;
import com.minelittlepony.client.render.entity.feature.ElytraFeature;
import com.minelittlepony.hdskins.client.dummy.DummyPlayerRenderer;
import com.minelittlepony.hdskins.profile.SkinType;
import com.minelittlepony.mson.api.ModelKey;
import com.minelittlepony.pony.IPony;
import com.minelittlepony.pony.meta.Race;

import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRenderDispatcher;
import net.minecraft.client.render.entity.feature.FeatureRenderer;
import net.minecraft.client.render.entity.model.PlayerEntityModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;

/**
 * Renderer used for the dummy pony model when selecting a skin.
 */
class DummyPonyRenderer extends DummyPlayerRenderer<DummyPony, ClientPonyModel<DummyPony>> implements IPonyRenderContext<DummyPony, ClientPonyModel<DummyPony>> {

    protected final EquineRenderManager<DummyPony, ClientPonyModel<DummyPony>> manager = new EquineRenderManager<>(this);

    public DummyPonyRenderer(EntityRenderDispatcher dispatcher) {
        super(dispatcher, null);
        addFeature(new GearFeature<>(this));

        manager.setModel(ModelType.EARTH_PONY.getKey(false));
        manager.setSkipBlend();
    }

    @Override
    public ModelWrapper<DummyPony, ClientPonyModel<DummyPony>> getModelWrapper() {
        return manager.playerModel;
    }

    @Override
    public IPony getEntityPony(DummyPony entity) {
        return MineLittlePony.getInstance().getManager().getPony(getTexture(entity));
    }

    @Override
    protected void scale(DummyPony entity, MatrixStack stack, float tickDelta) {
        manager.preRenderCallback(entity, stack, tickDelta);
        if (getModel() instanceof PlayerEntityModel) {
            ((PlayerEntityModel<?>)getModel()).setVisible(true);
        }

        if (entity.isSwimming()) {
            if (entity.getVelocity().x < 100) {
                entity.addVelocity(100, 0, 0);
            }

            model.getAttributes().motionPitch = 70;
        } else {
            model.getAttributes().motionPitch = 0;
        }

        if (getModel().getAttributes().isSitting) {
            stack.translate(0, entity.getHeightOffset(), 0);
        }
    }

    @Override
    public void render(DummyPony entity, float entityYaw, float tickDelta, MatrixStack stack, VertexConsumerProvider renderContext, int lightUv) {
        super.render(entity, entityYaw, tickDelta, stack, renderContext, lightUv);
    }

    @Override
    public ClientPonyModel<DummyPony> getEntityModel(DummyPony playermodel) {
        Identifier loc = getTexture(playermodel);

        boolean slim = playermodel.getTextures().usesThinSkin();

        IPony thePony = MineLittlePony.getInstance().getManager().getPony(loc);

        Race race = thePony.getRace(false);

        boolean canWet = playermodel.wet && (loc == playermodel.getTextures().getBlankSkin(SkinType.SKIN) || race == Race.SEAPONY);


        @SuppressWarnings("unchecked")
        ModelKey<? extends ClientPonyModel<DummyPony>> key = (ModelKey<? extends ClientPonyModel<DummyPony>>)(canWet ? ModelType.SEA_PONY.getKey(slim) : ModelType.getPlayerModel(thePony.getRace(true)).getKey(slim));

        return manager.setModel(key).apply(thePony.getMetadata()).getBody();
    }

    @Override
    protected FeatureRenderer<DummyPony, ClientPonyModel<DummyPony>> getArmourLayer() {
        return new ArmourFeature<>(this);
    }

    @Override
    protected FeatureRenderer<DummyPony, ClientPonyModel<DummyPony>> getHeldItemLayer() {
        return new GlowingItemFeature<>(this);
    }

    @Override
    protected FeatureRenderer<DummyPony, ClientPonyModel<DummyPony>> getElytraLayer() {
        return new ElytraFeature<DummyPony, ClientPonyModel<DummyPony>>(this) {
            @Override
            protected Identifier getElytraTexture(DummyPony entity) {
                return entity.getTextures().get(SkinType.ELYTRA).getId();
            }
        };
    }

    @Override
    public EquineRenderManager<DummyPony, ClientPonyModel<DummyPony>> getInternalRenderer() {
        return manager;
    }

    @Override
    public Identifier findTexture(DummyPony entity) {
        return getTexture(entity);
    }
}
