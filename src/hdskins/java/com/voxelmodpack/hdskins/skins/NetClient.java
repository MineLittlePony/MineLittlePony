package com.voxelmodpack.hdskins.skins;

import java.io.BufferedReader;
import java.io.Closeable;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URI;
import java.util.Map;

import org.apache.http.HttpEntity;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.methods.RequestBuilder;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

/**
 * Ew. Why so many builders? >.<
 */
public class NetClient implements Closeable {

    private static CloseableHttpClient client = null;

    private RequestBuilder rqBuilder;

    private Map<String, ?> headers;

    private CloseableHttpResponse response = null;

    public NetClient(String method, String uri) {
        start(method, uri);
    }

    /**
     * Starts a new network request.
     *
     * @param method    The HTTP method verb. GET/PUT/POST/DELETE/OPTIONS
     * @param uri       Http link to query
     *
     * @return Itself for chaining
     */
    public NetClient start(String method, String uri) {
        rqBuilder = RequestBuilder.create(method).setUri(uri);
        headers = null;

        if (response != null) {
            EntityUtils.consumeQuietly(response.getEntity());
            response = null;
        }

        return this;
    }

    /**
     * Adds a file to the request. Typically used with PUT/POST for uploading.
     * @param key           Key identifier to index the file in the request.
     * @param contentType   Type of file being sent. Usually the mime-type.
     * @param file          The file or a link to the file.
     *
     * @return itself for chaining
     */
    public NetClient putFile(String key, String contentType, URI file) {
        File f = new File(file);
        HttpEntity entity = MultipartEntityBuilder.create().addBinaryBody(key, f, ContentType.create(contentType), f.getName()).build();

        rqBuilder.setEntity(entity);

        return this;
    }

    /**
     * Sets the headers to be included with this request.
     * @param headers   Headers to send
     *
     * @return itself for chaining
     */
    public NetClient putHeaders(Map<String, ?> headers) {
        this.headers = headers;

        return this;
    }

    /**
     * Commits and sends the request.
     */
    private void send() throws IOException {
        HttpUriRequest request = rqBuilder.build();

        if (headers != null) {
            for (Map.Entry<String, ?> parameter : headers.entrySet()) {
                request.addHeader(parameter.getKey(), parameter.getValue().toString());
            }
        }

        if (client == null) {
            client = HttpClients.createSystem();
        }

        response = client.execute(request);
    }

    /**
     * Gets or obtains the http response body.
     */
    public CloseableHttpResponse getResponse() throws IOException {
        if (response == null) {
            send();
        }

        return response;
    }

    /**
     * Gets or obtains a response status code.
     */
    public int getResponseCode() throws IOException {
        return getResponse().getStatusLine().getStatusCode();
    }

    /**
     * Consumes and returns the entire response body.
     */
    public String getResponseText() throws IOException {
        if (getResponse().getEntity() == null) {
            return "";
        }

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(getResponse().getEntity().getContent()))) {
            StringBuilder builder = new StringBuilder();

            int ch;
            while ((ch = reader.read()) != -1) {
                builder.append((char)ch);
            }

            return builder.toString();
        }
    }

    @Override
    public void close() throws IOException {
        try {
            if (response != null) {
                response.close();
            }
        } finally {
            response = null;
        }
    }
}
