package com.bookripple.api.domain.aladin.service;

import com.bookripple.api.domain.aladin.config.AladinProperties;
import com.bookripple.api.domain.aladin.dto.AladinItemLookUpResDto;
import com.bookripple.api.domain.aladin.dto.AladinSearchResDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestClient;

@Service
@RequiredArgsConstructor
public class AladinService {

    private final AladinProperties props;

    private RestClient client() {
        if (!StringUtils.hasText(props.getBaseUrl())) {
            throw new IllegalStateException("aladin.base-url is not configured");
        }
        return RestClient.builder()
                .baseUrl(props.getBaseUrl())
                .build();
    }

    public AladinSearchResDto search(String keyword, int start, int size, String queryType, String searchTarget) {
        validateKeyword(keyword);

        int safeStart = Math.max(start, 1);
        int safeSize = clamp(size, 1, 50);

        // /ttb/api/ItemSearch.aspx (base-url에 /ttb/api 포함 여부에 따라 path 조정 필요)
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

    public AladinItemLookUpResDto lookup(Long aladinItemId) {
        if (aladinItemId == null) {
            throw new IllegalArgumentException("aladinItemId must not be null");
        }

        return client().get()
                .uri(uriBuilder -> uriBuilder
                        .path("/ItemLookUp.aspx")
                        .queryParam("ttbkey", props.getTtbKey())
                        .queryParam("ItemIdType", "ItemId")
                        .queryParam("ItemId", aladinItemId)
                        .queryParam("output", props.getOutput())
                        .queryParam("Version", props.getVersion())
                        .build())
                .retrieve()
                .body(AladinItemLookUpResDto.class);
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
        if (value < min) return min;
        return Math.min(value, max);
    }
}
