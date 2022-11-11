package com.voxelmodpack.hdskins.util;

import com.google.common.io.ByteStreams;
import com.google.common.io.CharStreams;
import com.google.gson.*;
import com.mojang.util.UUIDTypeAdapter;
import com.voxelmodpack.hdskins.HDSkinManager;
import com.voxelmodpack.hdskins.server.SkinServer;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.entity.ContentType;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.message.BasicNameValuePair;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.stream.Stream;

/**
 * Utility class for getting different response types from a http response.
 */
@FunctionalInterface
public interface MoreHttpResponses extends AutoCloseable {
    Gson GSON = new GsonBuilder()
            .registerTypeAdapter(UUID.class, new UUIDTypeAdapter())
            .create();

    CloseableHttpResponse getResponse();

    default boolean ok() {
        return getResponseCode() == HttpStatus.SC_OK;
    }

    default boolean json() {
        return "application/json".contentEquals(contentType().getMimeType());
    }

    default int getResponseCode() {
        return getResponse().getStatusLine().getStatusCode();
    }

    default Optional<HttpEntity> getEntity() {
        return Optional.ofNullable(getResponse().getEntity());
    }

    default ContentType contentType() {
        return getEntity()
                .map(ContentType::get)
                .orElse(ContentType.DEFAULT_TEXT);
    }

    default String getContentType() {
        return getEntity().map(HttpEntity::getContentType).map(Header::getValue).orElse("text/plain");
    }

    default InputStream getInputStream() throws IOException {
        return getResponse().getEntity().getContent();
    }

    default BufferedReader getReader() throws IOException {
        return new BufferedReader(new InputStreamReader(getInputStream(), StandardCharsets.UTF_8));
    }

    default byte[] bytes() throws IOException {
        try (InputStream input = getInputStream()) {
            return ByteStreams.toByteArray(input);
        }
    }

    default String text() throws IOException {
        try (BufferedReader reader = getReader()) {
            return CharStreams.toString(reader);
        }
    }

    default Stream<String> lines() throws IOException {
        try (BufferedReader reader = getReader()) {
            return reader.lines();
        }
    }

    default <T> T json(Class<T> type) throws IOException {
        try (BufferedReader reader = getReader()) {
            return SkinServer.gson.fromJson(reader, type);
        }
    }

    default <T> T json(Type type) throws IOException {
        try (BufferedReader reader = getReader()) {
            return SkinServer.gson.fromJson(reader, type);
        }
    }

    default <T> T json(Class<T> type, String errorMessage) throws IOException {
        return json((Type)type, errorMessage);
    }

    default <T> T json(Type type, String errorMessage) throws IOException {
        if (!json()) {
            String text = text();
            HDSkinManager.logger.error(errorMessage, text);
            throw new IOException(text);
        }

        try (BufferedReader reader = getReader()) {
            return GSON.fromJson(reader, type);
        }
    }

    default <T> T unwrapAsJson(Type type) throws IOException {
        if (!"application/json".equals(getContentType())) {
            throw new IOException("Server returned a non-json response!");
        }

        if (ok()) {
            return json(type);
        }

        throw exception();
    }

    default IOException exception() throws IOException {
        return new IOException(json(JsonObject.class, "Server error wasn't in json: {}").get("message").getAsString());
    }

    @Override
    default void close() throws IOException {
        this.getResponse().close();
    }

    static MoreHttpResponses execute(CloseableHttpClient client, HttpUriRequest request) throws IOException {
        CloseableHttpResponse response = client.execute(request);
        return () -> response;
    }

    static NameValuePair[] mapAsParameters(Map<String, String> parameters) {
        return parameters.entrySet().stream()
                .map(entry ->
                    new BasicNameValuePair(entry.getKey(), entry.getValue())
                )
                .toArray(NameValuePair[]::new);
    }
}
