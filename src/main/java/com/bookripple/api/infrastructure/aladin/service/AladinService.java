package com.bookripple.api.infrastructure.aladin.service;

import com.bookripple.api.infrastructure.aladin.config.AladinProperties;
import com.bookripple.api.infrastructure.aladin.dto.AladinItemLookUpResDto;
import com.bookripple.api.infrastructure.aladin.dto.AladinSearchResDto;
import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import java.util.concurrent.TimeUnit;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestClient;

@Service
@RequiredArgsConstructor
public class AladinService {

  private final AladinProperties props;

  private final Cache<String, AladinSearchResDto> newBooksCache = Caffeine.newBuilder()
      .expireAfterWrite(24, TimeUnit.HOURS) // 24시간 만료
      .maximumSize(10)
      .build();

  public AladinSearchResDto getSpecialNewBooks() {
    return newBooksCache.get("key", k -> getSpecialItems());
  }

  private RestClient client() {
    if (!StringUtils.hasText(props.getBaseUrl())) {
      throw new IllegalStateException("aladin.base-url is not configured");
    }
    return RestClient.builder()
        .baseUrl(props.getBaseUrl())
        .build();
  }

  //1. 알라딘 검색 API
  public AladinSearchResDto search(String keyword, int start, int size, String queryType,
      String searchTarget) {

    validateKeyword(keyword);
    int safeStart = Math.max(start, 1);
    int safeSize = clamp(size, 1, 50);

    // /ttb/api/ItemSearch.aspx url 빌드
    return client().get()
        .uri(uriBuilder -> uriBuilder
            .path("/ItemSearch.aspx")
            .queryParam("ttbkey", props.getTtbKey())
            .queryParam("Query", keyword)
            .queryParam("QueryType", queryType)         // default: Keyword
            .queryParam("SearchTarget", searchTarget)   // default: Book
            .queryParam("Start", safeStart)
            .queryParam("MaxResults", safeSize)
            .queryParam("output", props.getOutput())
            .queryParam("Version", props.getVersion())
            .build())
        .retrieve()
        .body(AladinSearchResDto.class);
  }


  //2. 알라딘 도서 상세 조회 API
  public AladinItemLookUpResDto lookup(Long itemId, String optResult) {
    if (itemId == null) {
      throw new IllegalArgumentException("itemId must not be null");
    }

    var req = client().get().uri(uriBuilder -> {
      var uri = uriBuilder
          .path("/ItemLookUp.aspx")
          .queryParam("ttbkey", props.getTtbKey())
          .queryParam("ItemIdType", "ItemId")
          .queryParam("ItemId", itemId)
          .queryParam("output", props.getOutput())
          .queryParam("Version", props.getVersion());

      if (StringUtils.hasText(optResult)) {
        uri.queryParam("OptResult", optResult); // ebookList,usedList,reviewList 등
      }

      return uri.build();
    });

    return req.retrieve().body(AladinItemLookUpResDto.class);
  }


  private void validateKeyword(String keyword) {
    if (!StringUtils.hasText(keyword)) {
      throw new IllegalArgumentException("keyword must not be blank");
    }
    if (keyword.length() > 100) {
      throw new IllegalArgumentException("keyword length must be <= 100");
    }
  }

  private int clamp(int value, int min, int max) {
    if (value < min) {
      return min;
    }
    return Math.min(value, max);
  }

  public AladinSearchResDto getSpecialItems() {
    return client().get()
        .uri(uriBuilder -> uriBuilder
            .path("/ItemList.aspx")
            .queryParam("ttbkey", props.getTtbKey())
            .queryParam("MaxResults", 3)
            .queryParam("QueryType", "ItemNewSpecial")         // default: Keyword
            .queryParam("SearchTarget", "Book")   // default: Book
            .queryParam("output", props.getOutput())
            .queryParam("Version", props.getVersion())
            .build())
        .retrieve()
        .body(AladinSearchResDto.class);
  }
}
