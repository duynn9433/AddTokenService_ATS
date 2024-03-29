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
import lombok.extern.log4j.Log4j2;
import org.springframework.core.env.Environment;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController()
@Log4j2
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


  @CrossOrigin
  @GetMapping(value = "/**/{filename}.m3u8", produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
  public ResponseEntity<?> getM3U8File(HttpServletRequest request, HttpServletResponse response,
     @PathVariable String filename,
     @RequestParam(required = false) String uid,
     @RequestParam Long timestamp,
     @RequestParam(required = false, defaultValue = "1") int key,
     @RequestParam(required = false, defaultValue = "1") int algo) {
    log.info("getMediaPlaylist: " + request.getRequestURL().toString());
    String schema = request.getScheme();
    String orignURI = request.getRequestURI();
    orignURI = orignURI.substring(1);
    //remove first part for 0011
    String hashURI = orignURI.substring(orignURI.indexOf('/') +1);

    String url = environment.getProperty("netCDN.origin") + "/" +orignURI;

    MacAlgorithm algorithm = MacAlgorithm.getByAlgorithmNumber(algo);
    response.setHeader("Content-Disposition", "attachment; filename="+ filename + ".m3u8");
    log.info("START get data from origin " + url);
    String m3u8Data;
    if(schema.equals("https")) {
      m3u8Data = getDataFromOriginService.getDataFromOriginHTTPS(url, true);
    } else {
      m3u8Data = getDataFromOriginService.getDataFromOriginHTTPS(url, false);
    }
    log.info("END get data from origin" + m3u8Data );
    if(getDataFromOriginService.isMasterPlaylist(m3u8Data)) {
      //master
      MasterPlaylist masterPlaylist = getDataFromOriginService.getMasterPlaylistFromOrigin(m3u8Data);
      masterPlaylist = rewriteManifestService.rewriteMasterPlaylist(
              masterPlaylist,
              hashURI ,
              uid, timestamp, key, algorithm);
      MasterPlaylistParser masterPlaylistParser = new MasterPlaylistParser();
      return ResponseEntity.ok(masterPlaylistParser.writePlaylistAsBytes(masterPlaylist));
    } else if(getDataFromOriginService.isMediaPlaylist(m3u8Data)) {
      //media
      MediaPlaylist mediaPlaylist = getDataFromOriginService.getMediaPlaylistFromOrigin(m3u8Data);
      mediaPlaylist = rewriteManifestService.rewriteMediaPlaylist(
              mediaPlaylist, hashURI,
              uid, timestamp, key, algorithm);
      MediaPlaylistParser mediaPlaylistParser = new MediaPlaylistParser();
      return ResponseEntity.ok(mediaPlaylistParser.writePlaylistAsBytes(mediaPlaylist));
    } else {
      //TODO: not support
      return ResponseEntity.badRequest().body("Not support");
    }
  }
}
