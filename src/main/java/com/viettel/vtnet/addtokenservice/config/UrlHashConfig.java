package com.viettel.vtnet.addtokenservice.config;

import com.viettel.vtnet.addtokenservice.common.MacAlgorithm;
import java.util.List;
import java.util.stream.Stream;
import lombok.Getter;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

@Component
@Getter
public class UrlHashConfig {
  private Environment env;
  private MacAlgorithm algorithm;
  private int keyNumber;
  private Boolean[] useParts;
  private List<String> hashQueryParams;
  private String tokenName;

  public UrlHashConfig(Environment env) {
    this.env = env;
    String algorithmStr = env.getRequiredProperty("netCDN.algorithm");
    this.algorithm = MacAlgorithm.getByAlgorithmNumber(Integer.parseInt(algorithmStr));
    this.keyNumber = Integer.parseInt(env.getRequiredProperty("netCDN.key-number"));
    String usePartsStr = env.getRequiredProperty("netCDN.use-parts");
    this.useParts = usePartsStr.chars()
        .mapToObj(c -> c == '1')
        .toArray(Boolean[]::new);
    String hashQueryParamsStr = env.getRequiredProperty("netCDN.hash-query-params");
    this.hashQueryParams = Stream.of(hashQueryParamsStr.split(","))
                                .map(String::trim).toList();
    this.tokenName = env.getRequiredProperty("netCDN.token-name");
  }
}
