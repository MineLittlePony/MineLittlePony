package com.minelittlepony.renderer.player;

import com.minelittlepony.MineLittlePony;
import com.minelittlepony.model.PlayerModel;

import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.RenderManager;

public class RenderPonyPlayer extends RenderPonyBase {
  public RenderPonyPlayer(RenderManager renderManager, boolean useSmallArms, String id, PlayerModel model) {
      super(renderManager, useSmallArms, id, model);
  }
  
  @Override
  protected float getPonyShadowScale() {
      if (!MineLittlePony.getConfig().showscale) return .5f;
      return getPony().getMetadata().getSize().getShadowSize();
  }
  
  @Override
  protected float getScaleFactor() {
      return getPony().getMetadata().getSize().getScaleFactor();
  }

  @Override
  protected void transformFlying(AbstractClientPlayer player, float yaw, float pitch, float ticks) {
      GlStateManager.rotate(90, 1, 0, 0);
      GlStateManager.translate(0, -1, 0);
  }
}
