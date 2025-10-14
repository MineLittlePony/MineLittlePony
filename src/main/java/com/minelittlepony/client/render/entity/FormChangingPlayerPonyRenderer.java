package com.minelittlepony.client.render.entity;

import com.minelittlepony.api.pony.*;

import java.util.function.Predicate;

import net.minecraft.client.network.ClientPlayerLikeEntity;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.entity.PlayerLikeEntity;
import net.minecraft.util.Identifier;

public class FormChangingPlayerPonyRenderer<Player extends PlayerLikeEntity & ClientPlayerLikeEntity> extends PlayerPonyRenderer<Player> {
    private final Identifier alternateFormSkinId;
    private final Predicate<Player> formModifierPredicate;

    public FormChangingPlayerPonyRenderer(EntityRendererFactory.Context context,
            boolean slim, Identifier alternateFormSkinId, Predicate<Player> formModifierPredicate) {
        super(context, slim);
        this.alternateFormSkinId = alternateFormSkinId;
        this.formModifierPredicate = formModifierPredicate;
    }

    @Override
    public Pony getEntityPony(Player entity) {
        Identifier skinOverride = getSkinOverride(entity);
        return skinOverride == null ? super.getEntityPony(entity) : Pony.getManager().getPony(skinOverride);
    }

    protected Identifier getSkinOverride(Player player) {
        return formModifierPredicate.test(player) ? SkinsProxy.getInstance().getSkin(alternateFormSkinId, player).orElse(null) : null;
    }
}
