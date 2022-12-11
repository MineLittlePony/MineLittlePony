package com.voxelmodpack.hdskins.util;

import com.google.common.io.CharStreams;
import com.google.gson.*;
import com.mojang.util.UUIDTypeAdapter;
import com.voxelmodpack.hdskins.HDSkinManager;
import com.voxelmodpack.hdskins.server.HttpException;

import org.apache.http.HttpEntity;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.entity.ContentType;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.*;

/**
 * Utility class for getting different response types from a http response.
 */
@FunctionalInterface
public interface MoreHttpResponses extends AutoCloseable {
    CloseableHttpClient HTTP_CLIENT = HttpClients.createSystem();
    Gson GSON = new GsonBuilder()
            .registerTypeAdapter(UUID.class, new UUIDTypeAdapter())
            .create();

    static MoreHttpResponses execute(HttpUriRequest request) throws IOException {
        CloseableHttpResponse response = HTTP_CLIENT.execute(request);
        return () -> response;
    }

    static NameValuePair[] mapAsParameters(Map<String, String> parameters) {
        return parameters.entrySet().stream()
                .map(entry ->
                    new BasicNameValuePair(entry.getKey(), entry.getValue())
                )
                .toArray(NameValuePair[]::new);
    }

    CloseableHttpResponse response();

    default boolean contentTypeMatches(String contentType) {
        return contentType.contentEquals(entity()
                .map(ContentType::get)
                .orElse(ContentType.DEFAULT_TEXT)
                .getMimeType()
        );
    }

    default int responseCode() {
        return response().getStatusLine().getStatusCode();
    }

    default Optional<HttpEntity> entity() {
        return Optional.ofNullable(response().getEntity());
    }

    default BufferedReader reader() throws IOException {
        return new BufferedReader(new InputStreamReader(response().getEntity().getContent(), StandardCharsets.UTF_8));
    }

    default String text() throws IOException {
        try (BufferedReader reader = reader()) {
            return CharStreams.toString(reader);
        }
    }

    default <T> T json(Class<T> type, String errorMessage) throws IOException {

        if (!contentTypeMatches("application/json")) {
            String text = text();
            HDSkinManager.logger.error(errorMessage, text);
            throw new HttpException(text, responseCode(), null);
        }

        String text = text();

        T t = GSON.fromJson(text, type);
        if (t == null) {
            throw new HttpException(errorMessage + "\n " + text, responseCode(), null);
        }
        return t;
    }

    default boolean ok() {
        return responseCode() < HttpStatus.SC_MULTIPLE_CHOICES;
    }

    default MoreHttpResponses requireOk() throws IOException {
        if (!ok()) {
            JsonObject json = json(JsonObject.class, "Server did not respond correctly. Status Code " + responseCode());
            if (json.has("message")) {
                throw new HttpException(json.get("message").getAsString(), responseCode(), null);
            } else {
                throw new HttpException(json.toString(), responseCode(), null);
            }
        }
        return this;
    }

    @Override
    default void close() throws IOException {
        response().close();
    }

}