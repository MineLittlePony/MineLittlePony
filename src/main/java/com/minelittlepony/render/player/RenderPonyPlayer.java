package com.minelittlepony.render.player;

import java.util.Map;
import java.util.Optional;

import com.minelittlepony.MineLittlePony;
import com.minelittlepony.PonyConfig;
import com.minelittlepony.ducks.IRenderPony;
import com.minelittlepony.model.AbstractPonyModel;
import com.minelittlepony.model.ModelWrapper;
import com.minelittlepony.model.components.ModelDeadMau5Ears;
import com.minelittlepony.pony.data.Pony;
import com.minelittlepony.pony.data.PonyLevel;
import com.minelittlepony.render.PonySkull;
import com.minelittlepony.render.PonySkullRenderer.ISkull;
import com.minelittlepony.render.layer.LayerEntityOnPonyShoulder;
import com.minelittlepony.render.layer.LayerHeldPonyItemMagical;
import com.minelittlepony.render.layer.LayerPonyArmor;
import com.minelittlepony.render.layer.LayerPonyCape;
import com.minelittlepony.render.layer.LayerPonyCustomHead;
import com.minelittlepony.render.layer.LayerPonyElytra;
import com.minelittlepony.transform.PonyPosture;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.minecraft.MinecraftProfileTexture;
import com.mojang.authlib.minecraft.MinecraftProfileTexture.Type;
import com.voxelmodpack.hdskins.HDSkinManager;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.RenderManager;

import net.minecraft.client.resources.DefaultPlayerSkin;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.client.renderer.entity.RenderPlayer;
import net.minecraft.client.renderer.entity.layers.LayerArrow;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;

public class RenderPonyPlayer extends RenderPlayer implements IRenderPony {

    public static final ISkull SKULL = new PonySkull() {

        private final ModelDeadMau5Ears deadMau5 = new ModelDeadMau5Ears();

        @Override
        public boolean canRender(PonyConfig config) {
            return config.getPonyLevel() != PonyLevel.HUMANS;
        }

        @Override
        public void preRender(boolean transparency) {
            GlStateManager.enableBlendProfile(GlStateManager.Profile.PLAYER_SKIN);
        }

        @Override
        public ResourceLocation getSkinResource(GameProfile profile) {
            if (profile != null) {
                deadMau5.setVisible("deadmau5".equals(profile.getName()));

                Optional<ResourceLocation> skin = HDSkinManager.INSTANCE.getSkinLocation(profile, Type.SKIN, true);
                if (skin.isPresent()) {
                    return skin.get();
                }

                Minecraft minecraft = Minecraft.getMinecraft();
                Map<Type, MinecraftProfileTexture> map = minecraft.getSkinManager().loadSkinFromCache(profile);

                if (map.containsKey(Type.SKIN)) {
                    return minecraft.getSkinManager().loadSkin(map.get(Type.SKIN), Type.SKIN);
                } else {
                    return DefaultPlayerSkin.getDefaultSkin(EntityPlayer.getUUID(profile));
                }
            }

            return DefaultPlayerSkin.getDefaultSkinLegacy();
        }

        @Override
        public void render(float animateTicks, float rotation, float scale) {
            super.render(animateTicks, rotation, scale);
            deadMau5.render(null, animateTicks, 0, 0, rotation, 0, scale);
        }
    }.register(ISkull.PLAYER);

    private ModelWrapper playerModel;

    protected AbstractPonyModel ponyModel;

    protected Pony pony;

    public RenderPonyPlayer(RenderManager manager, boolean useSmallArms, ModelWrapper model) {
        super(manager, useSmallArms);

        setPonyModel(model);

        layerRenderers.clear();
        addLayers();
    }

    protected void addLayers() {
        addLayer(new LayerPonyArmor<>(this));
        addLayer(new LayerArrow(this));
        addLayer(new LayerPonyCustomHead<>(this));
        addLayer(new LayerPonyElytra<>(this));
        addLayer(new LayerHeldPonyItemMagical<>(this));
        addLayer(new LayerPonyCape(this));
        addLayer(new LayerEntityOnPonyShoulder(renderManager, this));
    }

