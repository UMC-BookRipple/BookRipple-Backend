package com.bookripple.api.domain.ai.service;

import com.bookripple.api.common.error.ApiException;
import com.bookripple.api.domain.ai.code.AiErrorCode;
import com.bookripple.api.domain.ai.dto.AiReqDto.GeminiReq;
import com.bookripple.api.domain.ai.dto.AiResDto.AiQuestion;
import com.bookripple.api.domain.ai.dto.AiResDto.GeminiRes;
import com.bookripple.api.domain.ai.enums.AiQuestionType;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.List;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

@Service
public class AiService {

  private final RestClient restClient;
  private final ObjectMapper objectMapper;

  public AiService(RestClient.Builder builder, ObjectMapper objectMapper,
      @Value("${gemini.api.key}") String apiKey,
      @Value("${gemini.api.url}") String apiUrl
  ) {
    this.objectMapper = objectMapper;
    this.restClient = builder
        .baseUrl(apiUrl)
        .defaultHeader("x-goog-api-key", apiKey)
        .defaultHeader("Content-Type", "application/json")
        .build();
  }

  public AiQuestion generateQuestions(AiQuestionType type, String bookTitle) {
    String prompt = String.format(type.getPrompt(), bookTitle);

    GeminiReq req = createReq(prompt);

    GeminiRes res = restClient.post()
        .body(req)
        .retrieve()
        .body(GeminiRes.class);

    return parseRes(res);
  }

  private GeminiReq createReq(String prompt) {
    var part = new GeminiReq.Part(prompt);
    var content = new GeminiReq.Content(List.of(part));
    var config = new GeminiReq.GenerationConfig("application/json"); // JSON 모드 활성화
    return new GeminiReq(List.of(content), config);
  }

  private AiQuestion parseRes(GeminiRes response) {
    if (response == null || response.candidates() == null || response.candidates().isEmpty()) {
      throw new ApiException(AiErrorCode.AI_BAD_GATEWAY);
    }

    try {
      String jsonText = response.candidates().get(0).content().parts().get(0).text();

      int firstBrace = jsonText.indexOf("{");
      int lastBrace = jsonText.lastIndexOf("}");

      if (firstBrace == -1 && lastBrace == -1) {
        throw new ApiException(AiErrorCode.AI_UNAVAILABLE);
      }

      String cleanJson = jsonText.substring(firstBrace, lastBrace + 1);

      return objectMapper.readValue(cleanJson, AiQuestion.class);

    } catch (Exception e) {
      throw new ApiException(AiErrorCode.AI_UNAVAILABLE);
    }
  }
}
