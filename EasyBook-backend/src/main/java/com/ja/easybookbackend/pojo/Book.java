package com.ja.easybookbackend.pojo;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Book {
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
}

