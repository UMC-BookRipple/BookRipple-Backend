package com.bookripple.api.domain.question.service;

import com.bookripple.api.common.code.AnswerErrorCode;
import com.bookripple.api.common.code.QuestionErrorCode;
import com.bookripple.api.common.error.ApiException;
import com.bookripple.api.domain.member.entity.Member;
import com.bookripple.api.domain.member.repository.MemberRepository;
import com.bookripple.api.domain.notification.enums.NotificationType;
import com.bookripple.api.domain.notification.service.NotificationService;
import com.bookripple.api.domain.question.converter.AnswerConverter;
import com.bookripple.api.domain.question.dto.AnswerResDto.Ans;
import com.bookripple.api.domain.question.dto.AnswerResDto.AnswerList;
import com.bookripple.api.domain.question.dto.AnswerResDto.MyAnswer;
import com.bookripple.api.domain.question.dto.AnswerResDto.MyAnswerList;
import com.bookripple.api.domain.question.entity.Answer;
import com.bookripple.api.domain.question.entity.Question;
import com.bookripple.api.domain.question.repository.AnswerRepository;
import com.bookripple.api.domain.question.repository.QuestionRepository;
import com.bookripple.api.global.converter.GlobalConverter;
import com.bookripple.api.global.dto.GlobalDto.ContentReq;
import com.bookripple.api.global.dto.GlobalDto.IdRes;
import java.util.List;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@AllArgsConstructor
public class AnswerServiceImpl implements AnswerService {

  private static final String QUESTION_ANSWERED_CONTENT = "내 질문에 답변이 달렸습니다.";

  private final QuestionRepository questionRepository;
  private final MemberRepository memberRepository;
  private final AnswerRepository answerRepository;
  private final NotificationService notificationService;

  @Override
  @Transactional
  public IdRes createAnswer(Long questionId, Long memberId, ContentReq request) {
    Question question = questionRepository.findById(questionId)
        .orElseThrow(() -> new ApiException(QuestionErrorCode.QUESTION_NOT_FOUND));

    Member member = memberRepository.getReferenceById(memberId);

    Answer answer = AnswerConverter.toAnswer(question, member, request.content());

    answerRepository.save(answer);

    // 본인 질문 글에 답을 달지 않은 경우 알림 발생
    if (!question.getMember().getId().equals(memberId)) {
      notificationService.create(
          question.getMember(),
          NotificationType.QUESTION_ANSWERED,
          QUESTION_ANSWERED_CONTENT,
          toQuestionUrl(questionId)
      );
    }


    return GlobalConverter.toIdRes(answer.getId());
  }

  @Override
  @Transactional
  public IdRes updateAnswer(Long answerId, Long memberId, ContentReq request) {
    Answer answer = answerRepository.findById(answerId)
        .orElseThrow(() -> new ApiException(AnswerErrorCode.NO_ANSWER));

    if (!answer.getMember().getId().equals(memberId)) {
      throw new ApiException(AnswerErrorCode.FORBIDDEN);
    }

    answer.update(request.content());

    return GlobalConverter.toIdRes(answerId);
  }

  @Override
  public IdRes deleteAnswer(Long answerId, Long memberId) {
    Answer answer = answerRepository.findById(answerId)
        .orElseThrow(() -> new ApiException(AnswerErrorCode.NO_ANSWER));

    if (!answer.getMember().getId().equals(memberId)) {
      throw new ApiException(AnswerErrorCode.FORBIDDEN);
    }

    answerRepository.delete(answer);

    return GlobalConverter.toIdRes(answerId);
  }

  @Override
  public AnswerList getAnswers(Long questionId, Long memberId) {

    if (!questionRepository.existsById(questionId)) {
      throw new ApiException(QuestionErrorCode.QUESTION_NOT_FOUND);
    }

    List<Answer> answerList = answerRepository.findAllByQuestionId(questionId);

    List<Ans> ansList = answerList.stream()
        .map(answer -> {
          boolean isMine = answer.getMember().getId().equals(memberId);
          return AnswerConverter.toAns(answer, isMine);
        })
        .toList();

    return AnswerConverter.toAnswerList(ansList);
  }

  @Override
  public MyAnswerList getMyAnswers(Long memberId, Long lastAnswerId, int size) {

    Pageable pageable = PageRequest.of(0, size);

    Long cursor = lastAnswerId == null ? Long.MAX_VALUE : lastAnswerId;

    Slice<Answer> answerSlice = answerRepository.findMyAnswerByCursor(memberId, cursor, pageable);

    List<MyAnswer> myAnswerList = answerSlice.stream()
        .map(AnswerConverter::toMyAnswer)
        .toList();

    Long nextId = null;
    if (!myAnswerList.isEmpty()) {
      nextId = myAnswerList.get(myAnswerList.size() - 1).answerId();
    }

    return AnswerConverter.toMyAnswerList(myAnswerList, answerSlice.hasNext(), nextId);
  }

  private String toQuestionUrl(Long questionId) {
    return "/questions/" + questionId;
  }
}
