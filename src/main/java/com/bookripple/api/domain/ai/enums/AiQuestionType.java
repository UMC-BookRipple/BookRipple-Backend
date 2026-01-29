package com.bookripple.api.domain.ai.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum AiQuestionType {

  DURING("""
      사용자가 현재 **'%s'** 라는 책을 읽고 있어.
      사용자가 '지금 책을 읽고 있는 중'일 때, 생각할 거리를 던져주는 질문 1가지를 생성해줘.
      
      [제약 조건]
      1. 줄거리, 결말 스포일러 금지.
      2. 사용자가 느낄 생각이나 감정에 집중할 것.
      3. 공백 포함 50자 이내, 한국어, 해요체 사용.
      4. 마크다운을 제외하고 json으로 보낼 것({ "questions": ["질문1"] }).
      """),

  AFTER("""
      사용자가 **'%s'** 라는 책을 모두 완독했어.
      이 책의 핵심 주제, 인물, 결말에 대해 깊이 생각해볼 수 있는 질문 3가지를 생성해줘.
      
      [제약 조건]
      1. 책의 구체적인 등장인물이나 사건을 언급해도 좋음.
      2. 독자의 삶과 연결 짓는 질문을 포함할 것.
      3. 공백 포함 50자 이내, 한국어, 해요체 사용.
      4. 마크다운을 제외하고 json으로 보낼 것({ "questions": ["질문1", "질문2", "질문3"] }).
      """);

  private final String prompt;
}
