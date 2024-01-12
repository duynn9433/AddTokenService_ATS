package com.viettel.vtnet.addtokenservice.common;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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

  public static void main(String[] args) {
    String url = "http://192.168.122.32/foo/asdfasdf/adsf.m3u8?timestamp=213&uid=uid123&E=1703155339&A=1&K=4&P=1&S=608baf47ce4d76be52eb6488bce29f9ea4cfc2ed"; // Thay thế URL này với URL thực tế của bạn
    System.out.println("path:" + getUseParts(url, new Boolean[]{false, false, true, true}));
    System.out.println("query:"+getMapQueryParam(url));
    System.out.println("hash query param:"+getHashQueryParamWithValue(url, List.of("E", "A", "K")));
    System.out.println("hash query param:"+getHashQueryParamWithValue(url, List.of("E", "A", "K"), List.of("E")));
//    // Biểu thức chính quy để trích xuất phần mong muốn từ URL
//    Pattern pattern = Pattern.compile("https?://(.*?)/[^/]*$");
//    Matcher matcher = pattern.matcher(url);
//
//    if (matcher.find()) {
//      String extractedPart = matcher.group(1);
//      System.out.println("Extracted Part: " + extractedPart);
//    } else {
//      System.out.println("No match found");
//    }
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

  /**
   * check schema http or https <br>
   * 0: not have schema <br>
   * 1: http <br>
   * 2: https <br>
   * */
  public static int isHaveHttpSchema(String url) {
    if(url.contains(":")){
      String schema = url.substring(0, url.indexOf(":"));
      switch (schema){
        case "http":
          return 1;
        case "https":
          return 2;
      }
    }
    return 0;
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
<<<<<<< HEAD
        + "&P=0011&";
=======
        + "&P=01&";
>>>>>>> cb3b992 (remove timstamp for media playlist)
  }
<<<<<<< HEAD
=======

  public static Map<String, String> getMapQueryParam(String queryParam) {
    if(queryParam == null || queryParam.isEmpty()){
      return null;
    }
    if(queryParam.charAt(0) == '?'){
      queryParam = queryParam.substring(1);
    }
    String[] queryParamArr = queryParam.split("&");
    Map<String, String> queryParamMap = new HashMap<>();
    for (String queryParamItem : queryParamArr) {
      String[] queryParamItemArr = queryParamItem.split("=");
      queryParamMap.put(queryParamItemArr[0], queryParamItemArr[1]);
    }
    return queryParamMap;
  }
  /**
   * get hash query param with value from base query param <br>
   * MUST BE ORDERED <br>
   * @param baseQueryParam: base query param <br>
   *       Example: timestamp=213&uid=uid123&E=1703155339&A=1&K=4&P=1&S=608baf47ce4d76be52eb6488bce29f9ea4cfc2ed
   * @param listHashQueryParam: list hash query param <br>
   *       Example: List.of("E", "A", "K")
   * @param ignoreHashQueryParam: list hash query param ignore <br>
   * */
  public static  String getHashQueryParamWithValue(String baseQueryParam,
      List<String> listHashQueryParam,
      List<String> ignoreHashQueryParam) {
    ArrayList<String> listHashQueryParamTemp = new ArrayList<>(listHashQueryParam);
    listHashQueryParamTemp.removeAll(ignoreHashQueryParam);
    return getHashQueryParamWithValue(baseQueryParam, listHashQueryParamTemp);
  }
  /**
   * get hash query param with value from base query param <br>
   * MUST BE ORDERED <br>
   * @param baseQueryParam: base query param <br>
   *       Example: timestamp=213&uid=uid123&E=1703155339&A=1&K=4&P=1&S=608baf47ce4d76be52eb6488bce29f9ea4cfc2ed
   * @param listHashQueryParam: list hash query param <br>
   *       Example: List.of("E", "A", "K")
   * */
  public static String getHashQueryParamWithValue(String baseQueryParam, List<String> listHashQueryParam) {
    if(listHashQueryParam == null || listHashQueryParam.isEmpty()){
      return "";
    }
    StringBuilder sb = new StringBuilder();
    int i = 0, nextIndex = 0;
    for (String hashQueryParam : listHashQueryParam) {
      //get index of hash query param
      i = baseQueryParam.indexOf(hashQueryParam);
      if(i == -1){
        throw new RuntimeException("hashQueryParam not found in baseQueryParam");
      }
      //add hash query param with value
      nextIndex = baseQueryParam.indexOf("&", i);
      //last hash query param
      if(nextIndex == -1){
        sb.append(baseQueryParam.substring(i)).append("&");
      } else {
        sb.append(baseQueryParam, i, nextIndex + 1);
      }
      i = nextIndex;
    }
    //remove last "&"
    if(!sb.isEmpty()){
      sb.deleteCharAt(sb.length()-1);
    }
    return sb.toString();
  }

  /**
   * get use path from base url without schema and query param <br>
   * @param path: base url without schema <br>
   *       Example: 117.1.157.113/a/b/c
   * @param useParts: array of use path: 0011 <br>
   * */
  public static String getUseParts(String path, Boolean[] useParts){
    if (isHaveHttpSchema(path) != 0) {
      //remove schema
      path = path.substring(path.indexOf(":") + 3);
    }
    //Special case: use all & use none
    if(useParts.length == 1 && useParts[0]){
      return path;
    } else if(useParts.length == 1 && !useParts[0]){
      return "";
    }
    //Normal case
    StringBuilder sb = new StringBuilder();
    String [] parts = path.split("/");
    if(parts.length < useParts.length){
      throw new RuntimeException("useParts too long");
    }
    int i=0;
    for(; i < useParts.length; i++){
      if(useParts[i]){
        sb.append(parts[i]).append("/");
      }
    }
    //Special case: use last all when useParts[last]=1
    if(useParts[useParts.length-1]){
      for(; i < parts.length; i++){
        sb.append(parts[i]).append("/");
      }
    }
    //remove last "/"
    if(!sb.isEmpty()){
      sb.deleteCharAt(sb.length()-1);
    }
    return sb.toString();
  }
>>>>>>> 0ddc022 ([CDN-98] feature: support useParts P and new token rule)
}
