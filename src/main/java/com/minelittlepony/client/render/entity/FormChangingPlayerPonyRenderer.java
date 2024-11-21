package com.minelittlepony.client.render.entity;

import com.minelittlepony.api.pony.*;

import java.util.function.Predicate;

import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.util.Identifier;

public class FormChangingPlayerPonyRenderer extends PlayerPonyRenderer {
    private final Identifier alternateFormSkinId;
    private final Predicate<AbstractClientPlayerEntity> formModifierPredicate;

    public FormChangingPlayerPonyRenderer(EntityRendererFactory.Context context,
            boolean slim, Identifier alternateFormSkinId, Predicate<AbstractClientPlayerEntity> formModifierPredicate) {
        super(context, slim);
        this.alternateFormSkinId = alternateFormSkinId;
        this.formModifierPredicate = formModifierPredicate;
    }

    @Override
    public Pony getEntityPony(AbstractClientPlayerEntity entity) {
        Identifier skinOverride = getSkinOverride((AbstractClientPlayerEntity)entity);
        return skinOverride == null ? super.getEntityPony(entity) : Pony.getManager().getPony(skinOverride);
    }

    protected Identifier getSkinOverride(AbstractClientPlayerEntity player) {
        return formModifierPredicate.test(player) ? SkinsProxy.getInstance().getSkin(alternateFormSkinId, player).orElse(null) : null;
    }
}
