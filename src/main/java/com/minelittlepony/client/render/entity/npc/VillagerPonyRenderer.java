package com.minelittlepony.client.render.entity.npc;

import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.npc.villager.Villager;

import com.minelittlepony.client.VariatedTextureSupplier;
import com.minelittlepony.client.model.ClientPonyModel;
import com.minelittlepony.client.render.entity.npc.textures.*;

public class VillagerPonyRenderer extends AbstractNpcRenderer<Villager, VillagerPonyRenderer.State> {
    private static final TextureSupplier<String> FORMATTER = TextureSupplier.formatted("minelittlepony", "textures/entity/villager/%s.png");
    private static final TextureSupplier<Villager> TEXTURES = TextureSupplier.ofPool(
            VariatedTextureSupplier.BACKGROUND_PONIES_POOL,
            PlayerTextureSupplier.create(ProfessionTextureSupplier.create(FORMATTER))
    );

    public VillagerPonyRenderer(EntityRendererProvider.Context context) {
        super(context, "villager", TEXTURES, FORMATTER);
    }

    @Override
    protected void initializeModel(ClientPonyModel<State> model) {
        model.onSetModelAngles((m, state) -> {
            if (state.headRolling) {
                m.head.zRot = 0.3F * Mth.sin(0.45F * state.ageInTicks);
                m.head.xRot = 0.4F;
            } else {
                m.head.zRot = 0;
            }
        });
    }

    @Override
    public State createRenderState() {
        return new State();
    }

    @Override
    public void extractRenderState(Villager entity, State state, float tickDelta) {
        super.extractRenderState(entity, state, tickDelta);
        state.headRolling = entity.getUnhappyCounter() > 0;
        state.yRot = 0.3F * Mth.sin(0.45F * state.ageInTicks);
    }

    public static class State extends SillyPonyTextureSupplier.State {
        public boolean headRolling;
    }
}
