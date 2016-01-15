package com.mumfrey.liteloader.util.net;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.io.IOUtils;

/**
 * Utility class to fetch the contents of a URL into a string
 * 
 * @author Adam Mummery-Smith
 */
public class HttpStringRetriever extends Thread
{
    public static final String LINE_ENDING_LF = "\n";
    public static final String LINE_ENDING_CR = "\r";
    public static final String LINE_ENDING_CRLF = "\r\n";

    /**
     * URL to connect to
     */
    private final String url;

    /**
     * Additional headers to pass, if required
     */
    private final Map<String, String> headers;

    private final String lineEnding;

    /**
     * Response code returned from the remote server (if any)
     */
    private int httpResponseCode = 0;

    private final Object resultLock = new Object();

    /**
     * String retrieved from the remote server
     */
    private String string;

    /**
     * True when this retriever is complete, even if retrieval failed
     */
    private volatile boolean done = false;

    /**
     * True if the fetch operation was a success
     */
    private volatile boolean success = false;

    /**
     * Create a new string retriever for the specified URL, with the supplied
     * headers and line end characters.
     * 
     * @param url URL to download from
     * @param headers Additional headers to add to the request
     * @param lineEnding Line ending to use
     */
    public HttpStringRetriever(String url, Map<String, String> headers, String lineEnding)
    {
        this.url = url;
        this.headers = headers;
        this.lineEnding = lineEnding;
    }

    /**
     * Create a new string retriever for the specified URL, with the supplied
     * headers.
     * 
     * @param url URL to download from
     * @param headers Additional headers to add to the request
     */
    public HttpStringRetriever(String url, Map<String, String> headers)
    {
        this(url, headers, HttpStringRetriever.LINE_ENDING_LF);
    }

    /**
     * Create a new string retriever for the specified URL
     * 
     * @param url URL to download from
     */
    public HttpStringRetriever(String url)
    {
        this(url, null);
    }

    /**
     * Create a new string retriever to be used synchronously
     */
    public HttpStringRetriever()
    {
        this(null, null);
    }

    /**
     * Get the string which was retrieved
     */
    public String getString()
    {
        synchronized (this.resultLock)
        {
            return this.string;
        }
    }

    /**
     * True if the download is complete, even if it failed
     */
    public boolean isDone()
    {
        return this.done;
    }

    /**
     * True if the download completed successfully
     */
    public boolean getSuccess()
    {
        return this.success;
    }

    /**
     * Get the response code from the HTTP request, -1 if a connection error
     * occurred.
     */
    public int getHttpResponseCode()
    {
        return this.httpResponseCode;
    }

    /* (non-Javadoc)
     * @see java.lang.Thread#run()
     */
    @Override
    public void run()
    {
        try
        {
            String result = this.fetch(new URL(this.url));

            synchronized (this.resultLock)
            {
                this.string = result;
            }
        }
        catch (MalformedURLException ex)
        {
            ex.printStackTrace();
        }

        this.done = true;
    }

    /**
     * Fetch a String in the current thread, normally this method is called by
     * the run() method to fetch the resource in a new thread but can be called
     * directly to fetch the result in the current thread.
     * 
     * @param url URL to fetch
     * @return retrieved string or empty string on failure
     */
    public String fetch(URL url) 
    {
        StringBuilder readString = new StringBuilder();
        HttpURLConnection httpClient = null;

        try
        {
            // Open a HTTP connection to the URL
            httpClient = (HttpURLConnection)url.openConnection();
            httpClient.setDoInput(true);
            httpClient.setUseCaches(false);

            httpClient.setRequestMethod("GET");
            httpClient.setRequestProperty("Connection", "Close");
            httpClient.addRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64; rv:19.0) Gecko/20100101 Firefox/21.0"); // For CloudFlare

            if (this.headers != null)
            {
                for (Entry<String, String> header : this.headers.entrySet())
                    httpClient.addRequestProperty(header.getKey(), header.getValue());
            }

            this.httpResponseCode = httpClient.getResponseCode();
            if (this.httpResponseCode >= 200 && this.httpResponseCode < 300)
            {
                InputStream httpStream = httpClient.getInputStream();
                BufferedReader reader = null;
                try
                {
                    reader = new BufferedReader(new InputStreamReader(httpStream));

                    String readLine;
                    while ((readLine = reader.readLine()) != null)
                    {
                        readString.append(readLine).append(this.lineEnding);
                    }

                    this.success = true;
                }
                catch (IOException ex)
                {
                }
                finally
                {
                    IOUtils.closeQuietly(reader);
                    IOUtils.closeQuietly(httpStream);
                }
            }
        }
        catch (IOException ex)
        {
            this.httpResponseCode = -1;
        }
        finally
        {
            if (httpClient != null) httpClient.disconnect();
        }

        return readString.toString();
    }
}