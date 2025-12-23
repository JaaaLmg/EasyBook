package com.ja.easybookbackend.pojo;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Keyword {
    @JsonProperty("keyword_id")
    private String keywordId;
    private String keyword;
    @JsonProperty("search_count")
    private Integer searchCount;
    @JsonProperty("create_time")
    private LocalDateTime createTime;
}
