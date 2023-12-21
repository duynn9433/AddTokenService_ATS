package com.viettel.vtnet.addtokenservice.service;

import com.viettel.vtnet.addtokenservice.common.HMACUtil;
import com.viettel.vtnet.addtokenservice.common.MacAlgorithm;
import io.lindstrom.m3u8.model.MasterPlaylist;
import io.lindstrom.m3u8.model.MediaPlaylist;
import io.lindstrom.m3u8.model.MediaSegment;
import io.lindstrom.m3u8.model.Variant;
import io.lindstrom.m3u8.parser.MasterPlaylistParser;
import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class RewriteManifestService {

  public static void main(String[] args)
      throws IOException, NoSuchAlgorithmException, InvalidKeyException {
    String masterUrl = "https://cdnvt.net/hls-stream/test/master.m3u8";
    String mediaUrl = "https://cdnvt.net/hls-stream/test/144p_index.m3u8";
    MasterPlaylist masterPlaylist = new RewriteManifestService().rewriteMasterPlaylist(
        new GetDataFromOriginService()
            .getMasterPlaylistFromOrigin(masterUrl),"uid",30,1,MacAlgorithm.HmacSHA1);
    System.out.println(new MasterPlaylistParser().writePlaylistAsString(masterPlaylist));
    MediaPlaylist mediaPlaylist = new RewriteManifestService().rewriteMediaPlaylist("urlPrefix",
        new GetDataFromOriginService()
            .getMediaPlaylistFromOrigin(mediaUrl),"uid",30,1,MacAlgorithm.HmacSHA1);
    System.out.println(new MasterPlaylistParser().writePlaylistAsString(masterPlaylist));
  }
  public MasterPlaylist rewriteMasterPlaylist(
      MasterPlaylist originMasterPlaylist,
      String uid,
      long expiration,
      int keyNumber,
      MacAlgorithm macAlgorithm)
      throws NoSuchAlgorithmException, InvalidKeyException {
    List<Variant> variants = originMasterPlaylist.variants();
    List<Variant> updatedVariants = new ArrayList<>();
    for (int i = 0; i < variants.size(); i++) {
      Variant v = variants.get(i);
      StringBuilder sb = new StringBuilder(v.uri());
//      long timestamp = System.currentTimeMillis() + expiration;
      sb.append('?')
          .append("timestamp=").append(expiration)
          .append('&').append("uid=").append(uid);
      //hash
      String infoUrlSignPlugin = generateInfoForUrlSignPlugin(expiration, macAlgorithm, keyNumber);
      sb.append(infoUrlSignPlugin);
      sb.append("&S=");
      String data = sb.toString();
      sb.append(generateToken(data, keyNumber, macAlgorithm));
      //end-hash
      Variant updatedVariant = Variant.builder().from(v).uri(sb.toString()).build();
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
      MacAlgorithm macAlgorithm) throws NoSuchAlgorithmException, InvalidKeyException {
    List<MediaSegment> segments = originMediaPlaylist.mediaSegments();
    List<MediaSegment> updatedSegments = new ArrayList<>();
    for (int i = 0; i < segments.size(); i++) {
      MediaSegment segment = segments.get(i);
      StringBuilder sb = new StringBuilder(segment.uri());
//      long timestamp = System.currentTimeMillis() + expiration;
      sb.append('?')
          .append("timestamp=").append(expiration)
          .append('&').append("uid=").append(uid);
      //hash
      String infoUrlSignPlugin = generateInfoForUrlSignPlugin(expiration, macAlgorithm, keyNumber);
      sb.append(infoUrlSignPlugin);
      sb.append("&S=");
      //check if have schema http/https
      //if have -> remove

      //if no -> add urlprefix
      String data = sb.toString();
      sb.append(generateToken(data, keyNumber, macAlgorithm));
      //end-hash
      MediaSegment updatedMediaSegment = MediaSegment.builder().from(segment).uri(sb.toString()).build();
      updatedSegments.add(updatedMediaSegment);
    }
    return MediaPlaylist.builder().from(originMediaPlaylist).mediaSegments(updatedSegments).build();
  }

  private String generateInfoForUrlSignPlugin(long expiration, MacAlgorithm macAlgorithm, int keyNumber) {
    return "&E=" + expiration
        + "&A=" + macAlgorithm.algorithmNumber
        + "&K=" + keyNumber
        + "&P=1";
  }

  private String generateToken(String data, int keyNumber, MacAlgorithm algorithm)
      throws NoSuchAlgorithmException, InvalidKeyException {
    //get key
    String key = "key123";
    //generate token
    return HMACUtil.hmacWithJava(algorithm, data,key);

  }
}
