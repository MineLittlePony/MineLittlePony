package com.minelittlepony.client.model.gear;

import net.minecraft.client.model.ModelPart;
import net.minecraft.entity.Entity;
import net.minecraft.util.Identifier;

import com.minelittlepony.client.util.render.Part;
import com.minelittlepony.model.BodyPart;
import com.minelittlepony.model.IModel;
import com.minelittlepony.model.gear.IStackable;
import com.minelittlepony.mson.api.ModelContext;
import com.minelittlepony.pony.meta.Wearable;

public class WitchHat extends AbstractGear implements IStackable {

    private static final Identifier WITCH_TEXTURES = new Identifier("textures/entity/witch.png");

    private ModelPart witchHat;


    @Override
    public void init(ModelContext context) {
        witchHat = context.findByName("witch_hat");
    }

    @Deprecated
    public void init(float yOffset, float stretch) {
        accept(witchHat = new Part(this).size(64, 128));
        ((Part)witchHat).around(HEAD_RP_X, HEAD_RP_Y + yOffset, HEAD_RP_Z)
                .tex(0, 64).box(-5, -6, -7, 10, 2, 10, stretch)
                .child(0).around(1.75F, -4, 2)
                    .tex(0, 76).box(-5, -5, -7, 7, 4, 7, stretch)
                    .rotate(-0.05235988F, 0, 0.02617994F)
                    .child(0).around(1.75F, -4, 2)
                        .tex(0, 87).box(-5, -4, -7, 4, 4, 4, stretch)
                        .rotate(-0.10471976F, 0, 0.05235988F)
                        .child(0).around(1.75F, -2, 2)
                            .tex(0, 95).box(-5, -2, -7, 1, 2, 1, stretch)
                            .rotate(-0.20943952F, 0, 0.10471976F);
    }


    @Override
    public boolean canRender(IModel model, Entity entity) {
        return model.isWearing(Wearable.HAT);
    }

    @Override
    public BodyPart getGearLocation() {
        return BodyPart.HEAD;
    }

    @Override
    public <T extends Entity> Identifier getTexture(T entity, IRenderContext<T, ?> context) {
        return WITCH_TEXTURES;
    }

    @Override
    public float getStackingOffset() {
        return 0.7F;
    }
}
