package com.bookripple.api.domain.question.service;

import com.bookripple.api.common.code.BookErrorCode;
import com.bookripple.api.common.error.ApiException;
import com.bookripple.api.domain.book.entity.Book;
import com.bookripple.api.domain.book.repository.BookRepository;
import com.bookripple.api.domain.member.entity.Member;
import com.bookripple.api.domain.member.repository.MemberRepository;
import com.bookripple.api.domain.question.converter.QuestionConverter;
import com.bookripple.api.domain.question.entity.Question;
import com.bookripple.api.domain.question.repository.QuestionRepository;
import com.bookripple.api.global.converter.GlobalConverter;
import com.bookripple.api.global.dto.GlobalDto.ContentReq;
import com.bookripple.api.global.dto.GlobalDto.IdRes;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

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
}
