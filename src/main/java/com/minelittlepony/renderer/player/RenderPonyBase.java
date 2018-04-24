package com.minelittlepony.renderer.player;

import com.minelittlepony.MineLittlePony;
import com.minelittlepony.Pony;
import com.minelittlepony.ducks.IRenderManager;
import com.minelittlepony.ducks.IRenderPony;
import com.minelittlepony.model.AbstractPonyModel;
import com.minelittlepony.model.PlayerModel;
import com.minelittlepony.model.pony.ModelPlayerPony;
import com.minelittlepony.renderer.layer.LayerEntityOnPonyShoulder;
import com.minelittlepony.renderer.layer.LayerHeldPonyItem;
import com.minelittlepony.renderer.layer.LayerPonyArmor;
import com.minelittlepony.renderer.layer.LayerPonyCape;
import com.minelittlepony.renderer.layer.LayerPonyCustomHead;
import com.minelittlepony.renderer.layer.LayerPonyElytra;

import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.entity.RenderPlayer;
import net.minecraft.client.renderer.entity.layers.LayerArrow;
import net.minecraft.util.ResourceLocation;

public abstract class RenderPonyBase extends RenderPlayer implements IRenderPony {
  
  protected final boolean smallArms;
  
  private PlayerModel playerModel;
  private AbstractPonyModel ponyModel;
  
  private Pony pony;
  
  public RenderPonyBase(RenderManager manager, boolean useSmallArms, String id, PlayerModel model) {
      super(manager, useSmallArms);
      smallArms = useSmallArms;
      
      setPlayerModel(model);
      
      layerRenderers.clear();
      addExtraLayers();
      
      ((IRenderManager)manager).addPlayerSkin(id, this);
  }
  
  protected void addExtraLayers() {
      addLayer(new LayerPonyArmor(this));
      addLayer(new LayerHeldPonyItem(this));
      addLayer(new LayerArrow(this));
      addLayer(new LayerPonyCape(this));
      addLayer(new LayerPonyCustomHead(this));
      addLayer(new LayerPonyElytra(this));
      addLayer(new LayerEntityOnPonyShoulder(renderManager, this));
  }
  
  @Override
  protected void renderLivingAt(AbstractClientPlayer player, double x, double y, double z) {
      float s = getScaleFactor();
      GlStateManager.scale(s, s, s);
      super.renderLivingAt(player, x, y, z);
  }

  @Override
  public void doRender(AbstractClientPlayer player, double x, double y, double z, float entityYaw, float partialTicks) {
      updateModel(player);

      ponyModel.isSneak = player.isSneaking();
      ponyModel.isSleeping = player.isPlayerSleeping();
      ponyModel.isFlying = pony.isPegasusFlying(player);
      
      shadowSize = getPonyShadowScale();
      
      super.doRender(player, x, y, z, entityYaw, partialTicks);
  }
  
  @Override
  public void renderRightArm(AbstractClientPlayer player) {
      updateModel(player);
      bindEntityTexture(player);
      super.renderRightArm(player);
  }

  @Override
  public void renderLeftArm(AbstractClientPlayer player) {
      updateModel(player);
      bindEntityTexture(player);
      super.renderLeftArm(player);
  }
  
  @Override
  protected void applyRotations(AbstractClientPlayer player, float yaw, float pitch, float ticks) {
      super.applyRotations(player, yaw, pitch, ticks);
      
      if (player.isElytraFlying()) {
        transformFlying(player, yaw, pitch, ticks);
        
        return;
      }
      
      if (player.isEntityAlive() && player.isPlayerSleeping()) return;
      
      // require arms to be stretched out (sorry mud ponies, no flight skills for you)
      if (!((ModelPlayerPony) ponyModel).rainboom) {
          ponyModel.motionPitch = 0;
          return;
      }
      double motionX = player.posX - player.prevPosX;
      double motionY = player.posY - player.prevPosY;
      double motionZ = player.posZ - player.prevPosZ;
      if (player.onGround) {
          motionY = 0;
      }
      double dist = Math.sqrt(motionX * motionX + motionZ * motionZ);
      double angle = Math.atan2(motionY, dist);
      
      if (!player.capabilities.isFlying) {
          if (angle > 0) {
              angle = 0;
          } else {
              angle /= 2;
          }
      }

      if (angle > Math.PI / 3) angle = Math.PI / 3;
      if (angle < -Math.PI / 3) angle = -Math.PI / 3;

      ponyModel.motionPitch = (float) Math.toDegrees(angle);

      GlStateManager.rotate((float) Math.toDegrees(angle), 1, 0, 0);
  }
  
  public ResourceLocation getEntityTexture(AbstractClientPlayer entity) {
      updateModel(entity);
      return pony.getTexture();
  }
  
  @Override
  public PlayerModel getPlayerModel() {
      return playerModel;
  }

  protected void setPlayerModel(PlayerModel model) {
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
  
  protected abstract float getPonyShadowScale();
  
  protected abstract float getScaleFactor();

  protected abstract void transformFlying(AbstractClientPlayer player, float yaw, float pitch, float ticks);
}
