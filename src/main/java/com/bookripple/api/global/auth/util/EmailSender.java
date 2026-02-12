package com.bookripple.api.global.auth.util;

public interface EmailSender {

  void send(String to, String subject, String body);
}
