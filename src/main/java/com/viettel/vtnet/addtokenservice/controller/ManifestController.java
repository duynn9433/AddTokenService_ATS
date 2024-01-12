package com.viettel.vtnet.addtokenservice.controller;

import com.viettel.vtnet.addtokenservice.common.MacAlgorithm;
import com.viettel.vtnet.addtokenservice.config.UrlHashConfig;
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
  private UrlHashConfig urlHashConfig;

  public ManifestController(Environment environment,
      GetDataFromOriginService getDataFromOriginService,
      RewriteManifestService rewriteManifestService, UrlHashConfig urlHashConfig) {
    this.environment = environment;
    this.getDataFromOriginService = getDataFromOriginService;
    this.rewriteManifestService = rewriteManifestService;
    this.urlHashConfig = urlHashConfig;
  }

<<<<<<< HEAD

<<<<<<< HEAD
  @GetMapping(value = "/**/{filename}.m3u8", produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
  public ResponseEntity<?> getM3U8File(HttpServletRequest request, HttpServletResponse response,
     @PathVariable String filename,
     @RequestParam(required = false) String uid,
     @RequestParam Long timestamp,
     @RequestParam(required = false, defaultValue = "1") int key,
     @RequestParam(required = false, defaultValue = "1") int algo) {
    log.info("getMediaPlaylist: " + request.getRequestURL().toString());
=======
  @GetMapping("/fake.ts")
  public ResponseEntity<?> getFakeTs() {
    return ResponseEntity.ok("fake.ts");
  }

  @GetMapping(value = "/{type}/{env}/{source}",
      produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
  public ResponseEntity<?> getPlaylist(HttpServletRequest request, HttpServletResponse response,
=======

  @CrossOrigin
  @GetMapping(value = "/**/{filename}.m3u8", produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
  public ResponseEntity<?> getM3U8File(HttpServletRequest request, HttpServletResponse response,
     @PathVariable String filename,
     @RequestParam(required = false) String uid,
<<<<<<< HEAD
     @RequestParam Long timestamp,
     @RequestParam(required = false, defaultValue = "1") int key,
     @RequestParam(required = false, defaultValue = "1") int algo) {
    log.info("getMediaPlaylist: " + request.getRequestURL().toString());
<<<<<<< HEAD

    String orignUrl = request.getRequestURI();
    orignUrl = orignUrl.substring(1);
//    System.out.println(orignUrl);
    String url = environment.getProperty("netCDN.origin") + "/" +orignUrl;
//    System.out.println(url);

    MacAlgorithm algorithm = MacAlgorithm.getByAlgorithmNumber(algo);
    response.setHeader("Content-Disposition", "attachment; filename="+ filename + ".m3u8");
    log.info("START get data from origin " + url);
    String m3u8Data = getDataFromOriginService.getDataFromOrigin(url);
    log.info("END get data from origin" + m3u8Data );
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
              mediaPlaylist, orignUrl,
              uid, timestamp, key, algorithm);
      MediaPlaylistParser mediaPlaylistParser = new MediaPlaylistParser();
      return ResponseEntity.ok(mediaPlaylistParser.writePlaylistAsBytes(mediaPlaylist));
    } else {
      //TODO: not support
      return ResponseEntity.badRequest().body("Not support");
    }
  }

//  @GetMapping(value = "/{type}/{env}/{quality}/{source}",
//      produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
  public ResponseEntity<?> getMediaPlaylist(HttpServletRequest request, HttpServletResponse response,
>>>>>>> 46a6f09 (brk origin)
      @PathVariable String type,
      @PathVariable String env,
      @PathVariable String source) {
    String orignUrl = request.getRequestURL().toString();
//    System.out.println(orignUrl);
    String url = environment.getProperty("netCDN.origin") + "/" + type + "/" + env + "/" + source;
//    System.out.println(url);
>>>>>>> 50a9ca8 (save)

=======
    String schema = request.getScheme();
>>>>>>> 2340af7 ([CDN-98] feature: add HIT/MISS log)
    String orignURI = request.getRequestURI();
    orignURI = orignURI.substring(1);
    //remove first part for 0011
    String hashURI = orignURI.substring(orignURI.indexOf('/') +1);

    String url = environment.getProperty("netCDN.origin") + "/" +orignURI;

    MacAlgorithm algorithm = MacAlgorithm.getByAlgorithmNumber(algo);
    response.setHeader("Content-Disposition", "attachment; filename="+ filename + ".m3u8");
    log.info("START get data from origin " + url);
<<<<<<< HEAD
    String m3u8Data = getDataFromOriginService.getDataFromOrigin(url);
<<<<<<< HEAD
=======
=======
     @RequestParam Long timestamp) {
    log.debug("getMediaPlaylist: " + request.getRequestURL().toString());
    //response file name
    response.setHeader("Content-Disposition", "attachment; filename="+ filename + ".m3u8");
    /**Algorithm*/
    MacAlgorithm algorithm = urlHashConfig.getAlgorithm();
    /**Key*/
    int keyNumber = urlHashConfig.getKeyNumber();
    /**URL and get m3u8 data*/
    String schema = request.getScheme();
    String requestURL = request.getRequestURL().toString();
    String requestParam = request.getQueryString();
    String requestURI = request.getRequestURI();
    String url = environment.getProperty("netCDN.origin") +requestURI;
    log.debug("START get data from origin " + url);
>>>>>>> 0ddc022 ([CDN-98] feature: support useParts P and new token rule)
    String m3u8Data;
    if(schema.equals("https")) {
      m3u8Data = getDataFromOriginService.getDataFromOriginHTTPS(url, true);
    } else {
      m3u8Data = getDataFromOriginService.getDataFromOriginHTTPS(url, false);
    }
<<<<<<< HEAD
>>>>>>> 2340af7 ([CDN-98] feature: add HIT/MISS log)
    log.info("END get data from origin" + m3u8Data );
=======
    log.info("END get data from origin" + environment.getProperty("netCDN.origin"));
>>>>>>> 46a6f09 (brk origin)
=======
    log.debug("END get data from origin" + m3u8Data );
>>>>>>> 0ddc022 ([CDN-98] feature: support useParts P and new token rule)
    if(getDataFromOriginService.isMasterPlaylist(m3u8Data)) {
      //master
      MasterPlaylist masterPlaylist = getDataFromOriginService.getMasterPlaylistFromOrigin(m3u8Data);
      masterPlaylist = rewriteManifestService.rewriteMasterPlaylist(
<<<<<<< HEAD
<<<<<<< HEAD
              masterPlaylist,
<<<<<<< HEAD
              hashURI ,
              uid, timestamp, key, algorithm);
=======
          masterPlaylist,
          request.getRequestURI() ,
          uid, timestamp, K, A);
>>>>>>> 50a9ca8 (save)
=======
              masterPlaylist,
              orignUrl ,
              uid, timestamp, key, algorithm);
>>>>>>> 46a6f09 (brk origin)
=======
              requestURL, requestParam,
              timestamp, algorithm, keyNumber, urlHashConfig.getUseParts(), urlHashConfig.getHashQueryParams());
>>>>>>> 0ddc022 ([CDN-98] feature: support useParts P and new token rule)
      MasterPlaylistParser masterPlaylistParser = new MasterPlaylistParser();
      return ResponseEntity.ok(masterPlaylistParser.writePlaylistAsBytes(masterPlaylist));
    } else if(getDataFromOriginService.isMediaPlaylist(m3u8Data)) {
      //media
      MediaPlaylist mediaPlaylist = getDataFromOriginService.getMediaPlaylistFromOrigin(m3u8Data);
      mediaPlaylist = rewriteManifestService.rewriteMediaPlaylist(
<<<<<<< HEAD
<<<<<<< HEAD
              mediaPlaylist, hashURI,
              uid, timestamp, key, algorithm);
=======
          mediaPlaylist, "this version not use because in media playlist have full url",
          uid, timestamp, K, A);
>>>>>>> 50a9ca8 (save)
=======
              mediaPlaylist, requestURL, requestParam,
          timestamp, algorithm, keyNumber, urlHashConfig.getUseParts(), urlHashConfig.getHashQueryParams());
