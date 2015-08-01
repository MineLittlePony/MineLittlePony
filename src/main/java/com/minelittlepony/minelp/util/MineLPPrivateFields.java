package com.minelittlepony.minelp.util;

import java.util.Map;

import com.mumfrey.liteloader.core.runtime.Obf;
import com.mumfrey.liteloader.util.PrivateFields;

import net.minecraft.client.renderer.entity.layers.LayerArmorBase;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.ResourceLocation;

public class MineLPPrivateFields<P, T> extends PrivateFields<P, T> {
    public static final MineLPPrivateFields<EntityLivingBase, Boolean> isJumping = new MineLPPrivateFields<EntityLivingBase, Boolean>(
            EntityLivingBase.class, MineLPObf.isJumping);
    public static final MineLPPrivateFields<LayerArmorBase, Map<String, ResourceLocation>> ARMOR_TEXTURE_RES_MAP = new MineLPPrivateFields<LayerArmorBase, Map<String, ResourceLocation>>(
            LayerArmorBase.class, MineLPObf.ARMOR_TEXTURE_RES_MAP);

    protected MineLPPrivateFields(Class<P> owner, Obf obf) {
        super(owner, obf);
    }

}
