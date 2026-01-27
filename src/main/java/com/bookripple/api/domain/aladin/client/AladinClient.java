package com.bookripple.api.domain.aladin.client;

import com.bookripple.api.domain.aladin.dto.AladinSearchResDto;
import com.bookripple.api.domain.aladin.dto.AladinItemLookUpResDto;

public interface AladinClient {
    AladinSearchResDto search(String keyword, int start, int size, String queryType, String searchTarget);
    AladinItemLookUpResDto lookup(Long aladinItemId);
}
