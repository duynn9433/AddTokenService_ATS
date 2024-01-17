package com.viettel.vtnet.addtokenservice.common;

import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import org.apache.commons.codec.binary.Hex;

public class HMACUtil {
  //TODO: cache SecretKeySpec
  public static void main(String[] args) throws NoSuchAlgorithmException, InvalidKeyException {
    System.out.println(hmacWithJava(MacAlgorithm.HmacMD5, "data", "key"));
  }
  public static String hmacWithJava(MacAlgorithm algorithm, String data, String key)
      throws NoSuchAlgorithmException, InvalidKeyException {
    SecretKeySpec secretKeySpec = new SecretKeySpec(key.getBytes(), algorithm.toString());
    Mac mac = Mac.getInstance(algorithm.toString());
    mac.init(secretKeySpec);
//    return bytesToHex(mac.doFinal(data.getBytes()));
    return Hex.encodeHexString(mac.doFinal(data.getBytes()));
  }

  public static String bytesToHex(byte[] hash) {
    StringBuilder hexString = new StringBuilder(2 * hash.length);
    for (byte h : hash) {
      String hex = Integer.toHexString(0xff & h);
      if (hex.length() == 1)
        hexString.append('0');
      hexString.append(hex);
    }
    return hexString.toString();
  }
}
