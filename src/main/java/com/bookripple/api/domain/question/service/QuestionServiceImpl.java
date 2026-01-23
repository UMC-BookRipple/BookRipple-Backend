package com.bookripple.api.domain.question.service;

import com.bookripple.api.common.code.BookErrorCode;
import com.bookripple.api.common.code.QuestionErrorCode;
import com.bookripple.api.common.error.ApiException;
import com.bookripple.api.domain.book.entity.Book;
import com.bookripple.api.domain.book.repository.BookRepository;
import com.bookripple.api.domain.member.entity.Member;
import com.bookripple.api.domain.member.repository.MemberRepository;
import com.bookripple.api.domain.question.converter.QuestionConverter;
import com.bookripple.api.domain.question.dto.QuestionResDto.MyQ;
import com.bookripple.api.domain.question.dto.QuestionResDto.MyQuestionList;
import com.bookripple.api.domain.question.dto.QuestionResDto.Q;
import com.bookripple.api.domain.question.dto.QuestionResDto.QuestionList;
import com.bookripple.api.domain.question.entity.Question;
import com.bookripple.api.domain.question.repository.QuestionRepository;
import com.bookripple.api.global.converter.GlobalConverter;
import com.bookripple.api.global.dto.GlobalDto.ContentReq;
import com.bookripple.api.global.dto.GlobalDto.IdRes;
import jakarta.transaction.Transactional;
import java.util.List;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Service
@AllArgsConstructor
public class QuestionServiceImpl implements QuestionService {

  private final MemberRepository memberRepository;
  private final BookRepository bookRepository;
  private final QuestionRepository questionRepository;

  @Override
  @Transactional
  public IdRes createQuestion(Long memberId, Long bookId, ContentReq request) {

    Book book = bookRepository.findById(bookId)
        .orElseThrow(() -> new ApiException(BookErrorCode.NO_BOOK));

    Member member = memberRepository.getReferenceById(memberId);

    Question question = QuestionConverter.toQuestion(member, book, request.content());

    questionRepository.save(question);

    return GlobalConverter.toIdRes(question.getId());
  }

  @Override
  @Transactional
  public IdRes deleteQuestion(Long memberId, Long questionId) {
    Question question = questionRepository.findById(questionId)
        .orElseThrow(() -> new ApiException(QuestionErrorCode.QUESTION_NOT_FOUND));

    if (!question.getMember().getId().equals(memberId)) {
      throw new ApiException(QuestionErrorCode.QUESTION_FORBIDDEN);
    }

    questionRepository.delete(question);

    return GlobalConverter.toIdRes(questionId);
  }

  @Override
  public QuestionList getQuestion(Long memberId, Long bookId, String keyword, Boolean onlyMine,
      Long lastId, int size) {

    if (!bookRepository.existsById(bookId)) {
      throw new ApiException(BookErrorCode.NO_BOOK);
    }

    Pageable pageable = PageRequest.of(0, size);

    Slice<Question> questionSlice = questionRepository.findQuestionByCursor(bookId, lastId,
        memberId, keyword, onlyMine, pageable);

    List<Q> questionList = questionSlice.stream()
        .map(QuestionConverter::toQ)
        .toList();

    Long nextId = null;
    if (!questionList.isEmpty()) {
      nextId = questionList.get(questionList.size() - 1).id();
    }

    long totalCnt = 0;
    if (lastId == null && StringUtils.hasText(keyword)) {
      totalCnt = questionRepository.countQuestions(bookId, memberId, keyword, onlyMine);
    }

    return QuestionConverter.toQuestionList(questionList, nextId, questionSlice.hasNext(),
        totalCnt);

  }

  @Override
  public MyQuestionList getMyQuestion(Long memberId, String lastBookTitle, Long lastId, int size) {
    Pageable pageable = PageRequest.of(0, size);

    Slice<Question> questionSlice = questionRepository.findMyQuestionByCursor(memberId,
        lastBookTitle, lastId, pageable);

    List<MyQ> questionList = questionSlice.stream()
        .map(QuestionConverter::toMyQ)
        .toList();

    String nextTitle = null;
    Long nextId = null;
    if (!questionList.isEmpty()) {
      MyQ last = questionList.get(questionList.size() - 1);
      nextTitle = last.bookTitle();
      nextId = last.id();
    }

    return QuestionConverter.toMyQuestionList(questionList, nextTitle, nextId,
        questionSlice.hasNext());
  }
}
