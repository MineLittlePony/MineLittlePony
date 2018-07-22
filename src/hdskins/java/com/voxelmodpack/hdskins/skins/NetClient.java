package com.voxelmodpack.hdskins.skins;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URI;
import java.util.Map;

import org.apache.commons.io.IOUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.methods.RequestBuilder;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.impl.client.HttpClients;

/**
 * Ew. Why so many builders? >.<
 */
public class NetClient {

    private final RequestBuilder rqBuilder;

    private Map<String, ?> headers;

    private CloseableHttpResponse response = null;

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

    public boolean send() {
        HttpUriRequest request = rqBuilder.build();

        for (Map.Entry<String, ?> parameter : headers.entrySet()) {
            request.addHeader(parameter.getKey(), parameter.getValue().toString());
        }

        try {
            response = HttpClients.createSystem().execute(request);

            return getResponseCode() == HttpStatus.SC_OK;
        } catch (IOException e) { }

        return false;
    }

    public int getResponseCode() {
        if (response == null) {
            send();
        }

        return response.getStatusLine().getStatusCode();
    }

    public String getResponseText() {
        if (response == null) {
            if (!send()) {
                return "";
            }
        }

        BufferedReader reader = null;

        try {
            reader = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));

            StringBuilder builder = new StringBuilder();

            int ch;
            while ((ch = reader.read()) != -1) {
                builder.append((char)ch);
            }

            return builder.toString();
        } catch (IOException e) {

        } finally {
            IOUtils.closeQuietly(reader);
        }

        return "";
    }
}
