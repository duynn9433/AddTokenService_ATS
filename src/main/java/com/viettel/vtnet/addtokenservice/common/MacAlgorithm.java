package com.viettel.vtnet.addtokenservice.common;

import java.util.HashMap;
import java.util.Map;

/**
 * enum for javax.crypto.Mac;
 * algorithmNumber for url_sig ATS plugin
 * */
public enum MacAlgorithm {
  HmacSHA1(1),
  HmacMD5(2),
  HmacSHA256(3),
  HmacSHA384(4),
  HmacSHA512(5);

  public final int algorithmNumber;
  private static final Map<Integer, MacAlgorithm> BY_ALGORITHM_NUMBER = new HashMap<>();
  static {
    for (MacAlgorithm algorithm : values()) {
      BY_ALGORITHM_NUMBER.put(algorithm.algorithmNumber, algorithm);
    }
  }
  public static MacAlgorithm getByAlgorithmNumber(int algorithmNumber) {
    return BY_ALGORITHM_NUMBER.getOrDefault(algorithmNumber, null);
  }

  MacAlgorithm(int algorithmNumber) {
    this.algorithmNumber = algorithmNumber;
  }
}
