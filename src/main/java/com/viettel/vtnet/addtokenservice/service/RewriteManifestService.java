package com.viettel.vtnet.addtokenservice.service;

import static com.viettel.vtnet.addtokenservice.common.UrlUtil.concatHttpsSchema;
import static com.viettel.vtnet.addtokenservice.common.UrlUtil.generateInfoForUrlSignPlugin;
import static com.viettel.vtnet.addtokenservice.common.UrlUtil.getUrlPrefixHaveSchemaForUrlSigPlugin;
import static com.viettel.vtnet.addtokenservice.common.UrlUtil.isHaveHttpSchema;

import com.viettel.vtnet.addtokenservice.common.HMACUtil;
import com.viettel.vtnet.addtokenservice.common.MacAlgorithm;
import io.lindstrom.m3u8.model.*;
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
      String originUrl,
      String uid,
      Long expiration,
      int keyNumber,
      MacAlgorithm macAlgorithm) {
    //video-playlist
    List<Variant> variants = originMasterPlaylist.variants();
    List<Variant> updatedVariants = new ArrayList<>();
    for (int i = 0; i < variants.size(); i++) {
      Variant v = variants.get(i);
      //hash
      String urlWithToken = generateUrl(originUrl, v.uri(), expiration, uid, keyNumber, macAlgorithm, false);
      //end-hash
      Variant updatedVariant = Variant.builder().from(v).uri(urlWithToken).build();
      updatedVariants.add(updatedVariant);
    }
    //end-video-playlist
    //audio-playlist
    List<AlternativeRendition> alternativeRenditionList = originMasterPlaylist.alternativeRenditions();
    List<AlternativeRendition> updateARList = new ArrayList<>();
    for(int i = 0; i < alternativeRenditionList.size(); i++ ){
      AlternativeRendition ar = alternativeRenditionList.get(i);
      //hash
      String urlWithToken = "";
      if(ar.uri().isPresent()) {
        urlWithToken = generateUrl(originUrl, ar.uri().get(), expiration, uid, keyNumber, macAlgorithm, false);
      }
      //end-hash
      AlternativeRendition updateAR = AlternativeRendition.builder().from(ar).uri(urlWithToken).build();
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
      String originUrl,
      String uid,
      long expiration,
      int keyNumber,
      MacAlgorithm macAlgorithm){
    List<MediaSegment> segments = originMediaPlaylist.mediaSegments();
    List<MediaSegment> updatedSegments = new ArrayList<>();
    for (int i = 0; i < segments.size(); i++) {
      MediaSegment segment = segments.get(i);

      //hash
      String urlWithToken = generateUrl(originUrl, segment.uri(), expiration, uid, keyNumber, macAlgorithm, true);
      //end-hash
      MediaSegment updatedMediaSegment = MediaSegment.builder().from(segment).uri(urlWithToken)
          .build();
      updatedSegments.add(updatedMediaSegment);
    }
    return MediaPlaylist.builder().from(originMediaPlaylist).mediaSegments(updatedSegments).build();
  }

  /**
   * generate token for Url_sig plugin
   * 2 case: <br>
   * 1. master playlist: <br>
   * urlInM3u8: 144p_index.m3u8 <br>
   * 2. media playlist: <br>
   * urlInM3u8: https://cdnvt.net/hls-stream/test/144p_segment13102.ts <br>
   * <br>
   * Example data for Url_sig plugin: <br>
   * cdnvt.net/hls-stream/test/144p_segment13102.ts?timestamp=213&uid=uid123&E=1703155339&A=1&K=4&P=1&S=608baf47ce4d76be52eb6488bce29f9ea4cfc2ed
   * <br>
   * @param originUrl: origin url : http://192.168.122.32/foo/asdfasdf/adsf.m3u8?timestamp=213&uid=uid123&E=1703155339&A=1&K=4&P=1&S=608baf47ce4d76be52eb6488bce29f9ea4cfc2ed
   * @param urlInM3u8: url in m3u8 file: 144p_index.m3u8 || https://cdnvt.net/hls-stream/test/144p_segment13102.ts
   * @param expiration: time to live : 1703155339L
   * @param uid: user id : uid123
   * @param keyNumber: key number : 4
   * @param macAlgorithm: algorithm : HmacSHA1
   * <br>
   * */
  private String generateUrl(String originUrl, String urlInM3u8, long expiration, String uid,
      int keyNumber, MacAlgorithm macAlgorithm, boolean isMediaPlaylist){

    StringBuilder sb = new StringBuilder(urlInM3u8);
    StringBuilder originUrlSb = new StringBuilder(originUrl);
    boolean isHaveHttpSchema = isHaveHttpSchema(urlInM3u8);
    int sizeOfUrlPrefix = 0; //for remove after generate token
    //check if have schema http/https
    if (isHaveHttpSchema) {
      //remove schema
      sb.delete(0, sb.indexOf(":") + 3);
    } else {
      String urlPrefix = getUrlPrefixHaveSchemaForUrlSigPlugin(originUrl);
      //remove master.m3u8

      originUrlSb.delete(originUrlSb.lastIndexOf("/") , originUrlSb.length()+1);

//      if(isMediaPlaylist){
//        //remove segment
//        originUrlSb.delete(originUrlSb.lastIndexOf("/") , originUrlSb.length()+1);
//      }
      //add urlprefix
      if (urlPrefix != null) {
        sb.insert(0,'/');
        sb.insert(0, urlPrefix);
        sizeOfUrlPrefix = urlPrefix.length()+1;
      } else {
        //TODO: get prefix from origin url (verify info)

      }
    }
//      long timestamp = System.currentTimeMillis() + expiration;
    sb.append('?');
//        .append("uid=").append(uid)
    if(!isMediaPlaylist){
      sb.append("timestamp=").append(expiration).append("&");
    }

    //hash
    String infoUrlSignPlugin = generateInfoForUrlSignPlugin(expiration, macAlgorithm, keyNumber);
    sb.append(infoUrlSignPlugin);
    sb.append("S=");

    String data = originUrlSb.toString() + "/" + sb.toString();
    sb.append(generateToken(data, keyNumber, macAlgorithm));
    //TODO: get prefix from origin url (verify info) -> remove concat
    if(isHaveHttpSchema){
      return concatHttpsSchema(sb.toString());
    } else {
      return sb.substring(sizeOfUrlPrefix);
    }
  }

  private String generateToken(String data, int keyNumber, MacAlgorithm algorithm) {
    log.info("generateToken: " + data + " keyNumber: " + keyNumber + " algorithm: " + algorithm);
    //get key
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
