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

import com.mumfrey.liteloader.util.log.LiteLoaderLogger;

/**
 * Thing for doing the whole pastebin malarkey
 *
 * @author Adam Mummery-Smith
 */
public class PastebinUpload extends Thread
{
    public static int PUBLIC = 0;
    public static int UNLISTED = 1;
    public static int PRIVATE = 2;

    private static final String PASTEBIN_API_URL = "http://pastebin.com/api/api_post.php";

    private static final String PASTEBIN_API_KEY = "2eda2d0840d2ab7e1ed036e3e810bfda";

    private final String encodedData;

    private volatile boolean completed;

    private String pasteUrl = null;

    public PastebinUpload(String author, String pasteName, String content, int privacy)
    {
        Map<String, String> data = new HashMap<String, String>();

        data.put("api_option", "paste");
        data.put("api_user_key", "");
        data.put("api_paste_private", String.valueOf(privacy));
        data.put("api_paste_name", pasteName);
        data.put("api_paste_expire_date", "N");
        data.put("api_paste_format", "text");
        data.put("api_dev_key", PASTEBIN_API_KEY);
        data.put("api_paste_code", content); 

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

    public String getPasteUrl()
    {
        return this.pasteUrl;
    }

    @Override
    public void run()
    {
        try
        {
            URL url = new URL(PASTEBIN_API_URL);
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

            int responseCode = httpClient.getResponseCode();
            if (responseCode / 100 == 2)
            {
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
                    this.pasteUrl = readString.toString();
                }
                catch (IOException ex)
                {
                    ex.printStackTrace();
                }

                httpStream.close();
            }

            outputStream.close();
        }
        catch (Exception ex)
        {
            LiteLoaderLogger.warning("Error posting log to pastebin: %s: %s", ex.getClass(), ex.getMessage());
        }

        this.completed = true;
    }
}