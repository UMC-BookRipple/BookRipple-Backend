package com.bookripple.api.domain.question.service;

import com.bookripple.api.common.code.BookErrorCode;
import com.bookripple.api.common.code.CommonErrorCode;
import com.bookripple.api.common.code.QuestionErrorCode;
import com.bookripple.api.common.code.ReadingErrorCode;
import com.bookripple.api.common.error.ApiException;
import com.bookripple.api.domain.ai.dto.AiResDto.AiQuestion;
import com.bookripple.api.domain.ai.enums.AiQuestionType;
import com.bookripple.api.domain.ai.service.AiService;
import com.bookripple.api.domain.book.entity.Book;
import com.bookripple.api.domain.book.repository.BookRepository;
import com.bookripple.api.domain.member.entity.Member;
import com.bookripple.api.domain.member.repository.MemberRepository;
import com.bookripple.api.domain.question.converter.QuestionConverter;
import com.bookripple.api.domain.question.dto.QuestionResDto.MyQ;
import com.bookripple.api.domain.question.dto.QuestionResDto.MyQuestionList;
import com.bookripple.api.domain.question.dto.QuestionResDto.Q;
import com.bookripple.api.domain.question.dto.QuestionResDto.QuestionList;
import com.bookripple.api.domain.question.dto.QuestionResDto.ReadingAiQnA;
import com.bookripple.api.domain.question.dto.QuestionResDto.ReadingAiQnAList;
import com.bookripple.api.domain.question.entity.Question;
import com.bookripple.api.domain.question.entity.ReadingQuestion;
import com.bookripple.api.domain.question.entity.SearchQuestionLog;
import com.bookripple.api.domain.question.enums.QuestionType;
import com.bookripple.api.domain.question.repository.QuestionRepository;
import com.bookripple.api.domain.question.repository.ReadingQuestionRepository;
import com.bookripple.api.domain.question.repository.SearchQuestionLogRepository;
import com.bookripple.api.domain.reading.entity.ReadingProgress;
import com.bookripple.api.domain.reading.repository.ReadingProgressRepository;
import com.bookripple.api.global.converter.GlobalConverter;
import com.bookripple.api.global.dto.GlobalDto.ContentReq;
import com.bookripple.api.global.dto.GlobalDto.IdList;
import com.bookripple.api.global.dto.GlobalDto.IdRes;
import jakarta.transaction.Transactional;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Service
@AllArgsConstructor
public class QuestionServiceImpl implements QuestionService {

  private final MemberRepository memberRepository;
  private final BookRepository bookRepository;
  private final QuestionRepository questionRepository;
  private final AiService aiService;
  private final ReadingQuestionRepository readingQuestionRepository;
  private final SearchQuestionLogRepository searchQuestionLogRepository;
  private final ReadingProgressRepository progressRepository;

