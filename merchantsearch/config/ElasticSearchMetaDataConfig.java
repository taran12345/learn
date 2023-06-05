// All Rights Reserved, Copyright © Paysafe Holdings UK Limited 2017. For more information see LICENSE

package com.paysafe.mastermerchant.merchantsearch.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.Map;

import lombok.Data;

@Data
@Component
@ConfigurationProperties("elastic-search-config")
public class ElasticSearchMetaDataConfig {
  private Map<String, ConfigAttributes> searchMapping;
  private Map<String, ConfigAttributes> rangeMapping;
  private Map<String, SortAttributes> sortMapping;
  
  private Map<String, DownloadAttributes> downloadMapping;
  
  @Data
  public static class ConfigAttributes {
    private String name;
    private Boolean isNested;
    private String nestedPath;
    private String searchPath;
  }
  
  @Data
  public static class DownloadAttributes {
    private String sourcePath;
    private String jsonPath;
    private String header;
  }
  
  @Data
  public static class SortAttributes {
    private String name;
    private Boolean isNested;
    private String nestedPath;
    private String searchPath;
    private Boolean isSearchAfterSupported;
  }
}
