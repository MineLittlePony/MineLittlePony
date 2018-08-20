package com.voxelmodpack.hdskins.upload;

import org.apache.commons.io.IOUtils;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;
import java.util.Map.Entry;

import javax.annotation.Nullable;

/**
 * Uploader for Multipart form data
 *
 * @author Adam Mummery-Smith
 * @deprecated Use httpmime multipart upload
 */
@Deprecated
public class ThreadMultipartPostUpload {
    protected final Map<String, ?> sourceData;

    protected final String method;

    protected final String authorization;

    protected final String urlString;

    protected HttpURLConnection httpClient;

    protected static final String CRLF = "\r\n";

    protected static final String twoHyphens = "--";

    protected static final String boundary = "----------AaB03x";

    public String response;

    public ThreadMultipartPostUpload(String method, String url, Map<String, ?> sourceData, @Nullable String authorization) {
        this.method = method;
        this.urlString = url;
        this.sourceData = sourceData;
        this.authorization = authorization;
    }

    public ThreadMultipartPostUpload(String url, Map<String, ?> sourceData) {
        this("POST", url, sourceData, null);
    }

    public String uploadMultipart() throws IOException {
        // open a URL connection
        URL url = new URL(this.urlString);

        // Open a HTTP connection to the URL
        this.httpClient = (HttpURLConnection) url.openConnection();
        this.httpClient.setDoOutput(true);
        this.httpClient.setUseCaches(false);

        this.httpClient.setRequestMethod(this.method);
        this.httpClient.setRequestProperty("Connection", "Close");
        this.httpClient.addRequestProperty("User-Agent", "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.0)"); // For CloudFlare

        if (this.sourceData.size() > 0) {
            this.httpClient.setRequestProperty("Content-Type", "multipart/form-data; boundary=" + boundary);
        }

        if (this.authorization != null) {
            this.httpClient.addRequestProperty("Authorization", this.authorization);
        }

        try (DataOutputStream outputStream = new DataOutputStream(this.httpClient.getOutputStream())) {

            for (Entry<String, ?> data : this.sourceData.entrySet()) {
                outputStream.writeBytes(twoHyphens + boundary + CRLF);

                String paramName = data.getKey();
                Object paramData = data.getValue();

                if (paramData instanceof URI) {
                    Path uploadPath = Paths.get((URI) paramData);

                    outputStream.writeBytes("Content-Disposition: form-data; name=\"" + paramName + "\"; filename=\"" + uploadPath.getFileName() + "\"" + CRLF);
                    outputStream.writeBytes("Content-Type: image/png" + CRLF + CRLF);


                    Files.copy(uploadPath, outputStream);
                } else {
                    outputStream.writeBytes("Content-Disposition: form-data; name=\"" + paramName + "\"" + CRLF + CRLF);

                    outputStream.writeBytes(paramData.toString());
                }

                outputStream.writeBytes(ThreadMultipartPostUpload.CRLF);
            }

            outputStream.writeBytes(twoHyphens + boundary + twoHyphens + CRLF);
        }

        try (InputStream input = this.httpClient.getInputStream()) {
            return IOUtils.toString(input, StandardCharsets.UTF_8);
        }
    }

}