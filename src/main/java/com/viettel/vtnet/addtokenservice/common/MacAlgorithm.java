package com.viettel.vtnet.addtokenservice.common;

import java.security.Provider;
import java.security.Security;

public class HmacAlgorithm {
  public static void main(String[] args) {
    for (Provider provider : Security.getProviders()) {
      System.out.println(provider.getName());
      for (Provider.Service service : provider.getServices()) {
        if (service.getType().equals("Mac")) {
          System.out.println("  " + service.getAlgorithm());
        }
      }
    }
  }

}
