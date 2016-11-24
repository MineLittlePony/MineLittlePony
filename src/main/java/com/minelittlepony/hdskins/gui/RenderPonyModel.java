package com.minelittlepony.hdskins.gui;

import com.minelittlepony.MineLittlePony;
import com.minelittlepony.Pony;
import com.minelittlepony.model.PlayerModel;
import com.voxelmodpack.hdskins.gui.RenderPlayerModel;
import net.minecraft.client.model.ModelPlayer;
import net.minecraft.client.renderer.entity.RenderManager;

public class RenderPonyModel extends RenderPlayerModel<EntityPonyModel> {

    public RenderPonyModel(RenderManager renderer) {
        super(renderer);
    }

    @Override
    public ModelPlayer getEntityModel(EntityPonyModel playermodel) {
        Pony thePony = MineLittlePony.getInstance().getManager().getPonyFromResourceRegistry(this.getEntityTexture(playermodel));
        thePony.invalidateSkinCheck();
        thePony.checkSkin();

        // TODO small arms
        PlayerModel pm = thePony.getModel(true, false);
        pm.apply(thePony.metadata);

        return pm.getModel();
    }

}
