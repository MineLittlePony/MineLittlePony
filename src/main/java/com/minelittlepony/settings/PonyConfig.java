package com.minelittlepony.settings;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.minelittlepony.MineLittlePony;
import com.minelittlepony.pony.data.PonyLevel;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;

/**
 * Storage container for MineLP client settings.
 */
public class PonyConfig {

    private PonyLevel ponylevel = PonyLevel.PONIES;

    public boolean sizes = true;
    public boolean snuzzles = true;
    public boolean hd = true;
    public boolean showscale = true;

    public boolean villagers = true;
    public boolean zombies = true;
    public boolean pigzombies = true;
    public boolean skeletons = true;
    public boolean illagers = true;
    public boolean guardians = true;

    public enum PonySettings implements Setting<PonyConfig> {
        SIZES,
        SNUZZLES,
        HD,
        SHOWSCALE

    }

    /**
     * Gets the current PonyLevel. That is the level of ponies you would like to see.
     *
     * @param ignorePony true to ignore whatever value the setting has.
     */
    public PonyLevel getEffectivePonyLevel(boolean ignorePony) {
        return ignorePony ? PonyLevel.BOTH : getPonyLevel();
    }

    /**
     * Actually gets the pony level value. No option to ignore reality here.
     */
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
        this.ponylevel = ponylevel;
    }

    public void save() {

    }

    public static class Loader {

        private static final Gson gson = new GsonBuilder().setPrettyPrinting().create();

        private final Path path;

        private PonyConfig config;

        public Loader(Path path) {
            this.path = path;
        }

        public PonyConfig getConfig() {
            if (config == null) {
                reload();
            }
            return config;
        }

        public void reload() {
            try (BufferedReader reader = Files.newBufferedReader(path)) {
                config = gson.fromJson(reader, PonyConfig.class);
            } catch (NoSuchFileException e) {
                config = new PonyConfig();
            } catch (IOException e) {
                MineLittlePony.logger.warn("Error while loading config. Using defaults.", e);
                config = new PonyConfig();
            }
            save();
        }

        public void save() {
            try (BufferedWriter writer = Files.newBufferedWriter(path)) {
                gson.toJson(getConfig(), writer);
            } catch (IOException e) {
                MineLittlePony.logger.warn("Unable to save config.", e);
            }
        }
    }
}
