package com.minelittlepony.minelp.model;

import java.lang.reflect.Field;

import com.minelittlepony.minelp.model.pony.pm_Human;
import com.minelittlepony.minelp.model.pony.pm_newPonyAdv;
import com.minelittlepony.minelp.model.pony.pm_skeletonPony;
import com.minelittlepony.minelp.model.pony.pm_zombiePony;
import com.minelittlepony.minelp.model.pony.armor.pma_Human;
import com.minelittlepony.minelp.model.pony.armor.pma_newPony;
import com.minelittlepony.minelp.model.pony.armor.pma_skeletonPony;
import com.minelittlepony.minelp.model.pony.armor.pma_zombiePony;

public final class PMAPI {

    public static PlayerModel newPonyAdv = new PlayerModel("newPonyAdv", new pm_newPonyAdv("/mob/char.png"), 0)
            .setTextureHeight(32)
            .setArmor(new pma_newPony("minelittlepony:textures/models/armor/"))
            .setURL("http://skins.minecraft.net/MinecraftSkins/%NAME%.png")
            .setScale(0.9375F);
    public static PlayerModel newPonyAdv_64 = new PlayerModel("newPonyAdv", new pm_newPonyAdv("/mob/char.png"), 0)
            .setArmor(new pma_newPony("minelittlepony:textures/models/armor/"))
            .setURL("http://skins.minecraft.net/MinecraftSkins/%NAME%.png")
            .setScale(0.9375F);
    public static PlayerModel zombiePony = new PlayerModel("zombiePony", new pm_zombiePony("/mob/char.png"), 0)
            .setTextureHeight(32)
            .setArmor(new pma_zombiePony("minelittlepony:textures/models/armor/"))
            .setURL("http://skins.minecraft.net/MinecraftSkins/%NAME%.png")
            .setScale(0.9375F);
    public static PlayerModel skeletonPony = new PlayerModel("skeletonPony", new pm_skeletonPony("/mob/char.png"), 0)
            .setTextureHeight(32)
            .setArmor(new pma_skeletonPony("minelittlepony:textures/models/armor/"))
            .setURL("http://skins.minecraft.net/MinecraftSkins/%NAME%.png")
            .setScale(0.9375F);
    public static PlayerModel human = new PlayerModel("Human", new pm_Human("/mob/char.png"), 1)
            .setTextureHeight(32)
            .setArmor(new pma_Human("minecraft:textures/models/armor/"))
            .setURL("http://skins.minecraft.net/MinecraftSkins/%NAME%.png")
            .setScale(0.9375F);
    public static PlayerModel human_64 = new PlayerModel("Human", new pm_Human("/mob/char.png"), 1)
            .setArmor(new pma_Human("minecraft:textures/models/armor/"))
            .setURL("http://skins.minecraft.net/MinecraftSkins/%NAME%.png")
            .setScale(0.9375F);

    public static void init() {
        for (Field field : PMAPI.class.getFields()) {
            try {
                PlayerModel model = (PlayerModel) field.get(null);
                model.init();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
