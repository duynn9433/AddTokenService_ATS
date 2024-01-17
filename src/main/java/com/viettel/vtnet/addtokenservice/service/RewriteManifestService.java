package com.viettel.vtnet.addtokenservice.service;

import static com.viettel.vtnet.addtokenservice.common.UrlUtil.generateInfoForUrlSignPlugin;
import static com.viettel.vtnet.addtokenservice.common.UrlUtil.isHaveHttpSchema;

import com.viettel.vtnet.addtokenservice.common.HMACUtil;
import com.viettel.vtnet.addtokenservice.common.MacAlgorithm;
import com.viettel.vtnet.addtokenservice.common.UrlUtil;
import io.lindstrom.m3u8.model.AlternativeRendition;
import io.lindstrom.m3u8.model.MasterPlaylist;
import io.lindstrom.m3u8.model.MediaPlaylist;
import io.lindstrom.m3u8.model.MediaSegment;
import io.lindstrom.m3u8.model.Variant;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;
import lombok.extern.log4j.Log4j2;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

@Service
@Log4j2
public class RewriteManifestService {

  private Environment env;

  public RewriteManifestService(Environment env) {
    this.env = env;
  }

  public MasterPlaylist rewriteMasterPlaylist(
      MasterPlaylist originMasterPlaylist,
      String baseUrl,
      String requestParam,
      Long expiration,
      MacAlgorithm macAlgorithm,
      int keyNumber,
      Boolean[] useParts,
      List<String> listHashQueryParam) {
    //video-playlist
    List<Variant> variants = originMasterPlaylist.variants();
    List<Variant> updatedVariants = new ArrayList<>();
    for (int i = 0; i < variants.size(); i++) {
      Variant v = variants.get(i);
      //hash
      String urlWithToken = generateUrl(baseUrl, requestParam, v.uri(),
          expiration, macAlgorithm, keyNumber, useParts, listHashQueryParam, false);
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
        urlWithToken = generateUrl(baseUrl, requestParam, ar.uri().get(),
            expiration, macAlgorithm, keyNumber, useParts,
            new ArrayList<>(), false);
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
      long expiration,
      MacAlgorithm macAlgorithm,
      int keyNumber,
      Boolean[] useParts,
      List<String> listHashQueryParam) {
    List<MediaSegment> segments = originMediaPlaylist.mediaSegments();
    List<MediaSegment> updatedSegments = new ArrayList<>();
    for (int i = 0; i < segments.size(); i++) {
      MediaSegment segment = segments.get(i);

      //hash
      String urlWithToken = generateUrl(baseUrl, requestParam, segment.uri(),
          expiration, macAlgorithm, keyNumber, useParts,
          listHashQueryParam, true);
      //end-hash
      MediaSegment updatedMediaSegment = MediaSegment.builder().from(segment).uri(urlWithToken)
          .build();
      updatedSegments.add(updatedMediaSegment);
    }
    return MediaPlaylist.builder().from(originMediaPlaylist).mediaSegments(updatedSegments).build();
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
   * @param expiration:   time to live : 1703155339L
   * @param macAlgorithm: algorithm : HmacSHA1
   * @param keyNumber:    key number : 4
   * @param useParts:     use parts : [false, false, true, true]
   * @param listHashQueryParam: list hash query param : [timestamp, uid]
   */
  private String generateUrl(String baseUrl, String requestParam, String m3u8FileData,
      long expiration,
      MacAlgorithm macAlgorithm,
      int keyNumber,
      Boolean[] useParts,
      List<String> listHashQueryParam,
      boolean isMediaPlaylist) {

    //remove query param in m3u8 file data
    if (m3u8FileData.contains("?")) {
      m3u8FileData = m3u8FileData.substring(0, m3u8FileData.indexOf("?"));
    }

    /*get use path*/
    String usePath = UrlUtil.getUseParts(baseUrl, useParts);
    log.debug("usePath: " + usePath);
    //remove master.m3u8
    usePath = usePath.substring(0, usePath.lastIndexOf("/"));
    /*get hash query param*/
    String hashQueryParam = UrlUtil.getHashQueryParamWithValue(requestParam,
        listHashQueryParam);

    String hashData = usePath + "/" + m3u8FileData + "?" + hashQueryParam;
    //generate token
    String token = generateToken(hashData, keyNumber, macAlgorithm);
    log.debug("generate data: " + hashData );
    log.debug("token: " + token);
    log.debug("keyNumber: " + keyNumber);
    log.debug("algorithm: " + macAlgorithm);
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

  private String generateToken(String data, int keyNumber, MacAlgorithm algorithm) {
    //get key
    //TODO: dynamic get key
    String key = env.getProperty("url_sig.key" + keyNumber);
    //generate token
    assert key != null;
    try {
      return HMACUtil.hmacWithJava(algorithm, data, key);
    } catch (NoSuchAlgorithmException | InvalidKeyException e) {
      throw new RuntimeException(e);
    }
  }
}
