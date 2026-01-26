package com.bookripple.api.domain.auth.util;

public interface EmailSender {

  void send(String to, String subject, String body);
}
