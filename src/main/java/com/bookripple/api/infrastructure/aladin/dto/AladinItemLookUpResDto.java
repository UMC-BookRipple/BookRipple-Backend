package com.bookripple.api.infrastructure.aladin.dto;

import java.util.List;
import lombok.Getter;

@Getter
public class AladinItemLookUpResDto {

  private List<Item> item;

  @Getter
  public static class Item {

    private Long itemId;
    private String title;
    private String author;
    private String publisher;
    private String pubDate;
    private String cover;
    private String isbn10;
    private String isbn13;
    private String description; // story
    private SubInfo subInfo;    // pageCount (알라딘에서 이렇게 받아옴)

    @Getter
    public static class SubInfo {

      private Integer itemPage; // totalPage
    }
  }
}
