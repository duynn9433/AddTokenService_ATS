package com.viettel.vtnet.addtokenservice.repository;


import com.viettel.vtnet.addtokenservice.common.MacAlgorithm;
import com.viettel.vtnet.addtokenservice.config.UrlSigConfig;
import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.util.List;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

@Component
public class UrlSigConfigFileRepo {
  private Environment env;
  private String rootConfigPath;

  public UrlSigConfigFileRepo(Environment env) {
    this.env = env;
    this.rootConfigPath = env.getProperty("netCDN.config-path");
  }

  public UrlSigConfig getUrlSigConfig(String name) {
    String filePath = rootConfigPath + name;
    UrlSigConfig urlSigConfig = new UrlSigConfig();

    try (RandomAccessFile file = new RandomAccessFile(filePath, "r");
        FileChannel fileChannel = file.getChannel()) {
      long fileSize = fileChannel.size();
      MappedByteBuffer buffer = fileChannel.map(FileChannel.MapMode.READ_ONLY, 0, fileSize);
      StringBuilder lineBuffer = new StringBuilder();
      for (int i = 0; i < fileSize; i++) {
        char c = (char) buffer.get();
        if (c == '\n') {
          processLine(urlSigConfig, lineBuffer.toString());
          lineBuffer.setLength(0); // Clear the line buffer
        } else {
          lineBuffer.append(c);
        }
      }
      // Process the last line if it doesn't end with a newline character
      if (!lineBuffer.isEmpty()) {
        processLine(urlSigConfig, lineBuffer.toString());
      }
    } catch (IOException e) {
      e.printStackTrace();
    }

    return urlSigConfig;
  }

  private void processLine(UrlSigConfig urlSigConfig, String line) {
    // Process line here
    if(line.contains("knumber")){
      String[] arr = line.split(" ");
      urlSigConfig.setKeyNumber(Integer.parseInt(arr[2]));
    } else if(line.contains("key")){
      String[] arr = line.split(" ");
      urlSigConfig.getKeyMap().put(arr[0], arr[2]);
    } else if(line.contains("algorithm")) {
      String[] arr = line.split(" ");
      urlSigConfig.setAlgorithm(MacAlgorithm.getByAlgorithmNumber(Integer.parseInt(arr[2])));
    } else if(line.contains("use_parts")) {
      String[] arr = line.split(" ");
      urlSigConfig.setUseParts(arr[2].chars()
          .mapToObj(c -> c == '1')
          .toArray(Boolean[]::new));
    } else if(line.contains("hash_query_param")) {
      String[] arr = line.split(" ");
      String[] hashQueryParams = arr[2].split(",");
      urlSigConfig.setHashQueryParams(List.of(hashQueryParams));
    }
  }
}
