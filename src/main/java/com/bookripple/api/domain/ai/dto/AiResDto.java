package com.bookripple.api.domain.ai.dto;

import java.util.List;

public class AiResDto {

  public record GeminiRes(List<Candidate> candidates) {

    public record Candidate(Content content) {

    }

    public record Content(List<Part> parts) {

    }

    public record Part(String text) {

    }
  }

  public record AiQuestion(
      List<String> questions
  ) {

  }
}
