package com.minelittlepony;

import com.google.gson.annotations.Expose;
import com.mumfrey.liteloader.modconfig.ConfigStrategy;
import com.mumfrey.liteloader.modconfig.Exposable;
import com.mumfrey.liteloader.modconfig.ExposableOptions;

@ExposableOptions(filename = "minelittlepony", strategy = ConfigStrategy.Unversioned)
public class PonyConfig implements Exposable {

    @Expose
    private PonyLevel ponylevel = PonyLevel.PONIES;
    @Expose
    public boolean sizes = true;
    @Expose
    public boolean snuzzles = true;
    @Expose
    public boolean hd = true;
    @Expose
    public boolean showscale = true;
    @Expose
    public boolean villagers = true;
    @Expose
    public boolean zombies = true;
    @Expose
    public boolean pigzombies = true;
    @Expose
    public boolean skeletons = true;
    @Expose
    public boolean illagers = true;

    public PonyLevel getPonyLevel() {
        if (ponylevel == null)
            ponylevel = PonyLevel.PONIES;
        return ponylevel;
    }

    public void setPonyLevel(PonyLevel ponylevel) {
        this.ponylevel = ponylevel;
    }
}
