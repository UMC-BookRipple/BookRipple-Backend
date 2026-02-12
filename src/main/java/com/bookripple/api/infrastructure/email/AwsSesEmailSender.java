package com.bookripple.api.infrastructure.email;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import com.bookripple.api.domain.auth.util.EmailSender;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import software.amazon.awssdk.services.ses.SesClient;
import software.amazon.awssdk.services.ses.model.Body;
import software.amazon.awssdk.services.ses.model.Content;
import software.amazon.awssdk.services.ses.model.Destination;
import software.amazon.awssdk.services.ses.model.Message;
import software.amazon.awssdk.services.ses.model.SendEmailRequest;
import software.amazon.awssdk.services.ses.model.SendEmailResponse;

@Slf4j
@Component
@ConditionalOnProperty(prefix = "aws.ses", name = "enabled", havingValue = "true")
@RequiredArgsConstructor
public class AwsSesEmailSender implements EmailSender {

  private final SesClient sesClient;

  @Value("${aws.ses.from-email:noreply@bookripple.site}")
  private String fromEmail;

  @Override
  public void send(String to, String subject, String content) {
    try {
      SendEmailRequest emailRequest =
          SendEmailRequest.builder()
              .source(fromEmail)
              .destination(
                  Destination.builder()
                      .toAddresses(to)
                      .build())
              .message(
                  Message.builder()
                      .subject(Content.builder()
                          .data(subject)
                          .charset("UTF-8")
                          .build())
                      .body(Body.builder()
                          .text(Content.builder()
                              .data(content)
                              .charset("UTF-8")
                              .build())
                          .build())
                      .build())
              .build();

      SendEmailResponse result = sesClient.sendEmail(emailRequest);

      log.info("이메일 전송 성공: {} (MessageId: {})", to, result.messageId());
    } catch (Exception e) {
      log.error("이메일 전송 실패: {}", to, e);
      throw new RuntimeException("이메일 전송에 실패했습니다.", e);
    }
  }
}
