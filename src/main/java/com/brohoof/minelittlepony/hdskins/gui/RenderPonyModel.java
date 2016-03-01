package com.brohoof.minelittlepony.hdskins.gui;

import com.brohoof.minelittlepony.MineLittlePony;
import com.brohoof.minelittlepony.Pony;
import com.brohoof.minelittlepony.model.PlayerModel;
import com.voxelmodpack.hdskins.gui.RenderPlayerModel;

import net.minecraft.client.renderer.entity.RenderManager;

public class RenderPonyModel extends RenderPlayerModel<EntityPonyModel> {

    public RenderPonyModel(RenderManager renderer) {
        super(renderer);
    }

    @Override
    protected void renderModel(EntityPonyModel playermodel, float par2, float par3, float par4, float par5, float par6, float par7) {
        this.bindEntityTexture(playermodel);
        Pony thePony = MineLittlePony.getInstance().getManager().getPonyFromResourceRegistry(this.getEntityTexture(playermodel));
        thePony.checkSkin();
        // TODO small arms
        PlayerModel pm = thePony.getModel(true, false);
        this.mainModel = pm.getModel();
        pm.apply(thePony.metadata);
        this.mainModel.render(playermodel, par2, par3, par4, par5, par6, par7);
    }

}
