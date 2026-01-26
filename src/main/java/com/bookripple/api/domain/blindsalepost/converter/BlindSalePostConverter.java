package com.bookripple.api.domain.blindsalepost.converter;

import com.bookripple.api.domain.blindsalepost.entity.BlindSalePost;
import com.bookripple.api.domain.blindsalepost.entity.PurchaseRequest;
import com.bookripple.api.domain.blindsalepost.enums.BookCondition;
import com.bookripple.api.domain.blindsalepost.dto.BlindSalePostReqDto;
import com.bookripple.api.domain.blindsalepost.dto.BlindSalePostResDto;
import com.bookripple.api.domain.book.entity.Book;
import com.bookripple.api.domain.member.entity.Member;
import java.util.List;
import java.util.stream.Collectors;

public class BlindSalePostConverter {

    //  요청 DTO -> 엔티티 변환 (등록 시 사용)
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

    //  엔티티 -> 등록 응답 DTO 변환
    public static BlindSalePostResDto.Create toCreateResponse(BlindSalePost post) {
        return BlindSalePostResDto.Create.builder()
                .blindBookId(post.getId())
                .status(post.getPostStatus().name())
                .createdAt(post.getCreatedAt())
                .build();
    }

    //  [목록 조회] 엔티티 -> 목록 요약 DTO 변환
    public static BlindSalePostResDto.ListElement toListElement(BlindSalePost post) {
        return BlindSalePostResDto.ListElement.builder()
                .blindBookId(post.getId())
                .title(post.getTitle())
                //.author(post.getBook().getAuthor()) // 연관된 Book 엔티티에서 저자명 추출
                .price(post.getPrice())
                .status(post.getPostStatus().name()) // SALE, DONE 등
                .build();
    }

    //  [상세 조회] 엔티티 + 요청자 명단 -> 상세 DTO 변환
    public static BlindSalePostResDto.Detail toDetail(BlindSalePost post, List<PurchaseRequest> requests) {
        return BlindSalePostResDto.Detail.builder()
                .blindBookId(post.getId())
                .title(post.getTitle())
                .quote(post.getQuote()) // 포스트잇 문구
                .price(post.getPrice())
                .status(post.getPostStatus().name())
                .requestCount((long) requests.size()) // 실시간 요청 인원수 계산
                .requests(requests.stream()
                        .map(BlindSalePostConverter::toPurchaseRequestInfo)
                        .collect(Collectors.toList())) // 요청자 리스트 변환
                .build();
    }

    // [상세 조회 내부] 구매 요청 엔티티 -> 요청자 정보 DTO 변환
    public static BlindSalePostResDto.PurchaseRequestInfo toPurchaseRequestInfo(PurchaseRequest request) {
        return BlindSalePostResDto.PurchaseRequestInfo.builder()
                .requestId(request.getId())
                .name(request.getMember().getName()) // 멤버 엔티티의 닉네임을 사용
                .status(request.getStatus().name())
                .build();
    }

    // [목록 조회] List와 다음 페이지 정보를 SliceResponse DTO로 변환
    public static BlindSalePostResDto.SliceResponse toSliceResponse(
            List<BlindSalePostResDto.ListElement> content,
            Long nextCursor,
            boolean hasNext) {

        return BlindSalePostResDto.SliceResponse.builder()
                .content(content)
                .nextCursor(nextCursor)
                .hasNext(hasNext)
                .build();
    }


}