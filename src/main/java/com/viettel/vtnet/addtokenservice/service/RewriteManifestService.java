package com.viettel.vtnet.addtokenservice.service;

import static com.viettel.vtnet.addtokenservice.common.UrlUtil.generateInfoForUrlSignPlugin;
import static com.viettel.vtnet.addtokenservice.common.UrlUtil.isHaveHttpSchema;

import com.viettel.vtnet.addtokenservice.common.HMACUtil;
import com.viettel.vtnet.addtokenservice.common.MacAlgorithm;
import com.viettel.vtnet.addtokenservice.common.UrlUtil;
import com.viettel.vtnet.addtokenservice.config.UrlSigConfig;
import com.viettel.vtnet.addtokenservice.config.UrlSigConfigPool;
import io.lindstrom.m3u8.model.AlternativeRendition;
import io.lindstrom.m3u8.model.MasterPlaylist;
import io.lindstrom.m3u8.model.MediaPlaylist;
import io.lindstrom.m3u8.model.MediaSegment;
import io.lindstrom.m3u8.model.PartialSegment;
import io.lindstrom.m3u8.model.PreloadHint;
import io.lindstrom.m3u8.model.RenditionReport;
import io.lindstrom.m3u8.model.SegmentMap;
import io.lindstrom.m3u8.model.Variant;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import lombok.extern.log4j.Log4j2;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

@Service
@Log4j2
public class RewriteManifestService {
  public MasterPlaylist rewriteMasterPlaylist(
      MasterPlaylist originMasterPlaylist,
      String baseUrl,
      String requestParam,
      UrlSigConfig urlSigConfig
  ) {
    //video-playlist
    List<Variant> variants = originMasterPlaylist.variants();
    List<Variant> updatedVariants = new ArrayList<>();
    for (int i = 0; i < variants.size(); i++) {
      Variant v = variants.get(i);
      //hash
      String urlWithToken = generateUrl(baseUrl, requestParam, v.uri(), urlSigConfig);
      //end-hash
      Variant updatedVariant = Variant.builder().from(v).uri(urlWithToken).build();
      updatedVariants.add(updatedVariant);
    }
    //end-video-playlist
    //audio-playlist
    List<AlternativeRendition> alternativeRenditionList = originMasterPlaylist.alternativeRenditions();
    List<AlternativeRendition> updateARList = new ArrayList<>();
    for (int i = 0; i < alternativeRenditionList.size(); i++) {
      AlternativeRendition ar = alternativeRenditionList.get(i);
      //hash
      String urlWithToken = "";
      if (ar.uri().isPresent()) {
        urlWithToken = generateUrl(baseUrl, requestParam, ar.uri().get(), urlSigConfig);
      }
      //end-hash
      AlternativeRendition updateAR = AlternativeRendition.builder().from(ar).uri(urlWithToken)
          .build();
      updateARList.add(updateAR);
    }
    //end-audio-playlist
    originMasterPlaylist = MasterPlaylist.builder().from(originMasterPlaylist)
        .variants(updatedVariants)
        .alternativeRenditions(updateARList)
        .build();
    return originMasterPlaylist;
  }

  public MediaPlaylist rewriteMediaPlaylist(
      MediaPlaylist originMediaPlaylist,
      String baseUrl,
      String requestParam,
      UrlSigConfig urlSigConfig
  ) {
    //EXTINF
    List<MediaSegment> segments = originMediaPlaylist.mediaSegments();


    List<MediaSegment> updatedSegments = new ArrayList<>();
    for (int i = 0; i < segments.size(); i++) {
      MediaSegment segment = segments.get(i);
      //segment map
        //EXT-X-MAP
      Optional<SegmentMap> segmentMap = segment.segmentMap();
      Optional<SegmentMap> updatedSegmentMap = Optional.empty();
      if (segmentMap.isPresent()) {
        SegmentMap sm = segmentMap.get();
        //hash
        String urlWithToken = generateUrl(baseUrl, requestParam, sm.uri(), urlSigConfig);
        //end-hash
        updatedSegmentMap = Optional.of(SegmentMap.builder().from(sm).uri(urlWithToken).build());
      }
        //end-EXT-X-MAP

      //end-segment map
      //hash
      String urlWithToken = generateUrl(baseUrl, requestParam, segment.uri(), urlSigConfig);
      //end-hash
      if(updatedSegmentMap.isPresent()){
        MediaSegment updatedMediaSegment = MediaSegment.builder().from(segment).uri(urlWithToken)
            .segmentMap(updatedSegmentMap).build();
        updatedSegments.add(updatedMediaSegment);
        continue;
      }
      MediaSegment updatedMediaSegment = MediaSegment.builder().from(segment).uri(urlWithToken)
          .build();
      updatedSegments.add(updatedMediaSegment);
    }
    //end-EXTINF
    //X-PART
    List<PartialSegment> partialSegments = originMediaPlaylist.partialSegments();
    List<PartialSegment> updatedPartialSegments = new ArrayList<>();
    for (int i = 0; i < partialSegments.size(); i++) {
      PartialSegment partialSegment = partialSegments.get(i);
      //hash
      String urlWithToken = generateUrl(baseUrl, requestParam, partialSegment.uri(), urlSigConfig);
      //end-hash
      PartialSegment updatedPartialSegment = PartialSegment.builder().from(partialSegment)
          .uri(urlWithToken).build();
      updatedPartialSegments.add(updatedPartialSegment);
    }
    //end-X-PART
    //PreloadHint
    Optional<PreloadHint> preloadHints = originMediaPlaylist.preloadHint();
    Optional<PreloadHint> updatedPreloadHints = Optional.empty();
    if (preloadHints.isPresent()) {
      PreloadHint preloadHint = preloadHints.get();
      //hash
      String urlWithToken = generateUrl(baseUrl, requestParam, preloadHint.uri(), urlSigConfig);
      //end-hash
      updatedPreloadHints = Optional.of(PreloadHint.builder().from(preloadHint).uri(urlWithToken)
          .build());
    }
    //end-PreloadHint
    //X-RENDITION-REPORT
    List<RenditionReport> renditionReports = originMediaPlaylist.renditionReports();
    List<RenditionReport> updatedRenditionReports = new ArrayList<>();
    for (int i = 0; i < renditionReports.size(); i++) {
      RenditionReport renditionReport = renditionReports.get(i);
      //hash
      String urlWithToken = generateUrl(baseUrl, requestParam, renditionReport.uri(),
          urlSigConfig);
      //end-hash
      RenditionReport updatedRenditionReport = RenditionReport.builder().from(renditionReport)
          .uri(urlWithToken).build();
      updatedRenditionReports.add(updatedRenditionReport);
    }
    //end-X-RENDITION-REPORT

    return MediaPlaylist.builder().from(originMediaPlaylist)
        .mediaSegments(updatedSegments)
        .partialSegments(updatedPartialSegments)
        .preloadHint(updatedPreloadHints)
        .renditionReports(updatedRenditionReports)
        .build();
  }

