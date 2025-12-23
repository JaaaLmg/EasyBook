package com.ja.easybookbackend.pojo;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Inventory {
    @JsonProperty("inventory_id")
    private String inventoryId;
    private String isbn;
    private Integer quantity;
    @JsonProperty("safety_stock")
    private Integer safetyStock;
    @JsonProperty("reserved_quantity")
    private Integer reservedQuantity;
    @JsonProperty("location_code")
    private String locationCode;
    private String warehouse;
    @JsonProperty("last_restock")
    private LocalDateTime lastRestock;
    @JsonProperty("last_check")
    private LocalDateTime lastCheck;
    @JsonProperty("available_quantity")
    private Integer availableQuantity;
    private String status;
}
