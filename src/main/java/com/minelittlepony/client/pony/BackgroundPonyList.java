package com.minelittlepony.client.pony;

import net.minecraft.resource.Resource;
import net.minecraft.resource.ResourceManager;
import net.minecraft.resource.ResourceReloadListener.Synchronizer;
import net.minecraft.util.Identifier;
import net.minecraft.util.profiler.Profiler;

import com.google.common.collect.Lists;
import com.google.gson.Gson;
import com.google.gson.JsonParseException;
import com.minelittlepony.api.pony.IPony;
import com.minelittlepony.api.pony.IPonyManager;
import com.minelittlepony.client.MineLittlePony;
import com.minelittlepony.common.util.MoreStreams;
import com.minelittlepony.util.MathUtil;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

public class BackgroundPonyList {

    private static final Gson GSON = new Gson();

    /**
     * All currently loaded background ponies.
     */
    private List<Identifier> backgroundPonyList = Lists.newArrayList();

    public IPony getBackgroundPony(UUID uuid) {
        if (getNumberOfPonies() == 0 || isUser(uuid)) {
            return getPony(IPonyManager.getDefaultSkin(uuid));
        }

        int bgi = MathUtil.mod(uuid.hashCode(), getNumberOfPonies());

        return getPony(backgroundPonyList.get(bgi));
    }

    public void reloadAll(ResourceManager resourceManager) {
        backgroundPonyList.clear();

        List<Identifier> collectedPaths = new LinkedList<>();
        List<BackgroundPonies> collectedPonies = new LinkedList<>();

        Queue<BackgroundPonies> processingQueue = new LinkedList<>();

        for (String domain : resourceManager.getAllNamespaces()) {
            processingQueue.addAll(loadBgPonies(resourceManager, new Identifier(domain, BGPONIES_JSON)));
        }

        BackgroundPonies item;
        while ((item = processingQueue.poll()) != null) {
            for (Identifier imp : item.getImports()) {
                if (!collectedPaths.contains(imp)) {
                    collectedPaths.add(imp);
                    processingQueue.addAll(loadBgPonies(resourceManager, imp));
                }
            }

            collectedPonies.add(item);
        }

        for (BackgroundPonies i : collectedPonies) {
            if (i.override) {
                backgroundPonyList.clear();
            }

            backgroundPonyList.addAll(i.getPonies());
        }

        backgroundPonyList = MoreStreams.distinct(backgroundPonyList);

        MineLittlePony.logger.info("Detected {} background ponies installed.", getNumberOfPonies());
    }


    private Queue<BackgroundPonies> loadBgPonies(ResourceManager resourceManager, Identifier location) {
        Queue<BackgroundPonies> collectedPonies = new LinkedList<>();

        try {
            String path = location.getPath().replace("bgponies.json", "");

            for (Resource res : resourceManager.getAllResources(location)) {
                try (Reader reader = new InputStreamReader((res.getInputStream()))) {
                    BackgroundPonies ponies = GSON.fromJson(reader, BackgroundPonies.class);

                    ponies.domain = location.getNamespace();
                    ponies.path = path;

                    collectedPonies.add(ponies);
                } catch (JsonParseException e) {
                    MineLittlePony.logger.error("Invalid bgponies.json in " + res.getResourcePackName(), e);
                }
            }
        } catch (IOException ignored) {
            // this isn't the exception you're looking for.
        }

        return collectedPonies;
    }

    private int getNumberOfPonies() {
        return backgroundPonyList.size();
    }

    private static class BackgroundPonies {

        private boolean override;

        private List<String> ponies;

        private List<String> imports = new ArrayList<>();

        private String domain;
        private String path;

        private Identifier apply(String input) {
            return new Identifier(domain, String.format("%s%s.png", path, input));
        }

        private Identifier makeImport(String input) {
            return new Identifier(domain, String.format("%s%s/bgponies.json", path, input));
        }

        public List<Identifier> getPonies() {
            return MoreStreams.map(ponies, this::apply);
        }

        public List<Identifier> getImports() {
            return MoreStreams.map(imports, this::makeImport);
        }
    }
}
