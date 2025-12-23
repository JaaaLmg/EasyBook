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
public class Author {
    @JsonProperty("author_id")
    private String authorId;
    @JsonProperty("author_name")
    private String authorName;
    private String nationality;
    @JsonProperty("birth_date")
    private LocalDate birthDate;
    private String biography;
    @JsonProperty("create_time")
    private LocalDateTime createTime;
}

