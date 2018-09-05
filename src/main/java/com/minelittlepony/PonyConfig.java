package com.minelittlepony;

import com.google.gson.annotations.Expose;
import com.minelittlepony.pony.data.PonyLevel;
import com.minelittlepony.settings.SensibleConfig;
import com.mumfrey.liteloader.modconfig.ConfigStrategy;
import com.mumfrey.liteloader.modconfig.Exposable;
import com.mumfrey.liteloader.modconfig.ExposableOptions;
import com.voxelmodpack.hdskins.HDSkinManager;

/**
 * Storage container for MineLP client settings.
 */
@ExposableOptions(filename = "minelittlepony", strategy = ConfigStrategy.Unversioned)
public class PonyConfig extends SensibleConfig implements Exposable, IPonyConfig {

    @Expose private PonyLevel ponylevel = PonyLevel.PONIES;

    @Expose public boolean sizes = IPonyConfig.super.getSizes();
    @Expose public boolean snuzzles = IPonyConfig.super.getSnuzzles();
    @Expose public boolean hd = IPonyConfig.super.getHD();
    @Expose public boolean showscale = IPonyConfig.super.getShowScale();
    @Expose public boolean fpsmagic = IPonyConfig.super.getFPSMagic();
    @Expose public boolean ponyskulls = IPonyConfig.super.getPonySkulls();

    public enum PonySettings implements Setting {
        SIZES,
        SNUZZLES,
        HD,
        SHOWSCALE,
        FPSMAGIC,
        PONYSKULLS;
    }

    @Expose public boolean villagers = true;
    @Expose public boolean zombies = true;
    @Expose public boolean pigzombies = true;
    @Expose public boolean skeletons = true;
    @Expose public boolean illagers = true;
    @Expose public boolean guardians = true;
    @Expose public boolean endermen = true;

    @Override
    public boolean getSizes() {
        return sizes;
    }
    @Override
    public boolean getSnuzzles() {
        return snuzzles;
    }
    @Override
    public boolean getHD() {
        return hd;
    }
    @Override
    public boolean getShowScale() {
        return showscale;
    }
    @Override
    public boolean getFPSMagic() {
        return fpsmagic;
    }
    @Override
    public boolean getPonySkulls() {
        return ponyskulls;
    }

    @Override
    public PonyLevel getPonyLevel() {
        if (ponylevel == null) {
            ponylevel = PonyLevel.PONIES;
        }
        return ponylevel;
    }

    /**
     * Sets the pony level. Want MOAR PONEHS? Well here you go.
     *
     * @param ponylevel
     */
    public void setPonyLevel(PonyLevel ponylevel) {
        // only trigger reloads when the value actually changes
        if (ponylevel != this.ponylevel) {
            this.ponylevel = ponylevel;
            HDSkinManager.INSTANCE.parseSkins();
        }
    }
}
