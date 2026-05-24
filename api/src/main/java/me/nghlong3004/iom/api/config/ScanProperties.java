package me.nghlong3004.iom.api.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @author nghlong3004 (Nguyen Hoang Long)
 * @since 5/24/2026
 */
@ConfigurationProperties(prefix = "iom.scan")
public record ScanProperties(boolean enabled, String cronExpression) {

  public ScanProperties {
    if (cronExpression == null) {
      cronExpression = "0 0 * * * *";
    }
  }
}
