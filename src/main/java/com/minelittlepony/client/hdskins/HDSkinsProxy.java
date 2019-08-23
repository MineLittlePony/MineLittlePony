package com.minelittlepony.client.hdskins;

import com.minelittlepony.client.SkinsProxy;
import com.minelittlepony.hdskins.HDSkins;
import com.minelittlepony.hdskins.profile.SkinType;
import com.mojang.authlib.GameProfile;
import net.minecraft.util.Identifier;

class HDSkinsProxy extends SkinsProxy {

    @Override
    public Identifier getSkinTexture(GameProfile profile) {

        Identifier skin = HDSkins.getInstance().getProfileRepository().getTextures(profile).get(SkinType.SKIN);

        if (skin != null) {
            return skin;
        }

        return super.getSkinTexture(profile);
    }
}
