package com.ja.easybookbackend.pojo;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PurchaseShortageMap {
    @JsonProperty("purchase_id")
    private String purchaseId;
    @JsonProperty("record_id")
    private String recordId;
    @JsonProperty("mapped_quantity")
    private Integer mappedQuantity;
}
