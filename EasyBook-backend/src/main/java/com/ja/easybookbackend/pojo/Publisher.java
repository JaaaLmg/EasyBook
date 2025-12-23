package com.ja.easybookbackend.pojo;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Publisher {
    @JsonProperty("publisher_id")
    private String publisherId;
    @JsonProperty("publisher_name")
    private String publisherName;
    private String address;
    @JsonProperty("contact_phone")
    private String contactPhone;
    @JsonProperty("contact_person")
    private String contactPerson;
    private String email;
    private String website;
    private String description;
    @JsonProperty("create_time")
    private LocalDateTime createTime;
    @JsonProperty("update_time")
    private LocalDateTime updateTime;
}

