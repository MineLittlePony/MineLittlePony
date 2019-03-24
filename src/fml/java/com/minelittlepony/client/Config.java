package com.minelittlepony.client;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.stream.JsonWriter;
import com.minelittlepony.client.settings.ClientPonyConfig;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

class Config extends ClientPonyConfig {
    static final Gson gson = new GsonBuilder()
            .setPrettyPrinting()
            .excludeFieldsWithoutExposeAnnotation()
            .create();

    private final Path configFile;

    Config(Path file) {
        configFile = file;
    }

    @Override
    public void save() {

        try (JsonWriter writer = new JsonWriter(Files.newBufferedWriter(configFile))) {
            writer.setIndent("    ");

            gson.toJson(this, Config.class, writer);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    static Config of(Path file) {
        Config result = null;

        if (Files.exists(file)) {
            try (BufferedReader s = Files.newBufferedReader(file)) {
                result = gson.fromJson(s, Config.class);
            } catch (IOException ignored) {
                result = null;
            }
        }

        if (result == null) {
            result = new Config(file);
        }

        result.save();

        return result;
    }
}