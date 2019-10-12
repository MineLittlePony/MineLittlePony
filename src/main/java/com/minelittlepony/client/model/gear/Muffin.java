package com.minelittlepony.client.model.gear;

import net.minecraft.entity.Entity;
import net.minecraft.util.Identifier;

import com.minelittlepony.client.util.render.PonyRenderer;
import com.minelittlepony.model.BodyPart;
import com.minelittlepony.model.IModel;
import com.minelittlepony.model.gear.IStackable;
import com.minelittlepony.pony.meta.Wearable;

import java.util.UUID;

public class Muffin extends AbstractGear implements IStackable {

    private static final Identifier TEXTURE = new Identifier("minelittlepony", "textures/models/muffin.png");

    private PonyRenderer crown;

    @Override
    public void init(float yOffset, float stretch) {
        crown = new PonyRenderer(this, 0, 0).size(64, 44)
                .around(-4, -12, -6)
                .box(0, 0, 0, 8, 4, 8, stretch)
                .box(3, -1.5F, 3, 2, 2, 2, stretch)
                .tex(0, 12).box(1.5F, -1, 1.5F, 5, 1, 5, stretch)
                .tex(0, 18).box(2, 1, 1, 4, 7, 6, stretch)
                .tex(0, 18).box(1, 1, 2, 6, 7, 4, stretch);
    }

    @Override
    public void renderPart(float scale, UUID interpolatorId) {
        crown.render(scale);
    }

    @Override
    public boolean canRender(IModel model, Entity entity) {
        return model.isWearing(Wearable.MUFFIN);
    }

    @Override
    public BodyPart getGearLocation() {
        return BodyPart.HEAD;
    }

    @Override
    public <T extends Entity> Identifier getTexture(T entity, IRenderContext<T, ?> context) {
        return TEXTURE;
    }

    @Override
    public float getStackingOffset() {
        return 0.5F;
    }
}
