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
import org.springframework.stereotype.Service;

@Service
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

  public String getDataFromOrigin(String originUrl) {
    try {
      URL url = new URL(originUrl);
      HttpsURLConnection connection = (HttpsURLConnection) url.openConnection();

      BufferedReader reader = new BufferedReader(
          new InputStreamReader(connection.getInputStream()));
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

  public boolean isMasterPlaylist(String m3u8Data) {
    return m3u8Data.contains("#EXT-X-STREAM-INF");
  }

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
