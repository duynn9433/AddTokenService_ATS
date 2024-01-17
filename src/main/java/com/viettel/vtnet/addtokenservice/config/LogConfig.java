package com.viettel.vtnet.addtokenservice.config;

import lombok.Getter;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

@Component
@Getter
public class LogConfig {
  private Environment env;
  private boolean isLogDebug;

  public LogConfig(Environment env) {
    this.env = env;
    String logLevel = env.getProperty("logging.level.root");
    if(logLevel != null && (logLevel.equals("DEBUG") || logLevel.equals("TRACE"))) {
      this.isLogDebug = true;
    } else {
      this.isLogDebug = false;
    }
  }
}
