package com.ja.easybookbackend.pojo;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OutOfStockRecord {
    @JsonProperty("record_id")
    private String recordId;
    private String isbn;
    @JsonProperty("book_title")
    private String bookTitle;
    @JsonProperty("publisher_name")
    private String publisherName;
    @JsonProperty("required_quantity")
    private Integer requiredQuantity;
    @JsonProperty("customer_id")
    private String customerId;
    @JsonProperty("customer_email")
    private String customerEmail;
    @JsonProperty("customer_phone")
    private String customerPhone;
    @JsonProperty("source_type")
    private String sourceType;
    private Integer priority;
    private String status;
    @JsonProperty("notify_status")
    private String notifyStatus;
    @JsonProperty("notify_time")
    private LocalDateTime notifyTime;
    @JsonProperty("created_time")
    private LocalDateTime createdTime;
    @JsonProperty("resolve_time")
    private LocalDateTime resolveTime;
    private String remark;
}