>>>>>>> 0ddc022 ([CDN-98] feature: support useParts P and new token rule)
      MediaPlaylistParser mediaPlaylistParser = new MediaPlaylistParser();
      return ResponseEntity.ok(mediaPlaylistParser.writePlaylistAsBytes(mediaPlaylist));
    } else {
      //TODO: not support
      return ResponseEntity.badRequest().body("Not support");
    }
  }
<<<<<<< HEAD
=======
//  @GetMapping(value = "/{type}/{env}/{source}",
//      produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
  public ResponseEntity<?> getMasterPlaylist(HttpServletRequest request, HttpServletResponse response,
      @PathVariable String type,
      @PathVariable String env,
      @PathVariable String source,
      @RequestParam(required = false) String uid,
      @RequestParam Long timestamp,
      @RequestParam(required = false, defaultValue = "1") int key,
      @RequestParam(required = false, defaultValue = "1") int algo) {
    log.info("getMasterPlaylist: " + request.getRequestURL().toString());
    String orignUrl = request.getRequestURI();
    orignUrl = orignUrl.substring(1);

//    System.out.println(orignUrl);
    String url = environment.getProperty("netCDN.origin") + "/" + type + "/" + env + "/" + source;
//    System.out.println(url);

    MacAlgorithm algorithm = MacAlgorithm.getByAlgorithmNumber(algo);
    response.setHeader("Content-Disposition", "attachment; filename="+source);
    log.info("START get data from origin" + environment.getProperty("netCDN.origin"));
    String m3u8Data = getDataFromOriginService.getDataFromOrigin(url);
    log.info("END get data from origin" + environment.getProperty("netCDN.origin"));
    if(getDataFromOriginService.isMasterPlaylist(m3u8Data)) {
      //master
      MasterPlaylist masterPlaylist = getDataFromOriginService.getMasterPlaylistFromOrigin(m3u8Data);
      masterPlaylist = rewriteManifestService.rewriteMasterPlaylist(
          masterPlaylist,
          orignUrl ,
          uid, timestamp, key, algorithm);
      MasterPlaylistParser masterPlaylistParser = new MasterPlaylistParser();
      return ResponseEntity.ok(masterPlaylistParser.writePlaylistAsBytes(masterPlaylist));
    } else {
      //TODO: not support
      return ResponseEntity.badRequest().body("Not support");
    }
  }

>>>>>>> 46a6f09 (brk origin)
}
