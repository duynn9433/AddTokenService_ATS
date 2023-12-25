package com.viettel.vtnet.addtokenservice.service;

import static com.viettel.vtnet.addtokenservice.common.UrlUtil.concatHttpsSchema;
import static com.viettel.vtnet.addtokenservice.common.UrlUtil.generateInfoForUrlSignPlugin;
import static com.viettel.vtnet.addtokenservice.common.UrlUtil.getUrlPrefixHaveSchemaForUrlSigPlugin;
import static com.viettel.vtnet.addtokenservice.common.UrlUtil.isHaveHttpSchema;

import com.viettel.vtnet.addtokenservice.common.HMACUtil;
import com.viettel.vtnet.addtokenservice.common.MacAlgorithm;
import io.lindstrom.m3u8.model.MasterPlaylist;
import io.lindstrom.m3u8.model.MediaPlaylist;
import io.lindstrom.m3u8.model.MediaSegment;
import io.lindstrom.m3u8.model.Variant;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

@Service
public class RewriteManifestService {

  private Environment env;

  public RewriteManifestService(Environment env) {
    this.env = env;
  }

  public MasterPlaylist rewriteMasterPlaylist(
      MasterPlaylist originMasterPlaylist,
      String uid,
      Long expiration,
      int keyNumber,
      MacAlgorithm macAlgorithm) {
    List<Variant> variants = originMasterPlaylist.variants();
    List<Variant> updatedVariants = new ArrayList<>();
    for (int i = 0; i < variants.size(); i++) {
      Variant v = variants.get(i);
      //hash
      String urlWithToken = generateUrl("originUrl", v.uri(), expiration, uid, keyNumber, macAlgorithm);
      //end-hash
      Variant updatedVariant = Variant.builder().from(v).uri(urlWithToken).build();
      updatedVariants.add(updatedVariant);
    }
    return MasterPlaylist.builder().from(originMasterPlaylist).variants(updatedVariants).build();
  }

  public MediaPlaylist rewriteMediaPlaylist(
      String urlPrefix,
      MediaPlaylist originMediaPlaylist,
      String uid,
      long expiration,
      int keyNumber,
      MacAlgorithm macAlgorithm){
    List<MediaSegment> segments = originMediaPlaylist.mediaSegments();
    List<MediaSegment> updatedSegments = new ArrayList<>();
    for (int i = 0; i < segments.size(); i++) {
      MediaSegment segment = segments.get(i);
      //hash
      String urlWithToken = generateUrl("originUrl", segment.uri(), expiration, uid, keyNumber, macAlgorithm);
      //end-hash
      MediaSegment updatedMediaSegment = MediaSegment.builder().from(segment).uri(urlWithToken)
          .build();
      updatedSegments.add(updatedMediaSegment);
    }
    return MediaPlaylist.builder().from(originMediaPlaylist).mediaSegments(updatedSegments).build();
  }

  private String generateUrl(String originUrl, String urlInM3u8, long expiration, String uid,
      int keyNumber, MacAlgorithm macAlgorithm){

    StringBuilder sb = new StringBuilder(urlInM3u8);
    boolean isHaveHttpSchema = isHaveHttpSchema(urlInM3u8);
    //check if have schema http/https
    if (isHaveHttpSchema) {
      //remove schema
      sb.delete(0, sb.indexOf(":") + 3);
    } else {
      String urlPrefix = getUrlPrefixHaveSchemaForUrlSigPlugin(urlInM3u8);
      //add urlprefix
      if (urlPrefix != null) {
        sb.insert(0, urlPrefix);
      } else {
        //TODO: get prefix from origin url (verify info)

      }
    }
//      long timestamp = System.currentTimeMillis() + expiration;
    sb.append('?')
//        .append("timestamp=").append(expiration).append('&')
        .append("uid=").append(uid);
    //hash
    String infoUrlSignPlugin = generateInfoForUrlSignPlugin(expiration, macAlgorithm, keyNumber);
    sb.append(infoUrlSignPlugin);
    sb.append("&S=");

    String data = sb.toString();
    sb.append(generateToken(data, keyNumber, macAlgorithm));
    //TODO: get prefix from origin url (verify info) -> remove concat
    if(isHaveHttpSchema){
      return concatHttpsSchema(sb.toString());
    } else {
      return sb.toString();
    }
  }

  private String generateToken(String data, int keyNumber, MacAlgorithm algorithm) {
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
