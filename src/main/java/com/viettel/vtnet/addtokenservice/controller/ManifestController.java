package com.viettel.vtnet.addtokenservice.controller;

import com.viettel.vtnet.addtokenservice.common.MacAlgorithm;
import com.viettel.vtnet.addtokenservice.config.UrlSigConfig;
import com.viettel.vtnet.addtokenservice.config.UrlSigConfigPool;
import com.viettel.vtnet.addtokenservice.service.GetDataFromOriginService;
import com.viettel.vtnet.addtokenservice.service.RewriteManifestService;
import io.lindstrom.m3u8.model.MasterPlaylist;
import io.lindstrom.m3u8.model.MediaPlaylist;
import io.lindstrom.m3u8.parser.MasterPlaylistParser;
import io.lindstrom.m3u8.parser.MediaPlaylistParser;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.log4j.Log4j2;
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
  private GetDataFromOriginService getDataFromOriginService;
  private RewriteManifestService rewriteManifestService;
  private UrlSigConfigPool urlSigConfigPool;

  public ManifestController(
      GetDataFromOriginService getDataFromOriginService,
      RewriteManifestService rewriteManifestService,
      UrlSigConfigPool urlSigConfigPool) {
    this.getDataFromOriginService = getDataFromOriginService;
    this.rewriteManifestService = rewriteManifestService;
    this.urlSigConfigPool = urlSigConfigPool;
  }

  @CrossOrigin
  @GetMapping(value = "/**/{filename}.m3u8", produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
  public ResponseEntity<?> getM3U8File(HttpServletRequest request, HttpServletResponse response,
     @PathVariable String filename,
     @RequestParam(required = false) String uid,
     @RequestParam Long timestamp) {
    Long startTime = System.currentTimeMillis();
    log.debug("getMediaPlaylist: " + request.getRequestURL().toString());
    //response file name
    response.setHeader("Content-Disposition", "attachment; filename="+ filename + ".m3u8");
    /**URL and get m3u8 data*/
    String schema = request.getScheme();
    String domain = request.getRemoteHost();
    log.debug("remote host: " + domain +  " " + request.getRemoteAddr());
    String requestParam = request.getQueryString();
    String requestURI = request.getRequestURI();
    String clientServiceName = requestURI.substring(1, requestURI.indexOf("/",1));
    UrlSigConfig urlSigConfig = urlSigConfigPool.getUrlSigConfig(clientServiceName);
    String url = urlSigConfigPool.getOrigin(clientServiceName) + requestURI ;
    log.debug("START get data from origin " + url);

    String m3u8Data = schema.equals("https")
        ? getDataFromOriginService.getDataFromOriginHTTPS(url+ "?" + requestParam, true)
        : getDataFromOriginService.getDataFromOriginHTTPS(url+ "?" + requestParam, false);

    log.debug("END get data from origin" + m3u8Data );
    if(getDataFromOriginService.isMasterPlaylist(m3u8Data)) {
      //master
      MasterPlaylist masterPlaylist = getDataFromOriginService.getMasterPlaylistFromOrigin(m3u8Data);
      masterPlaylist = rewriteManifestService.rewriteMasterPlaylist(
              masterPlaylist,
              domain + requestURI,
              requestParam,
              urlSigConfig);
      MasterPlaylistParser masterPlaylistParser = new MasterPlaylistParser();

      Long endTime = System.currentTimeMillis();
      System.out.println("Spent time: " + (endTime - startTime));

      return ResponseEntity.ok(masterPlaylistParser.writePlaylistAsBytes(masterPlaylist));
    } else if(getDataFromOriginService.isMediaPlaylist(m3u8Data)) {
      //media
      MediaPlaylist mediaPlaylist = getDataFromOriginService.getMediaPlaylistFromOrigin(m3u8Data);
      mediaPlaylist = rewriteManifestService.rewriteMediaPlaylist(
              mediaPlaylist,
              domain + requestURI,
              requestParam,
              urlSigConfig);
      MediaPlaylistParser mediaPlaylistParser = new MediaPlaylistParser();

      Long endTime = System.currentTimeMillis();
      System.out.println("Spent time: " + (endTime - startTime));

      return ResponseEntity.ok(mediaPlaylistParser.writePlaylistAsBytes(mediaPlaylist));
    } else {
      //TODO: not support
      return ResponseEntity.badRequest().body("Not support");
    }
  }
}