  /**
   * generate token for Url_sig plugin 2 case: <br> 1. master playlist: <br> urlInM3u8:
   * 144p_index.m3u8 <br> 2. media playlist: <br> urlInM3u8:
   * https://cdnvt.net/hls-stream/test/144p_segment13102.ts <br>
   * <br>
   * Example data for Url_sig plugin: <br>
   * cdnvt.net/hls-stream/test/144p_segment13102.ts?timestamp=213&uid=uid123&E=1703155339&A=1&K=4&P=1&S=608baf47ce4d76be52eb6488bce29f9ea4cfc2ed
   * <br>
   *
   * @param baseUrl:      base m3u8 url :
   *                      (http://192.168.122.32/)foo/asdfasdf/adsf.m3u8?timestamp=213&uid=uid123&E=1703155339&A=1&K=4&P=1&S=608baf47ce4d76be52eb6488bce29f9ea4cfc2ed
   * @param m3u8FileData: url in m3u8 file: 144p_index.m3u8?a=1&b=2 ||
   *                      https://cdnvt.net/hls-stream/test/144p_segment13102.ts?a=1&b=2
   * @param urlSigConfig: algorithm : HmacSHA1 <br>
   * key number : 4 <br>
   *   use parts : [false, false, true, true]<br>
   * list hash query param : [timestamp, uid]<br>
   */
  private String generateUrl(String baseUrl, String requestParam, String m3u8FileData,
      UrlSigConfig urlSigConfig) {

    //remove query param in m3u8 file data
    if (m3u8FileData.contains("?")) {
      m3u8FileData = m3u8FileData.substring(0, m3u8FileData.indexOf("?"));
    }

    /*get use path*/
    String usePath = UrlUtil.getUseParts(baseUrl, urlSigConfig.getUseParts());
    log.debug("usePath: " + usePath);
    //remove master.m3u8
    usePath = usePath.substring(0, usePath.lastIndexOf("/"));
    /*get hash query param*/
    String hashQueryParam = UrlUtil.getHashQueryParamWithValue(requestParam,
        urlSigConfig.getHashQueryParams());

    String hashData = usePath + "/" + m3u8FileData + "?" + hashQueryParam;
    //generate token
    //get key
    String token = generateToken(hashData, urlSigConfig.getKey(), urlSigConfig.getAlgorithm());
    log.debug("generate data: " + hashData );
    log.debug("token: " + token);
    log.debug("keyNumber: " + urlSigConfig.getKeyNumber());
    log.debug("algorithm: " + urlSigConfig.getAlgorithm());
    /* hash data != return url data
     * return MUST include all query param + new token
     * */
    //String schema = isHaveHttpSchema == 0 ? "" : (isHaveHttpSchema == 1 ? "http://" : "https://");
    StringBuilder returnData = new StringBuilder(m3u8FileData)
        .append("?")
        .append(requestParam)
        .append("&")
        .append("token=").append(token);
    log.debug("return data: " + returnData);
    return returnData.toString();
  }

  private String generateToken(String data, String key, MacAlgorithm algorithm) {
    try {
      return HMACUtil.hmacWithJava(algorithm, data, key);
    } catch (NoSuchAlgorithmException | InvalidKeyException e) {
      throw new RuntimeException(e);
    }
  }
}
