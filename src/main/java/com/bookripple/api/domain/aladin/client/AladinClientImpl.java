package com.bookripple.api.domain.aladin.client;

import com.bookripple.api.domain.aladin.config.AladinProperties;
import com.bookripple.api.domain.aladin.dto.AladinItemLookUpResDto;
import com.bookripple.api.domain.aladin.dto.AladinSearchResDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestClient;

@Component
@RequiredArgsConstructor
public class AladinClientImpl implements AladinClient {

    private final AladinProperties props;

    private RestClient restClient() {
        if (!StringUtils.hasText(props.getBaseUrl())) {
            throw new IllegalStateException("aladin.base-url is not configured");
        }
        return RestClient.builder()
                .baseUrl(props.getBaseUrl())
                .build();
    }

    @Override
    public AladinSearchResDto search(String keyword, int start, int size, String queryType, String searchTarget) {
        // ItemSearch.aspx
        return restClient().get()
                .uri(uriBuilder -> uriBuilder
                        .path("/ItemSearch.aspx")
                        .queryParam("ttbkey", props.getTtbKey())
                        .queryParam("Query", keyword)
                        .queryParam("QueryType", queryType)
                        .queryParam("SearchTarget", searchTarget)
                        .queryParam("Start", start)
                        .queryParam("MaxResults", size)
                        .queryParam("output", props.getOutput())
                        .queryParam("Version", props.getVersion())
                        .build())
                .retrieve()
                .body(AladinSearchResDto.class);
    }

    @Override
    public AladinItemLookUpResDto lookup(Long aladinItemId) {
        // ItemLookUp.aspx
        return restClient().get()
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
}
