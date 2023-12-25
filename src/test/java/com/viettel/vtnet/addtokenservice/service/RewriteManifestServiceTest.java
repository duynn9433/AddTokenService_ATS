package com.viettel.vtnet.addtokenservice.service;

import static org.junit.jupiter.api.Assertions.*;

import com.viettel.vtnet.addtokenservice.common.MacAlgorithm;
import io.lindstrom.m3u8.model.MasterPlaylist;
import io.lindstrom.m3u8.model.MediaPlaylist;
import io.lindstrom.m3u8.parser.MasterPlaylistParser;
import io.lindstrom.m3u8.parser.MediaPlaylistParser;
import java.io.IOException;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
class RewriteManifestServiceTest {
  @Autowired
  RewriteManifestService rewriteManifestService;
  @Autowired
  GetDataFromOriginService getDataFromOriginService;


  @Test
  void rewriteMasterPlaylist() throws Exception {
    String masterUrl = "https://cdnvt.net/hls-stream/test/master.m3u8";
    MasterPlaylist originMasterPlaylist = getDataFromOriginService.getMasterPlaylistFromOrigin(masterUrl);
    System.out.println("-------------origin master playlist-----------------");
    System.out.println(new MasterPlaylistParser().writePlaylistAsString(originMasterPlaylist));
    System.out.println("-------------rewrite master playlist-----------------");
    MasterPlaylist masterPlaylist = rewriteManifestService.rewriteMasterPlaylist(
        originMasterPlaylist,
        "uid",30L,1, MacAlgorithm.HmacSHA1);
    System.out.println(new MasterPlaylistParser().writePlaylistAsString(masterPlaylist));
  }

  @Test
  void rewriteMediaPlaylist() throws Exception {
    String mediaUrl = "https://cdnvt.net/hls-stream/test/144p_index.m3u8";
    System.out.println("-------------origin media playlist-----------------");
    MediaPlaylist originMediaPlaylist = getDataFromOriginService.getMediaPlaylistFromOrigin(mediaUrl);
    System.out.println(new MediaPlaylistParser().writePlaylistAsString(originMediaPlaylist));
    System.out.println("-------------rewrite media playlist-----------------");
    MediaPlaylist mediaPlaylist = rewriteManifestService.rewriteMediaPlaylist("urlPrefix",
        originMediaPlaylist,"uid",30,1,MacAlgorithm.HmacSHA1);
    System.out.println(new MediaPlaylistParser().writePlaylistAsString(mediaPlaylist));
  }
}