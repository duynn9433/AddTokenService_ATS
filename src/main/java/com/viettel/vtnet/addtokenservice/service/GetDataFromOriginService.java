package com.viettel.vtnet.addtokenservice.service;

import io.lindstrom.m3u8.model.MasterPlaylist;
import io.lindstrom.m3u8.model.MediaPlaylist;
import io.lindstrom.m3u8.parser.MasterPlaylistParser;
import io.lindstrom.m3u8.parser.MediaPlaylistParser;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
<<<<<<< HEAD
import java.net.URL;
<<<<<<< HEAD
import javax.net.ssl.HttpsURLConnection;
=======
import java.nio.file.Paths;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.print.attribute.standard.Media;
>>>>>>> 46a6f09 (brk origin)
=======
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;
import java.util.Map;
import javax.net.ssl.HttpsURLConnection;
import lombok.extern.log4j.Log4j2;
>>>>>>> 2340af7 ([CDN-98] feature: add HIT/MISS log)
import org.springframework.stereotype.Service;

@Service
@Log4j2
public class GetDataFromOriginService {

  //  public String
  public static void main(String[] args) throws IOException {
    String masterUrl = "https://cdnvt.net/hls-stream/test/master.m3u8";
    String mediaUrl = "https://cdnvt.net/hls-stream/test/144p_index.m3u8";
    System.out.println("Master playlist");
    MasterPlaylistParser masterPlaylistParser = new MasterPlaylistParser();
    MasterPlaylist masterPlaylist = new GetDataFromOriginService().getMasterPlaylistFromOrigin(
        masterUrl);
    System.out.println(masterPlaylistParser.writePlaylistAsString(masterPlaylist));

    System.out.println("Media playlist:");
    MediaPlaylistParser mediaPlaylistParser = new MediaPlaylistParser();
    MediaPlaylist mediaPlaylist = new GetDataFromOriginService().getMediaPlaylistFromOrigin(
        mediaUrl);
    System.out.println(mediaPlaylistParser.writePlaylistAsString(mediaPlaylist));
  }

  public String getDataFromOriginHTTPS(String originUrl, boolean isHTTPS) {
    try {
      URL url = new URL(originUrl);
<<<<<<< HEAD
<<<<<<< HEAD
      HttpsURLConnection connection = (HttpsURLConnection) url.openConnection();

      BufferedReader reader = new BufferedReader(
          new InputStreamReader(connection.getInputStream()));
=======
//      //TODO: run 1 time
//      //no check certificate
//      // Create a custom TrustManager that disables certificate validation
//      TrustManager[] trustAllCerts = new TrustManager[]{new DisableCertificateValidation()};
//
//      // Create an SSLContext and initialize it with the custom TrustManager
//      SSLContext sslContext = SSLContext.getInstance("TLS");
//      sslContext.init(null, trustAllCerts, null);
//
//      // Set the default SSLContext to use your custom TrustManager
//      HttpsURLConnection.setDefaultSSLSocketFactory(sslContext.getSocketFactory());
//      //--------------------------------
      HttpsURLConnection connection = (HttpsURLConnection) url.openConnection();
=======
      HttpURLConnection connection;

      if(isHTTPS){
        connection = (HttpsURLConnection) url.openConnection();
      } else {
        connection = (HttpURLConnection) url.openConnection();
      }
      //for check HIT/MISS
      connection.setRequestProperty("X-Debug", "X-Cache, X-Cache-Key");
      // Get the response headers
      Map<String, List<String>> headers = connection.getHeaderFields();

      // Iterate through the headers and find the desired ones
      for (Map.Entry<String, List<String>> entry : headers.entrySet()) {
        String headerName = entry.getKey();
        List<String> headerValues = entry.getValue();
        if (headerValues != null) {
          for (String value : headerValues) {
            // Check for specific headers
            if ("X-Cache".equalsIgnoreCase(headerName)) {
              // Handle X-Cache header
              log.debug("X-Cache: " + value);
            } else if ("X-Cache-Key".equalsIgnoreCase(headerName)) {
              // Handle X-Cache-Key header
              log.debug("X-Cache-Key: " + value);
            }
          }
        }
      }
>>>>>>> 2340af7 ([CDN-98] feature: add HIT/MISS log)

      BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
>>>>>>> 46a6f09 (brk origin)
      StringBuilder stringBuilder = new StringBuilder();
      String line;
      while ((line = reader.readLine()) != null) {
        stringBuilder.append(line);
        stringBuilder.append("\n");
      }
      return stringBuilder.toString();
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

<<<<<<< HEAD
  public boolean isMasterPlaylist(String m3u8Data) {
    return m3u8Data.contains("#EXT-X-STREAM-INF");
  }

  public boolean isMediaPlaylist(String m3u8Data) {
=======
  /**
   * check is master playlist base on "#EXT-X-STREAM-INF"
   * */
  public boolean isMasterPlaylist(String m3u8Data){
    return m3u8Data.contains("#EXT-X-STREAM-INF");
  }

  /**
   * check is media playlist base on "#EXTINF"
   * */
  public boolean isMediaPlaylist(String m3u8Data){
>>>>>>> 0ddc022 ([CDN-98] feature: support useParts P and new token rule)
    return m3u8Data.contains("#EXTINF");
  }

  public MasterPlaylist getMasterPlaylistFromOrigin(String m3u8Data) {
    try {
      MasterPlaylistParser parser = new MasterPlaylistParser();
      return parser.readPlaylist(m3u8Data);
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  public MediaPlaylist getMediaPlaylistFromOrigin(String m3u8Data) {
    try {
      MediaPlaylistParser parser = new MediaPlaylistParser();
      return parser.readPlaylist(m3u8Data);
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

}
