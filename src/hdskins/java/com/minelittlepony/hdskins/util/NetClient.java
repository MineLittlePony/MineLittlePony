package com.minelittlepony.hdskins.util;

import org.apache.http.HttpEntity;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.methods.RequestBuilder;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.MultipartEntityBuilder;

import com.minelittlepony.hdskins.HDSkinManager;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

/**
 * Ew. Why so many builders? >.<
 */
public class NetClient {

    private final RequestBuilder rqBuilder;
    private MultipartEntityBuilder entityBuilder;

    private Map<String, ?> headers;

    public NetClient(String method, String uri) {
        rqBuilder = RequestBuilder.create(method).setUri(uri);
    }

    public NetClient putFile(String key, String contentType, URI file) {
        if (entityBuilder == null) {
            entityBuilder= MultipartEntityBuilder.create();
        }

        File f = new File(file);
        HttpEntity entity = entityBuilder.addBinaryBody(key, f, ContentType.create(contentType), f.getName()).build();

        rqBuilder.setEntity(entity);

        return this;
    }

    public NetClient putFormData(Map<String, ?> data, String contentTypes) {
        if (entityBuilder == null) {
            entityBuilder= MultipartEntityBuilder.create();
        }

        for (Map.Entry<String, ?> i : data.entrySet()) {
            entityBuilder.addTextBody(i.getKey(), i.getValue().toString());
        }
        return this;
    }

    public NetClient putHeaders(Map<String, ?> headers) {
        this.headers = headers;

        return this;
    }

    public MoreHttpResponses send() throws IOException {
        if (entityBuilder != null) {
            rqBuilder.setEntity(entityBuilder.build());
        }

        HttpUriRequest request = rqBuilder.build();

        if (headers != null) {
            for (Map.Entry<String, ?> parameter : headers.entrySet()) {
                request.addHeader(parameter.getKey(), parameter.getValue().toString());
            }
        }

        return MoreHttpResponses.execute(HDSkinManager.httpClient, request);
    }

    public CompletableFuture<MoreHttpResponses> async(Executor exec) {
        return CallableFutures.asyncFailableFuture(this::send, exec);
    }
}
