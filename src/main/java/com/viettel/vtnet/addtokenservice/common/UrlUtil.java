package com.viettel.vtnet.addtokenservice.common;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class UrlUtil {

  public static void main(String[] args) {
    String url = "http://192.168.122.32/foo/asdfasdf/adsf.m3u8?timestamp=213&uid=uid123&E=1703155339&A=1&K=4&P=1&S=608baf47ce4d76be52eb6488bce29f9ea4cfc2ed"; // Thay thế URL này với URL thực tế của bạn

    // Biểu thức chính quy để trích xuất phần mong muốn từ URL
    Pattern pattern = Pattern.compile("https?://(.*?)/[^/]*$");
    Matcher matcher = pattern.matcher(url);

    if (matcher.find()) {
      String extractedPart = matcher.group(1);
      System.out.println("Extracted Part: " + extractedPart);
    } else {
      System.out.println("No match found");
    }
    //TODO: check co http hay k, k co -> them prefix, co -> cat schema
  }

  /**
   * Get URL prefix have schema for Url_sig Plugin ATS <br> Example: <br>
   * http://192.168.122.32/foo/asdfasdf/adsf.m3u8?timestamp=213&uid=uid123&E=1703155339&A=1&K=4&P=1&S=608baf47ce4d76be52eb6488bce29f9ea4cfc2ed
   * <br> -> 192.168.122.32/foo/asdfasdf
   */
  public static String getUrlPrefixHaveSchemaForUrlSigPlugin(String fullUrl) {
    Pattern pattern = Pattern.compile("https?://(.*?)/[^/]*$");
    Matcher matcher = pattern.matcher(fullUrl);

    if (matcher.find()) {
      return matcher.group(1);
    } else {
      return null;
    }
  }

  public static boolean isHaveHttpSchema(String url) {
    if(url.contains(":")){
      String schema = url.substring(0, url.indexOf(":"));
      return schema.equals("http") || schema.equals("https");
    }
    return false;
  }

  public static String getSchema(String url) {
    return url.substring(0, url.indexOf(":"));
  }

  public static String concatHttpsSchema(String url) {
    return "https://" + url;
  }

  public static String generateInfoForUrlSignPlugin(long expiration, MacAlgorithm macAlgorithm, int keyNumber) {
    return "E=" + expiration
        + "&A=" + macAlgorithm.algorithmNumber
        + "&K=" + keyNumber
        + "&P=0011&";
  }


}
