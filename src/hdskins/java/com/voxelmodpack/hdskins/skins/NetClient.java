package com.voxelmodpack.hdskins.skins;

import com.voxelmodpack.hdskins.HDSkinManager;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.methods.RequestBuilder;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.MultipartEntityBuilder;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.util.Map;

/**
 * Ew. Why so many builders? >.<
 */
public class NetClient {

    private final RequestBuilder rqBuilder;

    private Map<String, ?> headers;

    public NetClient(String method, String uri) {
        rqBuilder = RequestBuilder.create(method).setUri(uri);
    }

    public NetClient putFile(String key, String contentType, URI file) {
        File f = new File(file);
        HttpEntity entity = MultipartEntityBuilder.create().addBinaryBody(key, f, ContentType.create(contentType), f.getName()).build();

        rqBuilder.setEntity(entity);

        return this;
    }

    public NetClient putHeaders(Map<String, ?> headers) {
        this.headers = headers;

        return this;
    }

    // TODO: Fix this
    public MoreHttpResponses send() throws IOException {
        HttpUriRequest request = rqBuilder.build();

        for (Map.Entry<String, ?> parameter : headers.entrySet()) {
            request.addHeader(parameter.getKey(), parameter.getValue().toString());
        }

        return MoreHttpResponses.execute(HDSkinManager.httpClient, request);
    }
}
