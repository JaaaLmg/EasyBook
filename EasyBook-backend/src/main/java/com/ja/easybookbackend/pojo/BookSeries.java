package com.ja.easybookbackend.pojo;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BookSeries {
    @JsonProperty("series_id")
    private String seriesId;
    @JsonProperty("series_name")
    private String seriesName;
    @JsonProperty("publisher_id")
    private String publisherId;
    @JsonProperty("total_books")
    private Integer totalBooks;
    private String description;
}
