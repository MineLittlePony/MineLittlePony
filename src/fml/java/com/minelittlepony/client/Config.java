package com.minelittlepony.client;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import com.minelittlepony.client.settings.ClientPonyConfig;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;

class Config extends ClientPonyConfig {
    static final Gson gson = new GsonBuilder()
            .setPrettyPrinting()
            .excludeFieldsWithoutExposeAnnotation()
            .create();

    private final File configFile;

    Config(File file) {
        configFile = file;
    }

    @Override
    public void save() {
        if (configFile.exists()) {
            configFile.delete();
        }

        try (JsonWriter writer = new JsonWriter(new OutputStreamWriter(new FileOutputStream(configFile)))) {
            writer.setIndent("    ");

            gson.toJson(this, Config.class, writer);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    static Config of(File file) {
        Config result = null;

        if (file.exists()) {
            try (FileInputStream s = new FileInputStream(file)) {
                result = gson.fromJson(new JsonReader(new InputStreamReader(s)), Config.class);
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