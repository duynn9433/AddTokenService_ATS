package com.viettel.vtnet.addtokenservice.service;

import com.viettel.vtnet.addtokenservice.config.LogConfig;
import io.lindstrom.m3u8.model.MasterPlaylist;
import io.lindstrom.m3u8.model.MediaPlaylist;
import io.lindstrom.m3u8.parser.MasterPlaylistParser;
import io.lindstrom.m3u8.parser.MediaPlaylistParser;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.ByteBuffer;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import javax.net.ssl.HttpsURLConnection;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;

@Service
@Log4j2
public class GetDataFromOriginService {

  private LogConfig logConfig;

  public GetDataFromOriginService(LogConfig logConfig) {
    this.logConfig = logConfig;
  }

  public String getDataFromOriginHTTPS(String originUrl, boolean isHTTPS){
    HttpURLConnection connection = null;
    try {
      URL url = new URL(originUrl);

      if (isHTTPS) {
        connection = (HttpsURLConnection) url.openConnection();
      } else {
        connection = (HttpURLConnection) url.openConnection();
      }

      if (logConfig.isLogDebug()) {
        //for check HIT/MISS
        connection.setRequestProperty("X-Debug", "X-Cache, X-Cache-Key");
        // Get the response headers
        Map<String, List<String>> headers = connection.getHeaderFields();

        // Iterate through the headers and find the desired ones
        for (Entry<String, List<String>> entry : headers.entrySet()) {
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
      }
      try(ReadableByteChannel readableByteChannel = Channels.newChannel(connection.getInputStream())){
        ByteBuffer buffer = ByteBuffer.allocate(8192); // 8KB
        StringBuilder stringBuilder = new StringBuilder();

        while (readableByteChannel.read(buffer) > 0) {
          buffer.flip();
          while (buffer.hasRemaining()) {
            stringBuilder.append((char) buffer.get());
          }
          buffer.clear();
        }
        return stringBuilder.toString();
      } catch (IOException e) {
        throw new RuntimeException(e);
      }
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
    finally {
      if(connection != null){
        connection.disconnect();
      }
    }
  }

  /**
   * check is master playlist base on "#EXT-X-STREAM-INF"
   */
  public boolean isMasterPlaylist(String m3u8Data) {
    return m3u8Data.contains("#EXT-X-STREAM-INF");
  }

  /**
   * check is media playlist base on "#EXTINF"
   */
  public boolean isMediaPlaylist(String m3u8Data) {
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
