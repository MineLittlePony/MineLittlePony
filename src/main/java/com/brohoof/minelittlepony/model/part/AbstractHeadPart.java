package com.brohoof.minelittlepony.model.part;

import com.brohoof.minelittlepony.PonyData;
import com.brohoof.minelittlepony.model.BodyPart;
import com.brohoof.minelittlepony.model.ModelPony;
import com.brohoof.minelittlepony.renderer.AniParams;

public abstract class AbstractHeadPart implements IPonyPart {

    private ModelPony pony;

    @Override
    public void init(ModelPony pony, float yOffset, float stretch) {
        this.pony = pony;
    }
    
    @Override
    public void render(PonyData data, float scale) {
        pony.transform(BodyPart.HEAD);
    }

    @Override
    public final void animate(PonyData data, AniParams ani) {
        rotateHead(ani.horz, ani.vert);
        if (pony.isSneak && !pony.isFlying) {
            position(0, 6, -2);
        } else {
            position(0, 0, 0);
        }
    }

    private void rotateHead(float horz, float vert) {
        float y;
        float x;
        if (pony.isSleeping) {
            y = 1.4F;
            x = 0.1F;
        } else {
            y = horz / (float) (180 / Math.PI);
            x = vert / (float) (180 / Math.PI);
        }
        x = Math.min(x, 0.5F);
        x = Math.max(x, -0.5F);

        rotate(x, y);
    }

    protected ModelPony getPony() {
        return pony;
    }

    protected abstract void position(float posX, float posY, float posZ);

    protected abstract void rotate(float rotX, float rotY);

}
