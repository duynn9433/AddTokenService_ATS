package com.viettel.vtnet.addtokenservice.controller;

import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

@RestController
public class FakeManifestController {
  @GetMapping(value = "/fake-manifest", produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
  public byte[] getManifest() throws IOException {
    // Đọc nội dung của file manifest từ tài nguyên trong classpath
    Resource resource = (Resource) new ClassPathResource("playlist.m3u8");
    Path manifestPath = resource.getFile().toPath();
    return Files.readAllBytes(manifestPath);
  }
}
