package com.viettel.vtnet.addtokenservice.service;

import io.lindstrom.m3u8.model.MasterPlaylist;
import io.lindstrom.m3u8.model.MediaPlaylist;
import io.lindstrom.m3u8.parser.MasterPlaylistParser;
import io.lindstrom.m3u8.parser.MediaPlaylistParser;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import javax.net.ssl.HttpsURLConnection;
import java.net.HttpURLConnection;
import java.util.List;
import java.util.Map;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;

@Service
@Log4j2
public class GetDataFromOriginService {

  public String getDataFromOriginHTTPS(String originUrl, boolean isHTTPS) {
    try {
      URL url = new URL(originUrl);
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

      BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
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
