package com.ja.easybookbackend.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

@Data
public class CreateBookRequest {
    private String isbn;
    private String title;
    @JsonProperty("publisher_id")
    private String publisherId;
    private Double price;
    @JsonProperty("edition")
    private String edition;
    @JsonProperty("publish_date")
    private String publishDate;
    @JsonProperty("page_count")
    private Integer pageCount;
    private String format;
    private String language;
    private String description;
    @JsonProperty("table_of_contents")
    private String tableOfContents;
    @JsonProperty("cover_image")
    private String coverImage;
    @JsonProperty("series_id")
    private String seriesId;
    @JsonProperty("book_type")
    private String bookType;
    private String status;
    @JsonProperty("initial_stock")
    private Integer initialStock;
    @JsonProperty("safety_stock")
    private Integer safetyStock;
    private String warehouse;
    @JsonProperty("location_code")
    private String locationCode;
    @JsonProperty("authors")
    private List<AuthorRef> authors;
    @JsonProperty("author_names")
    private List<String> authorNames;
    @JsonProperty("category_ids")
    private List<String> categoryIds;
    @JsonProperty("keywords")
    private List<String> keywords;

    @Data
    public static class AuthorRef {
        @JsonProperty("author_id")
        private String authorId;
        @JsonProperty("author_order")
        private Integer authorOrder;
    }
}
