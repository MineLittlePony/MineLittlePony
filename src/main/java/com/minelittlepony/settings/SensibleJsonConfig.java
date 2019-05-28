package com.minelittlepony.settings;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.stream.JsonWriter;
import com.minelittlepony.client.settings.ClientPonyConfig;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.function.Supplier;

public class SensibleJsonConfig extends SensibleConfig {

    static final Gson gson = new GsonBuilder()
            .setPrettyPrinting()
            .excludeFieldsWithoutExposeAnnotation()
            .create();

    private Path configFile;

    @Override
    public void save() {

        try (JsonWriter writer = new JsonWriter(Files.newBufferedWriter(configFile))) {
            writer.setIndent("    ");

            gson.toJson(this, ClientPonyConfig.class, writer);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @SuppressWarnings("unchecked")
    protected <T extends SensibleJsonConfig> T load(Path file) {
        SensibleJsonConfig result = this;

        try {
            if (Files.exists(file)) {
                try (BufferedReader s = Files.newBufferedReader(file)) {
                    result = gson.fromJson(s, getClass());
                } catch (IOException ignored) {
                    result = null;
                }
            }

            if (result == null) {
                result = this;
            }

            result.configFile = file;
        } finally {
            result.save();
        }

        return (T)result;
    }

    public static <T extends SensibleJsonConfig> T of(Path file, Supplier<T> creator) {
        return creator.get().load(file);
    }
}
