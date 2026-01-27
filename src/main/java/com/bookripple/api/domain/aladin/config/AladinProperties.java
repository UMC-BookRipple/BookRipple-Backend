package com.bookripple.api.domain.aladin.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Getter @Setter
@ConfigurationProperties(prefix = "aladin")
public class AladinProperties {
    private String baseUrl;
    private String ttbKey;
    private String version;
    private String output;
}
