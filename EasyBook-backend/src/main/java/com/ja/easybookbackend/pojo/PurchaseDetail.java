package com.ja.easybookbackend.pojo;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PurchaseDetail {
    @JsonProperty("detail_id")
    private String detailId;
    @JsonProperty("purchase_id")
    private String purchaseId;
    private String isbn;
    private Integer quantity;
    @JsonProperty("unit_price")
    private Double unitPrice;
    @JsonProperty("received_quantity")
    private Integer receivedQuantity;
    private String status;
}
