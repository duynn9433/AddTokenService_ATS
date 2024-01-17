package com.viettel.vtnet.addtokenservice.config;

import com.viettel.vtnet.addtokenservice.common.MacAlgorithm;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.Data;

@Data
public class UrlSigConfig {
  private Map<String, String> keyMap;
  private MacAlgorithm algorithm;
  private int keyNumber;
  private Boolean[] useParts;
  private List<String> hashQueryParams;

  public UrlSigConfig() {
    keyMap = new HashMap<>();
  }

  public String getKey(int keyNumber){
    String res =  keyMap.get("key" + keyNumber);
    if(res == null){
      throw new RuntimeException("Key not found");
    }
    return res;
  }

  public String getKey(){
    return getKey(keyNumber);
  }

}
