package com.minelittlepony.client.render.entity;

import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.mob.*;
import net.minecraft.util.Identifier;

import com.minelittlepony.api.model.ModelAttributes;
import com.minelittlepony.api.model.PonyModel;
import com.minelittlepony.api.pony.Pony;
import com.minelittlepony.api.pony.meta.Race;
import com.minelittlepony.client.MineLittlePony;
import com.minelittlepony.client.model.ModelType;
import com.minelittlepony.client.model.entity.ZomponyModel;
import com.minelittlepony.client.render.entity.npc.textures.TextureSupplier;
import com.minelittlepony.client.render.entity.state.PonyRenderState;

public class ZomponyRenderer<T extends HostileEntity> extends PonyRenderer<T, ZomponyRenderer.State, ZomponyModel<ZomponyRenderer.State>> {
    public static final Identifier ZOMBIE = MineLittlePony.id("textures/entity/zombie/zombie_pony.png");
    public static final Identifier HUSK = MineLittlePony.id("textures/entity/zombie/husk_pony.png");
    public static final Identifier DROWNED = MineLittlePony.id("textures/entity/zombie/drowned_pony.png");

    public static final Identifier DEMON_CHILD = MineLittlePony.id("textures/entity/zombie/demon_child.png");

    protected ZomponyRenderer(EntityRendererFactory.Context context, TextureSupplier<T> texture, float scale) {
        super(context, ModelType.ZOMBIE, texture, scale);
    }

    @Override
    public State createRenderState() {
        return new State();
    }

    public static ZomponyRenderer<ZombieEntity> zombie(EntityRendererFactory.Context context) {
        return new ZomponyRenderer<>(context, entity -> isCozyGlow(entity) ? DEMON_CHILD : ZOMBIE, 1);
    }

    public static ZomponyRenderer<HuskEntity> husk(EntityRendererFactory.Context context) {
        return new ZomponyRenderer<>(context, TextureSupplier.of(HUSK), 1.0625F);
    }

    public static ZomponyRenderer<DrownedEntity> drowned(EntityRendererFactory.Context context) {
        return new ZomponyRenderer<>(context, TextureSupplier.of(DROWNED), 1);
    }

    public static ZomponyRenderer<GiantEntity> giant(EntityRendererFactory.Context context) {
        return new ZomponyRenderer<>(context, TextureSupplier.of(ZOMBIE), 6.8F);
    }

    static boolean isCozyGlow(LivingEntity entity) {
        return entity.isBaby() && entity.getUuid().getLeastSignificantBits() % 160 == 0;
    }

    public static class State extends PonyRenderState {
        public boolean isWinged;
        public boolean isCozyGlow;

        public void updateState(LivingEntity entity, PonyModel<?> model, Pony pony, ModelAttributes.Mode mode) {
            super.updateState(entity, model, pony, mode);
            isCozyGlow = isCozyGlow(entity);
            isWinged = entity.getUuid().getLeastSignificantBits() % 30 == 0;
        }

        @Override
        public Race getRace() {
            return isWinged ? (super.getRace().hasHorn() ? Race.ALICORN : Race.PEGASUS) : super.getRace();
        }
    }
}
