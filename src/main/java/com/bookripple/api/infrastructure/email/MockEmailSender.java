package com.bookripple.api.infrastructure.email;

import com.bookripple.api.global.auth.util.EmailSender;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@ConditionalOnProperty(prefix = "aws.ses", name = "enabled", havingValue = "false", matchIfMissing = true)
public class MockEmailSender implements EmailSender {

  @Override
  public void send(String to, String subject, String content) {
    log.info("[MOCK] 이메일 전송 (실제 발송 않음)");
    log.info("[MOCK] 받는 사람: {}", to);
    log.info("[MOCK] 제목: {}", subject);
    log.info("[MOCK] 내용:\n{}", content);
  }
}
