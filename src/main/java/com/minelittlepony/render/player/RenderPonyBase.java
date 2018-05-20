package com.minelittlepony.render.player;

import com.minelittlepony.MineLittlePony;
import com.minelittlepony.ducks.IRenderPony;
import com.minelittlepony.model.AbstractPonyModel;
import com.minelittlepony.model.ModelWrapper;
import com.minelittlepony.pony.data.Pony;
import com.minelittlepony.render.layer.LayerEntityOnPonyShoulder;
import com.minelittlepony.render.layer.LayerHeldPonyItemMagical;
import com.minelittlepony.render.layer.LayerPonyArmor;
import com.minelittlepony.render.layer.LayerPonyCape;
import com.minelittlepony.render.layer.LayerPonyCustomHead;
import com.minelittlepony.render.layer.LayerPonyElytra;

import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.entity.RenderPlayer;
import net.minecraft.client.renderer.entity.layers.LayerArrow;
import net.minecraft.util.ResourceLocation;

public abstract class RenderPonyBase extends RenderPlayer implements IRenderPony {

  private ModelWrapper playerModel;

  protected AbstractPonyModel ponyModel;

  private Pony pony;

  public RenderPonyBase(RenderManager manager, boolean useSmallArms, ModelWrapper model) {
      super(manager, useSmallArms);

      setPlayerModel(model);

      layerRenderers.clear();
      addExtraLayers();
  }

  protected void addExtraLayers() {
      addLayer(new LayerPonyArmor<>(this));
      addLayer(new LayerHeldPonyItemMagical<>(this));
      addLayer(new LayerArrow(this));
      addLayer(new LayerPonyCape(this));
      addLayer(new LayerPonyCustomHead<>(this));
      addLayer(new LayerPonyElytra<>(this));
      addLayer(new LayerEntityOnPonyShoulder(renderManager, this));
  }

  @Override
  protected void preRenderCallback(AbstractClientPlayer player, float ticks) {
      updateModel(player);

      ponyModel.isSneak = player.isSneaking();
      ponyModel.isSleeping = player.isPlayerSleeping();
      ponyModel.isFlying = pony.isPegasusFlying(player);

      super.preRenderCallback(player, ticks);
      shadowSize = getShadowScale();

      float s = getScaleFactor();
      GlStateManager.scale(s, s, s);

      GlStateManager.translate(0, 0, -player.width / 2); // move us to the center of the shadow
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

  @Override
  protected void applyRotations(AbstractClientPlayer player, float yaw, float pitch, float ticks) {
      super.applyRotations(player, yaw, pitch, ticks);

      double motionX = player.posX - player.prevPosX;
      double motionY = player.onGround ? 0 : player.posY - player.prevPosY;
      double motionZ = player.posZ - player.prevPosZ;

      if (player.isElytraFlying()) {
        transformElytraFlight(player, motionX, motionY, motionZ, ticks);
        return;
      }

      if (player.isEntityAlive() && player.isPlayerSleeping()) return;

      if (ponyModel.rainboom) {
          transformPegasusFlight(player, motionX, motionY, motionZ, yaw, pitch, ticks);
          return;
      }

      // require arms to be stretched out (sorry mud ponies, no flight skills for you)
      ponyModel.motionPitch = 0;
  }

  @Override
  public ResourceLocation getEntityTexture(AbstractClientPlayer player) {
      updateModel(player);
      return pony.getTexture();
  }

  @Override
  public ModelWrapper getPlayerModel() {
      return playerModel;
  }

  protected void setPlayerModel(ModelWrapper model) {
      playerModel = model;
      mainModel = ponyModel = playerModel.getModel();
  }

  protected void updateModel(AbstractClientPlayer player) {
      pony = MineLittlePony.getInstance().getManager().getPony(player);
      playerModel.apply(pony.getMetadata());
  }

  public Pony getPony() {
      return pony;
  }

  protected abstract void transformElytraFlight(AbstractClientPlayer player, double motionX, double motionY, double motionZ, float ticks);

  protected abstract void transformPegasusFlight(AbstractClientPlayer player, double motionX, double motionY, double motionZ, float yaw, float pitch, float ticks);
}
