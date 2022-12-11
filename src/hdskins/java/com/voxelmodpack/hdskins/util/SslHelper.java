package com.voxelmodpack.hdskins.util;

import java.io.*;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.security.KeyStore;
import java.security.cert.Certificate;
import java.security.cert.CertificateFactory;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManagerFactory;

import org.apache.http.client.methods.RequestBuilder;
import org.apache.logging.log4j.Logger;

/**
 * Credit to https://github.com/Cloudhunter/LetsEncryptCraft
 *
 * @author CloudHunter
 * @author Sollace - modified to close resources
 */
public class SslHelper {

    public static void addLetsEncryptCertificate() throws Exception {

        try (InputStream caInput = SslHelper.class.getResourceAsStream("/lets-encrypt-x3-cross-signed.der")) {
            Certificate crt = CertificateFactory.getInstance("X.509").generateCertificate(caInput);
            KeyStore keyStore = KeyStore.getInstance(KeyStore.getDefaultType());

            try (InputStream ksPath = Files.newInputStream(Paths.get(System.getProperty("java.home"), "lib", "security", "cacerts"))) {
                keyStore.load(ksPath, "changeit".toCharArray());
            }

            keyStore.setCertificateEntry("lets-encrypt-x3-cross-signed", crt);

            TrustManagerFactory tmf = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
            tmf.init(keyStore);

            SSLContext sslContext = SSLContext.getInstance("TLS");
            sslContext.init(null, tmf.getTrustManagers(), null);
            SSLContext.setDefault(sslContext);
        }
    }

    public static void doStuff(Logger mod) {
        String version = System.getProperty("java.version");
        Pattern p = Pattern.compile("^(\\d+\\.\\d+).*?_(\\d+).*");
        Matcher matcher = p.matcher(version);
        String majorVersion;
        int minorVersion;

        if (matcher.matches()) {
            majorVersion = matcher.group(1);
            minorVersion = Integer.valueOf(matcher.group(2));
        } else {
            majorVersion = "1.7";
            minorVersion = 110;
            mod.info("Regex to parse Java version failed - applying LetsEncrypt anyway.");
        }

        switch (majorVersion) {
            case "1.7":
                if (minorVersion >= 111) {
                    mod.info("LetsEncrypt is not needed as Java version is at least Java 7u111.");
                    return;
                }
                break;
            case "1.8":
                if (minorVersion >= 101) {
                    mod.info("LetsEncrypt is not needed as Java version is at least Java 8u101.");
                    return;
                }
                break;
        }

        String body = "";
        try {
            mod.info("Adding Let's Encrypt certificate...");
            addLetsEncryptCertificate();
            mod.info("Done, attempting to connect to https://helloworld.letsencrypt.org...");
            URL url = new URL("https://helloworld.letsencrypt.org");
            URLConnection conn = url.openConnection();
            conn.setConnectTimeout(5000);
            conn.setReadTimeout(5000);
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream(), StandardCharsets.UTF_8))) {
                body = reader.readLine();
            }

            try (MoreHttpResponses response = MoreHttpResponses.execute(RequestBuilder.get().setUri("https://helloworld.letsencrypt.org").build())) {
                response.requireOk();
            }
        } catch (Exception e) {
            mod.error("An error occurred whilst adding the Let's Encrypt root certificate. I'm afraid you wont be able to access resources with a Let's Encrypt certificate D:", e);
        }

        if (body.isEmpty()) {
            mod.error("An unknown error occurred whilst adding the Let's Encrypt root certificate. I'm afraid you may not be able to access resources with a Let's Encrypt certificate D:");
        } else {
            mod.info("Done - you are now able to access resources with a Let's Encrypt certificate :D");
        }
    }
}
