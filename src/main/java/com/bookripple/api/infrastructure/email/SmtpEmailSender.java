package com.bookripple.api.infrastructure.email;

import com.bookripple.api.domain.auth.util.EmailSender;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
@ConditionalOnProperty(prefix = "spring.mail", name = "host")
public class SmtpEmailSender implements EmailSender {

  private final JavaMailSender mailSender;

  @Override
  public void send(String to, String subject, String content) {
    try {
      MimeMessage message = mailSender.createMimeMessage();
      MimeMessageHelper helper = new MimeMessageHelper(message, false, "UTF-8");

      helper.setTo(to);
      helper.setSubject(subject);
      helper.setText(content, false); // HTML 여부: false = 텍스트

      mailSender.send(message);

      log.info("이메일 전송 성공: {}", to);
    } catch (MessagingException e) {
      log.error("이메일 전송 실패: {}", to, e);
      throw new RuntimeException("이메일 전송에 실패했습니다.");
    }
  }
}
