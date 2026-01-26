package com.bookripple.api.domain.blindsalepost.converter;

import com.bookripple.api.domain.blindsalepost.entity.BlindSalePost;
import com.bookripple.api.domain.blindsalepost.enums.BookCondition;
import com.bookripple.api.domain.blindsalepost.dto.BlindSalePostReqDto;
import com.bookripple.api.domain.blindsalepost.dto.BlindSalePostResDto;
import com.bookripple.api.domain.book.entity.Book;
import com.bookripple.api.domain.member.entity.Member;
import java.util.List;

public class BlindSalePostConverter {

    // 1. 요청 DTO -> 엔티티 변환 (등록 시 사용)
    public static BlindSalePost toBlindSalePost(BlindSalePostReqDto.Create request, Member member, Book book) {
        return BlindSalePost.builder()
                .member(member)
                .book(book)
                .title(request.title())
                .quote(request.blindContent()) // DTO의 blindContent -> 엔티티의 quote
                .price(request.price())
                .bookCondition(BookCondition.valueOf(request.bookCondition()))
                .build(); // postStatus는 엔티티 기본값 SALE 적용
    }

    // 2. 엔티티 -> 등록 응답 DTO 변환
    public static BlindSalePostResDto.Create toCreateResponse(BlindSalePost post) {
        return BlindSalePostResDto.Create.builder()
                .blindBookId(post.getId())
                .status(post.getPostStatus().name())
                .createdAt(post.getCreatedAt())
                .build();
    }


}