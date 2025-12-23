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
public class PurchaseOrder {
    @JsonProperty("purchase_id")
    private String purchaseId;
    @JsonProperty("purchase_no")
    private String purchaseNo;
    @JsonProperty("supplier_id")
    private String supplierId;
    @JsonProperty("total_amount")
    private Double totalAmount;
    private String status;
    @JsonProperty("order_date")
    private LocalDate orderDate;
    @JsonProperty("expected_delivery")
    private LocalDate expectedDelivery;
    @JsonProperty("actual_delivery")
    private LocalDate actualDelivery;
    @JsonProperty("create_time")
    private LocalDateTime createTime;
    @JsonProperty("create_by")
    private String createBy;
    @JsonProperty("approve_time")
    private LocalDateTime approveTime;
    @JsonProperty("approve_by")
    private String approveBy;
}