    @Override
    public float prepareScale(AbstractClientPlayer player, float ticks) {

        if (!player.isRiding()) {
            float x = player.width/2;
            float y = 0;

            if (player.isSneaking()) {
                // Sneaking makes the player 1/15th shorter.
                // This should be compatible with height-changing mods.
                y += player.height / 15;
            }

            super.doRenderShadowAndFire(player, 0, y, x, 0, ticks);
        }

        return super.prepareScale(player, ticks);
    }

    @Override
    protected void preRenderCallback(AbstractClientPlayer player, float ticks) {
        updateModel(player);

        ponyModel.updateLivingState(player, pony);

        super.preRenderCallback(player, ticks);
        shadowSize = getShadowScale();

        float s = getScaleFactor();
        GlStateManager.scale(s, s, s);

        if (player.isRiding()) {
            GlStateManager.translate(0, player.getYOffset(), 0);
        }
    }

    @Override
    public void doRenderShadowAndFire(Entity player, double x, double y, double z, float yaw, float ticks) {
        if (player.isRiding()) {
            super.doRenderShadowAndFire(player, x, y, z, yaw, ticks);
        }
    }

    @Override
    public void renderRightArm(AbstractClientPlayer player) {
        updateModel(player);
        bindEntityTexture(player);
        GlStateManager.pushMatrix();
        GlStateManager.translate(0, -0.37, 0);
        super.renderRightArm(player);
        GlStateManager.popMatrix();
    }

    @Override
    public void renderLeftArm(AbstractClientPlayer player) {
        updateModel(player);
        bindEntityTexture(player);
        GlStateManager.pushMatrix();
        GlStateManager.translate(0.06, -0.37, 0);
        super.renderLeftArm(player);
        GlStateManager.popMatrix();
    }

    @SuppressWarnings("unchecked")
    @Override
    protected void applyRotations(AbstractClientPlayer player, float yaw, float pitch, float ticks) {
        super.applyRotations(player, yaw, pitch, ticks);

        PonyPosture<?> posture = getPosture(player);
        if (posture != null && posture.applies(player)) {
            double motionX = player.posX - player.prevPosX;
            double motionY = player.onGround ? 0 : player.posY - player.prevPosY;
            double motionZ = player.posZ - player.prevPosZ;
            ((PonyPosture<EntityLivingBase>)posture).transform(getModelWrapper().getBody(), player, motionX, motionY, motionZ, pitch, yaw, ticks);
        }
    }

    protected PonyPosture<?> getPosture(EntityLivingBase entity) {
        if (entity.isElytraFlying()) {
            return PonyPosture.ELYTRA;
        }

        if (entity.isEntityAlive() && entity.isPlayerSleeping()) return null;

        if (getModelWrapper().getBody().isSwimming()) {
            return PonyPosture.SWIMMING;
        }

        if (getModelWrapper().getBody().isGoingFast()) {
            return PonyPosture.FLIGHT;
        }

        return PonyPosture.FALLING;
    }

    @Override
    public ResourceLocation getEntityTexture(AbstractClientPlayer player) {
        updateModel(player);
        return pony.getTexture();
    }

    @Override
    public ModelWrapper getModelWrapper() {
        return playerModel;
    }

    protected void setPonyModel(ModelWrapper model) {
        playerModel = model;
        mainModel = ponyModel = playerModel.getBody();
    }

    protected void updatePony(AbstractClientPlayer player) {
        pony = MineLittlePony.getInstance().getManager().getPony(player);
    }

    protected void updateModel(AbstractClientPlayer player) {
        updatePony(player);
        getModelWrapper().apply(pony.getMetadata());
    }

    @Override
    public float getShadowScale() {
        return pony.getMetadata().getSize().getShadowSize();
    }

    @Override
    public float getScaleFactor() {
        return pony.getMetadata().getSize().getScaleFactor();
    }
}
