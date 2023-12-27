package com.viettel.vtnet.addtokenservice.controller;

import com.viettel.vtnet.addtokenservice.common.MacAlgorithm;
import com.viettel.vtnet.addtokenservice.service.GetDataFromOriginService;
import com.viettel.vtnet.addtokenservice.service.RewriteManifestService;
import io.lindstrom.m3u8.model.MasterPlaylist;
import io.lindstrom.m3u8.model.MediaPlaylist;
import io.lindstrom.m3u8.parser.MasterPlaylistParser;
import io.lindstrom.m3u8.parser.MediaPlaylistParser;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.core.env.Environment;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController()
public class ManifestController {

  private Environment environment;
  private GetDataFromOriginService getDataFromOriginService;
  private RewriteManifestService rewriteManifestService;

  public ManifestController(Environment environment,
      GetDataFromOriginService getDataFromOriginService,
      RewriteManifestService rewriteManifestService) {
    this.environment = environment;
    this.getDataFromOriginService = getDataFromOriginService;
    this.rewriteManifestService = rewriteManifestService;
  }

  @GetMapping("/")
  public ResponseEntity<?> hello(){
    return ResponseEntity.ok("hello");
  }

  @GetMapping("/fake.ts")
  public ResponseEntity<?> getFakeTs() {
    return ResponseEntity.ok("fake.ts");
  }

  @GetMapping(value = "/{type}/{env}/{source}",
      produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
  public ResponseEntity<?> getPlaylist(HttpServletRequest request, HttpServletResponse response,
      @PathVariable String type,
      @PathVariable String env,
      @PathVariable String source,
      @RequestParam String uid,
      @RequestParam Long timestamp,
      @RequestParam(required = false, defaultValue = "1") int key,
      @RequestParam(required = false, defaultValue = "1") int algo) {
    String orignUrl = request.getRequestURL().toString();
//    System.out.println(orignUrl);
    String url = environment.getProperty("netCDN.origin") + "/" + type + "/" + env + "/" + source;
//    System.out.println(url);

    MacAlgorithm algorithm = MacAlgorithm.getByAlgorithmNumber(algo);
    response.setHeader("Content-Disposition", "attachment; filename="+source);

    String m3u8Data = getDataFromOriginService.getDataFromOrigin(url);
    if(getDataFromOriginService.isMasterPlaylist(m3u8Data)) {
      //master
      MasterPlaylist masterPlaylist = getDataFromOriginService.getMasterPlaylistFromOrigin(m3u8Data);
      masterPlaylist = rewriteManifestService.rewriteMasterPlaylist(
          masterPlaylist,
          orignUrl ,
          uid, timestamp, key, algorithm);
      MasterPlaylistParser masterPlaylistParser = new MasterPlaylistParser();
      return ResponseEntity.ok(masterPlaylistParser.writePlaylistAsBytes(masterPlaylist));
    } else if(getDataFromOriginService.isMediaPlaylist(m3u8Data)) {
      //media
      MediaPlaylist mediaPlaylist = getDataFromOriginService.getMediaPlaylistFromOrigin(m3u8Data);
      mediaPlaylist = rewriteManifestService.rewriteMediaPlaylist(
          mediaPlaylist, "this version not use because in media playlist have full url",
          uid, timestamp, key, algorithm);
      MediaPlaylistParser mediaPlaylistParser = new MediaPlaylistParser();
      return ResponseEntity.ok(mediaPlaylistParser.writePlaylistAsBytes(mediaPlaylist));
    } else {
      //TODO: not support
      return ResponseEntity.badRequest().body("Not support");
    }
  }

}
