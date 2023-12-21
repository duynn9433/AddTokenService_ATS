package com.viettel.vtnet.addtokenservice.common;

import java.security.Provider;
import java.security.Security;
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

  MacAlgorithm(int algorithmNumber) {
    this.algorithmNumber = algorithmNumber;
  }
}
