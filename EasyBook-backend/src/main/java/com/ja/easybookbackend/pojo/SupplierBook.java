package com.ja.easybookbackend.pojo;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SupplierBook {
    @JsonProperty("supplier_id")
    private String supplierId;
    private String isbn;
    @JsonProperty("supply_price")
    private Double supplyPrice;
    @JsonProperty("min_order_quantity")
    private Integer minOrderQuantity;
    @JsonProperty("delivery_days")
    private Integer deliveryDays;
    @JsonProperty("is_available")
    private Boolean isAvailable;
    @JsonProperty("last_update")
    private LocalDateTime lastUpdate;
}
