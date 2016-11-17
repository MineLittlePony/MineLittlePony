package com.minelittlepony.hdskins.gui;

import com.google.common.base.Optional;
import com.minelittlepony.MineLittlePony;
import com.minelittlepony.Pony;
import com.minelittlepony.PonyGender;
import com.minelittlepony.PonyRace;
import com.minelittlepony.PonySize;
import com.minelittlepony.TailLengths;
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

        if (playermodel.metaHandler != null) {
            Optional<String> race = playermodel.metaHandler.get(MineLittlePony.MLP_RACE);
            Optional<String> tail = playermodel.metaHandler.get(MineLittlePony.MLP_TAIL);
            Optional<String> gender = playermodel.metaHandler.get(MineLittlePony.MLP_GENDER);
            Optional<String> size = playermodel.metaHandler.get(MineLittlePony.MLP_SIZE);
            Optional<String> magicColor = playermodel.metaHandler.get(MineLittlePony.MLP_MAGIC);

            if (race.isPresent())
                thePony.metadata.setRace(PonyRace.valueOf(race.get()));
            if (tail.isPresent())
                thePony.metadata.setTail(TailLengths.valueOf(tail.get()));
            if (gender.isPresent())
                thePony.metadata.setGender(PonyGender.valueOf(gender.get()));
            if (size.isPresent())
                thePony.metadata.setSize(PonySize.valueOf(size.get()));
            if (magicColor.isPresent())
                thePony.metadata.setGlowColor(Integer.parseInt(magicColor.get()));
        }

        // TODO small arms
        PlayerModel pm = thePony.getModel(true, false);
        pm.apply(thePony.metadata);

        return pm.getModel();
    }

}