  @Override
  @Transactional
  public IdRes createQuestion(Long memberId, Long bookId, ContentReq request) {

    Book book = bookRepository.findById(bookId)
        .orElseThrow(() -> new ApiException(BookErrorCode.NO_BOOK));

    Member member = memberRepository.getReferenceById(memberId);

    Question question = QuestionConverter.toQuestion(member, book, request.content(),
        QuestionType.USER);

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

    ReadingProgress readingProgress = Optional.ofNullable(
            progressRepository.findByMemberIdAndBookId(memberId, bookId))
        .orElseThrow(() -> new ApiException(ReadingErrorCode.NO_READING_SESSION));

    if (readingProgress.getProgress().compareTo(new BigDecimal("30")) < 0) {
      throw new ApiException(QuestionErrorCode.INSUFFICIENT_PROGRESS);
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

  @Override
  @Transactional
  public QuestionList createAfterReadingQuestion(Long memberId, Long bookId) {

    Member member = memberRepository.getReferenceById(memberId);

    Book book = bookRepository.findById(bookId)
        .orElseThrow(() -> new ApiException(BookErrorCode.NO_BOOK));

    ReadingProgress progress = Optional.ofNullable(
            progressRepository.findByMemberIdAndBookId(memberId, bookId))
        .orElseThrow(() -> new ApiException(ReadingErrorCode.NO_READING_SESSION));

    if (!progress.isCompleted()) {
      throw new ApiException(QuestionErrorCode.INSUFFICIENT_PROGRESS_2);
    }

    AiQuestion aiQuestion = aiService.generateQuestions(AiQuestionType.AFTER, book.getTitle(),
        BigDecimal.valueOf(100));

    List<Question> aiQuestions = aiQuestion.questions().stream()
        .map(content -> QuestionConverter.toQuestion(member, book, content,
            QuestionType.AI_AFTER_READING))
        .toList();

    questionRepository.saveAll(aiQuestions);

    List<Q> res = aiQuestions.stream()
        .map(QuestionConverter::toQ)
        .toList();

    return QuestionConverter.toQuestionList(res, null, false, 3);
  }

  @Override
  @Transactional
  public Q createDuringReadingQuestion(Long memberId, Long bookId) {

    Member member = memberRepository.getReferenceById(memberId);

    Book book = bookRepository.findById(bookId)
        .orElseThrow(() -> new ApiException(BookErrorCode.NO_BOOK));

    ReadingProgress progress = Optional.ofNullable(
            progressRepository.findByMemberIdAndBookId(memberId, bookId))
        .orElseThrow(() -> new ApiException(ReadingErrorCode.NO_READING_SESSION));

    AiQuestion aiQuestion = aiService.generateQuestions(AiQuestionType.DURING, book.getTitle(),
        progress.getProgress());

    ReadingQuestion question = QuestionConverter.toReadingQuestion(member, book,
        aiQuestion.questions().get(0));

    readingQuestionRepository.save(question);

    return QuestionConverter.toQ(question);
  }

  @Override
  @Transactional
  public IdRes updateReadingQuestion(Long memberId, Long readingQuestionId, ContentReq request) {

    ReadingQuestion readingQuestion = readingQuestionRepository.findById(readingQuestionId)
        .orElseThrow(() -> new ApiException(QuestionErrorCode.QUESTION_NOT_FOUND));

    if (!readingQuestion.getMember().getId().equals(memberId)) {
      throw new ApiException(QuestionErrorCode.QUESTION_FORBIDDEN);
    }

    readingQuestion.update(request.content());

    return GlobalConverter.toIdRes(readingQuestionId);
  }

  @Override
  public ReadingAiQnAList getReadingAiQnAList(Long memberId, Long bookId, Long lastId, int size) {

    Pageable pageable = PageRequest.of(0, size);

    if (lastId == null) {
      lastId = Long.MAX_VALUE;
    }
    if (!bookRepository.existsById(bookId)) {
      throw new ApiException(BookErrorCode.NO_BOOK);
    }

    Slice<ReadingQuestion> readingQuestionSlice = readingQuestionRepository.findByMemberIdAndBookIdAndIdLessThanOrderByIdDesc(
        memberId, bookId, lastId, pageable);

    List<ReadingAiQnA> readingAiQnAS = readingQuestionSlice.stream()
        .map(QuestionConverter::toReadingAiQnA)
        .toList();

    Long nextId = null;
    if (!readingAiQnAS.isEmpty() && readingQuestionSlice.hasNext()) {
      nextId = readingAiQnAS.get(readingAiQnAS.size() - 1).id();
    }
    return QuestionConverter.toReadingAiQnAList(readingAiQnAS, readingQuestionSlice.hasNext(),
        nextId);
  }

  @Override
  @Transactional
  public IdRes deleteReadingAiQnA(Long memberId, Long questionId) {
    ReadingQuestion question = readingQuestionRepository.findById(questionId)
        .orElseThrow(() -> new ApiException(QuestionErrorCode.QUESTION_NOT_FOUND));

    if (!question.getMember().getId().equals(memberId)) {
      throw new ApiException(QuestionErrorCode.QUESTION_FORBIDDEN);
    }

    readingQuestionRepository.delete(question);

    return GlobalConverter.toIdRes(questionId);
  }

  @Override
  @Transactional
  public QuestionList searchQuestion(Long memberId, Long bookId, String query, int page, int size) {
    if (!StringUtils.hasText(query)) {
      throw new ApiException(CommonErrorCode.BAD_REQUEST);
    }

    String keyword = query.trim();

    Book book = bookRepository.findById(bookId)
        .orElseThrow(() -> new ApiException(BookErrorCode.NO_BOOK));

    Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "id"));

    Page<Question> questionPage = questionRepository.findByBookIdAndContentContainingOrderByIdDesc(
        bookId, keyword, pageable);

    List<Q> questionList = questionPage.stream()
        .map(QuestionConverter::toQ)
        .toList();

    Long lastId = null;
    if (!questionList.isEmpty()) {
      lastId = questionList.get(questionList.size() - 1).id();
    }

    Member member = memberRepository.getReferenceById(memberId);
    SearchQuestionLog searchQuestionLog = SearchQuestionLog.builder()
        .book(book)
        .member(member)
        .history(keyword)
        .build();
    searchQuestionLogRepository.save(searchQuestionLog);

    return QuestionConverter.toQuestionList(questionList, lastId, questionPage.hasNext(),
        questionPage.getTotalElements());
  }

  @Override
  public QuestionList getSearchHistory(Long memberId) {
    List<Q> questionList = searchQuestionLogRepository.findAllByMemberIdOrderByCreatedAtDesc(
            memberId)
        .stream()
        .map(QuestionConverter::toQL)
        .toList();

    Long lastId = null;
    if (!questionList.isEmpty()) {
      lastId = questionList.get(questionList.size() - 1).id();
    }

    return QuestionConverter.toQuestionList(questionList, lastId, false, questionList.size());
  }

  @Override
  @Transactional
  public IdRes deleteSearchHistory(Long memberId, Long historyId) {
    SearchQuestionLog searchQuestionLog = searchQuestionLogRepository.findById(historyId)
        .orElseThrow(() -> new ApiException(CommonErrorCode.NOT_FOUND));

    if (!searchQuestionLog.getMember().getId().equals(memberId)) {
      throw new ApiException(CommonErrorCode.FORBIDDEN);
    }

    searchQuestionLogRepository.delete(searchQuestionLog);
    return GlobalConverter.toIdRes(historyId);
  }

  @Override
  @Transactional
  public void deleteAllSearchHistory(Long memberId) {
    searchQuestionLogRepository.deleteAllByMemberId(memberId);
  }

  @Override
  @Transactional
  public void deleteMyQuestions(Long memberId, IdList request) {
    if (request.idList() == null || request.idList().isEmpty()) {
      throw new ApiException(CommonErrorCode.BAD_REQUEST);
    }
    questionRepository.deleteMyQuestions(memberId, request.idList());
  }
}
