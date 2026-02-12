package com.bookripple.api.global.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import lombok.extern.slf4j.Slf4j;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.ses.SesClient;

@Slf4j
@Configuration
@ConditionalOnProperty(prefix = "aws.ses", name = "enabled", havingValue = "true")
public class AwsConfig {

  @Value("${aws.ses.region:ap-northeast-2}")
  private String region;

  @Bean
  public SesClient sesClient() {
    log.info("AWS SES 클라이언트 초기화 - 리전: {}", region);
    try {
      return SesClient.builder()
          .region(Region.of(region))
          .build();
    } catch (Exception e) {
      log.error("AWS SES 클라이언트 생성 실패. EC2 IAM Role 및 IMDS 접근 상태 확인 필요", e);
      throw e;
    }
  }
}
