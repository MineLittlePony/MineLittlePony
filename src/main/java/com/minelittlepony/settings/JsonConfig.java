package com.minelittlepony.settings;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.stream.JsonWriter;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

public class JsonConfig extends Config {

    public static <T extends JsonConfig> T of(Path file, Supplier<T> creator) {
        return creator.get().load(file);
    }

    static final Gson gson = new GsonBuilder()
            .setPrettyPrinting()
            .excludeFieldsWithoutExposeAnnotation()
            .create();

    private Path configFile;

    @Override
    public void save() {
        try (JsonWriter writer = new JsonWriter(Files.newBufferedWriter(configFile))) {
            writer.setIndent("    ");

            gson.toJson(entries, HashMap.class, writer);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @SuppressWarnings("unchecked")
    protected <T extends JsonConfig> T load(Path file) {
        try {
            if (Files.exists(file)) {
                try (BufferedReader s = Files.newBufferedReader(file)) {
                    Map<String, Object> parsed = gson.fromJson(s, HashMap.class);

                    if (parsed != null) {
                        entries = parsed;
                    }
                } catch (IOException ignored) { }
            }
            configFile = file;
        } finally {
            save();
        }

        return (T)this;
    }
}
