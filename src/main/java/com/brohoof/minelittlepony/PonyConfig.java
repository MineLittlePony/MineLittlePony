package com.brohoof.minelittlepony;

import java.io.File;

import com.google.gson.GsonBuilder;
import com.google.gson.annotations.Expose;
import com.mumfrey.liteloader.modconfig.AdvancedExposable;
import com.mumfrey.liteloader.modconfig.ConfigStrategy;
import com.mumfrey.liteloader.modconfig.ExposableOptions;

import net.minecraft.util.EnumTypeAdapterFactory;

@ExposableOptions(filename = "minelittlepony", strategy = ConfigStrategy.Unversioned)
public class PonyConfig implements AdvancedExposable {

    @Expose
    private Value<PonyLevel> ponylevel = new Value<PonyLevel>(PonyLevel.PONIES);
    @Expose
    private Value<Boolean> sizes = new Value<Boolean>(true);
    @Expose
    private Value<Boolean> ponyarmor = new Value<Boolean>(true);
    @Expose
    private Value<Boolean> snuzzles = new Value<Boolean>(true);
    @Expose
    private Value<Boolean> hd = new Value<Boolean>(true);
    @Expose
    private Value<Boolean> showscale = new Value<Boolean>(true);
    @Expose
    private Value<Boolean> villagers = new Value<Boolean>(true);
    @Expose
    private Value<Boolean> zombies = new Value<Boolean>(true);
    @Expose
    private Value<Boolean> pigzombies = new Value<Boolean>(true);
    @Expose
    private Value<Boolean> skeletons = new Value<Boolean>(true);

    public Value<PonyLevel> getPonyLevel() {
        if (ponylevel.get() == null)
            ponylevel.set(PonyLevel.PONIES);
        return ponylevel;
    }

    public Value<Boolean> getSizes() {
        return sizes;
    }

    public Value<Boolean> getPonyArmor() {
        return ponyarmor;
    }

    public Value<Boolean> getSnuzzles() {
        return snuzzles;
    }

    public Value<Boolean> getHd() {
        return hd;
    }

    public Value<Boolean> getShowScale() {
        return showscale;
    }

    public Value<Boolean> getVillagers() {
        return villagers;
    }

    public Value<Boolean> getZombies() {
        return zombies;
    }

    public Value<Boolean> getPigZombies() {
        return pigzombies;
    }

    public Value<Boolean> getSkeletons() {
        return skeletons;
    }

    @Override
    public void setupGsonSerialiser(GsonBuilder gsonBuilder) {
        gsonBuilder.registerTypeAdapterFactory(new EnumTypeAdapterFactory())
                .registerTypeAdapter(Value.class, new Value.Serializer());
    }

    @Override
    public File getConfigFile(File configFile, File configFileLocation, String defaultFileName) {
        return null;
    }

}
