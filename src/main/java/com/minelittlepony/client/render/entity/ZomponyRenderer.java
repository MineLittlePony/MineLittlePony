package com.minelittlepony.client.render.entity;

import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.monster.*;
import net.minecraft.world.entity.monster.zombie.*;

import com.minelittlepony.api.pony.meta.Race;
import com.minelittlepony.client.MineLittlePony;
import com.minelittlepony.client.model.ModelType;
import com.minelittlepony.client.model.entity.ZomponyModel;
import com.minelittlepony.client.render.entity.npc.textures.TextureSupplier;
import com.minelittlepony.client.render.entity.state.HostilePonyRenderState;

public class ZomponyRenderer<T extends Monster> extends PonyRenderer<T, HostilePonyRenderState, ZomponyModel<HostilePonyRenderState>> {
    public static final Identifier ZOMBIE = MineLittlePony.id("textures/entity/zombie/zombie_pony.png");
    public static final Identifier HUSK = MineLittlePony.id("textures/entity/zombie/husk_pony.png");
    public static final Identifier DROWNED = MineLittlePony.id("textures/entity/zombie/drowned_pony.png");

    public static final Identifier DEMON_CHILD = MineLittlePony.id("textures/entity/zombie/demon_child.png");

    protected ZomponyRenderer(EntityRendererProvider.Context context, TextureSupplier<T> texture, float scale) {
        super(context, ModelType.ZOMBIE, texture, scale);
    }

    @Override
    public HostilePonyRenderState createRenderState() {
        return new HostilePonyRenderState();
    }

    @Override
    public void extractRenderState(T entity, HostilePonyRenderState state, float tickDelta) {
        super.extractRenderState(entity, state, tickDelta);
        state.race = isWinged(entity) ? (state.race.hasHorn() ? Race.ALICORN : Race.PEGASUS) : state.race;
    }

    public static ZomponyRenderer<Zombie> zombie(EntityRendererProvider.Context context) {
        return new ZomponyRenderer<>(context, entity -> isCozyGlow(entity) ? DEMON_CHILD : ZOMBIE, 1);
    }

    public static ZomponyRenderer<Husk> husk(EntityRendererProvider.Context context) {
        return new ZomponyRenderer<>(context, TextureSupplier.of(HUSK), 1.0625F);
    }

    public static ZomponyRenderer<Drowned> drowned(EntityRendererProvider.Context context) {
        return new ZomponyRenderer<>(context, TextureSupplier.of(DROWNED), 1);
    }

    public static ZomponyRenderer<Giant> giant(EntityRendererProvider.Context context) {
        return new ZomponyRenderer<>(context, TextureSupplier.of(ZOMBIE), 6.8F);
    }

    static boolean isCozyGlow(LivingEntity entity) {
        return entity.isBaby() && entity.getUUID().getLeastSignificantBits() % 160 == 0;
    }

    static boolean isWinged(LivingEntity entity) {
        return entity.getUUID().getLeastSignificantBits() % 30 == 0;
    }
}
