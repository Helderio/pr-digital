package com.ponteshop.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Getter
@Setter
@ConfigurationProperties(prefix = "firecrawl.api")
public class FirecrawlApiProperties {
    private String key = "";
    private String url = "https://api.firecrawl.dev/v2";
    private int timeoutSeconds = 15;
}
