package com.viettel.vtnet.addtokenservice.config;

import com.viettel.vtnet.addtokenservice.repository.UrlSigConfigFileRepo;
import java.util.HashMap;
import java.util.Map;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

@Component
public class UrlSigConfigPool {
  private Environment env;
  private UrlSigConfigFileRepo urlSigConfigFileRepo;
  private Map<String, UrlSigConfig> urlSigConfigMap;
  private Map<String, String> pathOriginMap;
  private Map<String, String> pathConfigMap;
  private String tokenName;

  public UrlSigConfigPool(UrlSigConfigFileRepo urlSigConfigFileRepo, Environment env) {
    this.env = env;
    this.urlSigConfigFileRepo = urlSigConfigFileRepo;
    urlSigConfigMap = new HashMap<>();
    pathOriginMap = new HashMap<>();
    pathConfigMap = new HashMap<>();
    //TODO: read from config file
//    pathOriginMap.put("bpk-tv", "http://117.1.157.115:9092");
    pathOriginMap.put("bpk-tv", env.getProperty("netCDN.bkp-tv-origin"));
    pathConfigMap.put("bpk-tv", "url_sig.config");
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
