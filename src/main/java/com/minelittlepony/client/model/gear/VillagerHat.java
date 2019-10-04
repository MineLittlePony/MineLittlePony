package com.minelittlepony.client.model.gear;

import net.minecraft.entity.Entity;
import net.minecraft.util.Identifier;
import net.minecraft.village.VillagerDataContainer;

import com.minelittlepony.client.util.render.PonyRenderer;
import com.minelittlepony.model.BodyPart;
import com.minelittlepony.model.IModel;
import com.minelittlepony.pony.meta.Wearable;

import java.util.UUID;

public class VillagerHat extends AbstractGear {

    private static final Identifier TEXTURE = new Identifier("minelittlepony", "textures/models/antlers.png");

    private PonyRenderer hat;

    @Override
    public void init(float yOffset, float stretch) {
        hat = new PonyRenderer(this, 30, 47)
                .around(0, 0, 0)
                .box(-8, -8, -6, 16, 16, 1, stretch);
        hat.pitch = -1.5707964F;
    }

    @Override
    public boolean canRender(IModel model, Entity entity) {
        return model.isWearing(Wearable.VILLAGER);
    }

    @Override
    public BodyPart getGearLocation() {
        return BodyPart.HEAD;
    }

    @Override
    public <T extends Entity> Identifier getTexture(T entity, IGearRenderContext<T> context) {
        if (entity instanceof VillagerDataContainer) {
            return context.getDefaultTexture(entity, this);
        }
        return TEXTURE;
    }

    @Override
    public void renderPart(float scale, UUID interpolatorId) {
        hat.render(scale);
    }

}
