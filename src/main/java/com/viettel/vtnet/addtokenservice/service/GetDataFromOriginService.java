package com.viettel.vtnet.addtokenservice.service;

import io.lindstrom.m3u8.model.MasterPlaylist;
import io.lindstrom.m3u8.model.MediaPlaylist;
import io.lindstrom.m3u8.model.Playlist;
import io.lindstrom.m3u8.parser.MasterPlaylistParser;
import io.lindstrom.m3u8.parser.MediaPlaylistParser;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Paths;
import javax.print.attribute.standard.Media;
import org.springframework.stereotype.Service;

@Service
public class GetDataFromOriginService {

  //  public String
  public static void main(String[] args) throws IOException {
    String masterUrl = "https://cdnvt.net/hls-stream/test/master.m3u8";
    String mediaUrl = "https://cdnvt.net/hls-stream/test/144p_index.m3u8";
    MediaPlaylistParser mediaPlaylistParser = new MediaPlaylistParser();
    MediaPlaylist mediaPlaylist = new GetDataFromOriginService().getMediaPlaylistFromOrigin(mediaUrl);
    System.out.println(mediaPlaylistParser.writePlaylistAsString(mediaPlaylist));
  }

  public MasterPlaylist getMasterPlaylistFromOrigin(String masterPlaylistUrl) throws IOException {
    URL url = new URL(masterPlaylistUrl);
    HttpURLConnection connection = (HttpURLConnection) url.openConnection();
    MasterPlaylistParser parser = new MasterPlaylistParser();
    return parser.readPlaylist(connection.getInputStream());
  }
  public MediaPlaylist getMediaPlaylistFromOrigin(String mediaPlaylistUrl)
      throws IOException {
    URL url = new URL(mediaPlaylistUrl);
    HttpURLConnection connection = (HttpURLConnection) url.openConnection();
    MediaPlaylistParser parser = new MediaPlaylistParser();
    return parser.readPlaylist(connection.getInputStream());
  }

}
