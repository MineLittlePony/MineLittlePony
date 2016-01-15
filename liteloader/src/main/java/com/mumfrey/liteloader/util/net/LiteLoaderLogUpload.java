package com.mumfrey.liteloader.util.net;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import com.mumfrey.liteloader.core.LiteLoader;
import com.mumfrey.liteloader.util.log.LiteLoaderLogger;

/**
 * Upload manager for posting logs to liteloader.com
 *
 * @author Adam Mummery-Smith
 */
public class LiteLoaderLogUpload extends Thread
{
    private static final String POST_URL = "http://logs.liteloader.com/post";

    private static final String LITELOADER_KEY = "liteloader0cea4593b6a51e7c";

    private final String encodedData;

    private volatile boolean completed;

    private String response = "Unknown Error";

    public LiteLoaderLogUpload(String nick, String uuid, String content)
    {
        Map<String, String> data = new HashMap<String, String>();

        data.put("nick", nick);
        data.put("uuid", uuid);
        data.put("token", LITELOADER_KEY);
        data.put("version", LiteLoader.getVersion());
        data.put("brand", "" + LiteLoader.getBranding());
        data.put("log", content); 

        StringBuilder sb = new StringBuilder();

        try
        {
            String separator = "";
            for (Entry<String, String> postValue : data.entrySet())
            {
                sb.append(separator).append(postValue.getKey()).append("=").append(URLEncoder.encode(postValue.getValue(), "UTF-8"));
                separator = "&";
            }
        }
        catch (UnsupportedEncodingException ex)
        {
            ex.printStackTrace();
        }

        this.encodedData = sb.toString();
    }

    public boolean isCompleted()
    {
        return this.completed;
    }

    public String getLogUrl()
    {
        return this.response;
    }

    @Override
    public void run()
    {
        try
        {
            URL url = new URL(POST_URL);
            HttpURLConnection httpClient = (HttpURLConnection)url.openConnection();
            httpClient.setConnectTimeout(5000);
            httpClient.setReadTimeout(10000);
            httpClient.addRequestProperty("Content-type", "application/x-www-form-urlencoded");
            httpClient.addRequestProperty("User-Agent", "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.0)"); // For not fail++
            httpClient.setRequestMethod("POST");
            httpClient.setDoOutput(true);

            DataOutputStream outputStream = new DataOutputStream(httpClient.getOutputStream());
            outputStream.writeBytes(this.encodedData);
            outputStream.flush();

            InputStream httpStream = httpClient.getInputStream();

            try
            {
                StringBuilder readString = new StringBuilder();
                BufferedReader reader = new BufferedReader(new InputStreamReader(httpStream));

                String readLine;
                while ((readLine = reader.readLine()) != null)
                {
                    readString.append(readLine).append("\n");
                }

                reader.close();
                this.response = readString.toString();
            }
            catch (IOException ex)
            {
                ex.printStackTrace();
            }

            httpStream.close();
            outputStream.close();
        }
        catch (Exception ex)
        {
            this.response = ex.getMessage();
            LiteLoaderLogger.warning("Error posting log to liteloader.com: %s: %s", ex.getClass(), ex.getMessage());
        }

        this.completed = true;
    }
}