package com.voxelmodpack.hdskins.util;

import com.google.common.io.CharStreams;
import com.voxelmodpack.hdskins.server.SkinServer;

import org.apache.http.HttpStatus;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.impl.client.CloseableHttpClient;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.util.stream.Stream;

/**
 * Utility class for getting different response types from a http response.
 */
@FunctionalInterface
public interface MoreHttpResponses extends AutoCloseable {

    CloseableHttpResponse getResponse();

    default boolean ok() {
        return getResponseCode() == HttpStatus.SC_OK;
    }

    default int getResponseCode() {
        return getResponse().getStatusLine().getStatusCode();
    }

    default InputStream getInputStream() throws IOException {
        return getResponse().getEntity().getContent();
    }

    default BufferedReader getReader() throws IOException {
        return new BufferedReader(new InputStreamReader(getInputStream(), StandardCharsets.UTF_8));
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

    @Override
    default void close() throws IOException {
        this.getResponse().close();
    }

    static MoreHttpResponses execute(CloseableHttpClient client, HttpUriRequest request) throws IOException {
        CloseableHttpResponse response = client.execute(request);
        return () -> response;
    }
}
