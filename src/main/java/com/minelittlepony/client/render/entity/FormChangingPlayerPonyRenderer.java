package com.minelittlepony.client.render.entity;

import net.minecraft.client.entity.ClientAvatarEntity;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.Avatar;

import com.minelittlepony.api.pony.*;

import java.util.function.Predicate;


public class FormChangingPlayerPonyRenderer<Player extends Avatar & ClientAvatarEntity> extends PlayerPonyRenderer<Player> {
    private final Identifier alternateFormSkinId;
    private final Predicate<Player> formModifierPredicate;

    public FormChangingPlayerPonyRenderer(EntityRendererProvider.Context context,
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
