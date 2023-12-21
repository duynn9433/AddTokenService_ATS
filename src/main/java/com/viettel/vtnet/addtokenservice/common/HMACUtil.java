package com.viettel.vtnet.addtokenservice.common;

<<<<<<< HEAD
<<<<<<< HEAD
=======
>>>>>>> 6764d70 (save)
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

public class HMACUtil {

  public static void main(String[] args) throws NoSuchAlgorithmException, InvalidKeyException {
    System.out.println(hmacWithJava(MacAlgorithm.HmacMD5, "data", "key"));
  }
  public static String hmacWithJava(MacAlgorithm algorithm, String data, String key)
      throws NoSuchAlgorithmException, InvalidKeyException {
    SecretKeySpec secretKeySpec = new SecretKeySpec(key.getBytes(), algorithm.toString());
    Mac mac = Mac.getInstance(algorithm.toString());
    mac.init(secretKeySpec);
    return bytesToHex(mac.doFinal(data.getBytes()));
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
<<<<<<< HEAD
=======
public class HMACUtil {

>>>>>>> a7d5f61 (Initial commit)
=======
>>>>>>> 6764d70 (save)
}
