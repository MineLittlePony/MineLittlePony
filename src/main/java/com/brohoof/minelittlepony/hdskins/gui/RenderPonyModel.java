package com.brohoof.minelittlepony.hdskins.gui;

import com.brohoof.minelittlepony.MineLittlePony;
import com.brohoof.minelittlepony.Pony;
import com.brohoof.minelittlepony.model.PlayerModel;
import com.voxelmodpack.hdskins.gui.EntityPlayerModel;
import com.voxelmodpack.hdskins.gui.RenderPlayerModel;

import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.EntityLivingBase;

public class RenderPonyModel extends RenderPlayerModel {

    public RenderPonyModel(RenderManager renderer) {
        super(renderer);
    }

    @Override
    protected void renderModel(EntityLivingBase par1EntityLivingBase, float par2, float par3, float par4, float par5, float par6, float par7) {
        this.bindEntityTexture(par1EntityLivingBase);
        EntityPlayerModel playerModelEntity = (EntityPlayerModel) par1EntityLivingBase;
        Pony thePony = PonyManager.getInstance().getPonyFromResourceRegistry(this.getEntityTexture(playerModelEntity));
        thePony.checkSkin();
        PlayerModel pm = thePony.getModel(true);
        this.mainModel = pm.model;
        pm.model.isFlying = thePony.isFlying = false;
        pm.model.isPegasus = thePony.isPegasus();
        pm.model.isUnicorn = thePony.isUnicorn();
        pm.model.isMale = thePony.isMale();
        pm.model.size = thePony.size();
        pm.model.glowColor = thePony.glowColor();
        pm.model.wantTail = thePony.wantTail();
        pm.model.isVillager = false;
        this.mainModel.render(par1EntityLivingBase, par2, par3, par4, par5, par6, par7);
    }

}
