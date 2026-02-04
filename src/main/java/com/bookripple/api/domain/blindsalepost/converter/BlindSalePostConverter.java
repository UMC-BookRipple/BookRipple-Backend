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
                .subtitle(request.subtitle())
                .description(request.description())
                .price(request.price())
                .bookCondition(BookCondition.valueOf(request.bookCondition()))
                .build();
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

    // 상세 정보 + 요청 인원수 변환
    public static BlindSalePostResDto.Detail toDetail(BlindSalePost post, long requestCount) {
        return BlindSalePostResDto.Detail.builder()
                .blindBookId(post.getId())
                .title(post.getTitle())
                .subtitle(post.getSubtitle())
                .description(post.getDescription())
                .price(post.getPrice())
                .status(post.getPostStatus().name())
                .requestCount(requestCount) // 카운트만 매핑
                .build();
    }

    // 요청자 명단 리스트 변환
    public static BlindSalePostResDto.PurchaseRequestList toPurchaseRequestList(Long blindBookId, List<PurchaseRequest> requests) {
        return BlindSalePostResDto.PurchaseRequestList.builder()
                .blindBookId(blindBookId)
                .requests(requests.stream()
                        .map(BlindSalePostConverter::toPurchaseRequestInfo)
                        .collect(Collectors.toList()))
                .build();
    }

    // 개별 요청 정보 변환
    public static BlindSalePostResDto.PurchaseRequestInfo toPurchaseRequestInfo(PurchaseRequest request) {
        return BlindSalePostResDto.PurchaseRequestInfo.builder()
                .requestId(request.getId())
                .name(request.getMember().getName()) // 닉네임 또는 익명 명칭 사용
                .status(request.getStatus().name())
                .build();
    }

    // List와 다음 페이지 정보를 SliceResponse DTO로 변환
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