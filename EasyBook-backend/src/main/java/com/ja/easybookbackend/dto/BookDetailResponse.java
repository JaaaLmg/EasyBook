package com.ja.easybookbackend.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.ja.easybookbackend.pojo.Author;
import java.time.LocalDate;
import java.time.LocalDateTime;
import com.ja.easybookbackend.pojo.Publisher;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Collections;
import java.util.List;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BookDetailResponse {
    private String isbn;
    private String title;
    private String edition;
    @JsonProperty("publisher_id")
    private String publisherId;
    @JsonProperty("publish_date")
    private LocalDate publishDate;
    private Double price;
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
    @JsonProperty("create_time")
    private LocalDateTime createTime;
    @JsonProperty("update_time")
    private LocalDateTime updateTime;
    private List<Author> authors;
    private Publisher publisher;
    private List<Map<String, Object>> categories;
    private List<Map<String, Object>> keywords;
    private Map<String, Object> series;
    private InventorySummary inventory;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class InventorySummary {
        private Integer quantity;
        @JsonProperty("reserved_quantity")
        private Integer reservedQuantity;
        @JsonProperty("available_quantity")
        private Integer availableQuantity;
        @JsonProperty("safety_stock")
        private Integer safetyStock;
        private String status;
    }

    public static BookDetailResponse empty() {
        BookDetailResponse r = new BookDetailResponse();
        r.setAuthors(Collections.emptyList());
        r.setCategories(Collections.emptyList());
        r.setKeywords(Collections.emptyList());
        return r;
    }
}
