package com.viettel.vtnet.addtokenservice.config;

import com.viettel.vtnet.addtokenservice.repository.UrlSigConfigFileRepo;
import java.util.HashMap;
import java.util.Map;
import org.springframework.stereotype.Component;

@Component
public class UrlSigConfigPool {
  private UrlSigConfigFileRepo urlSigConfigFileRepo;
  private Map<String, UrlSigConfig> urlSigConfigMap;
  private Map<String, String> pathOriginMap;
  private Map<String, String> pathConfigMap;
  private String tokenName;

  public UrlSigConfigPool(UrlSigConfigFileRepo urlSigConfigFileRepo) {
    this.urlSigConfigFileRepo = urlSigConfigFileRepo;
    urlSigConfigMap = new HashMap<>();
    pathOriginMap = new HashMap<>();
    pathConfigMap = new HashMap<>();
    //TODO: read from config file
    pathOriginMap.put("bpk-tv", "http://117.1.157.113:9091");
    pathConfigMap.put("bpk-tv", "url_sig.conf");
    this.tokenName = "token";
  }

  public String getTokenName() {
    return tokenName;
  }

  public UrlSigConfig getUrlSigConfig(String name) {
    if (urlSigConfigMap.containsKey(name)) {
      return urlSigConfigMap.get(name);
    } else {
      String configName = getConfigName(name);
      if(configName == null) {
        throw new RuntimeException("Config name not found");
      } else {
        UrlSigConfig urlSigConfig = urlSigConfigFileRepo.getUrlSigConfig(configName);
        urlSigConfigMap.put(name, urlSigConfig);
        return urlSigConfig;
      }
    }
  }

  public String getOrigin(String name) {
    return pathOriginMap.get(name);
  }
  public String getConfigName(String name) {
    return pathConfigMap.get(name);
  }

}
