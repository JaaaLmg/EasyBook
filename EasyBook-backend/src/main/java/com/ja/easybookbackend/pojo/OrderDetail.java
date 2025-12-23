package com.ja.easybookbackend.pojo;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderDetail {
    @JsonProperty("detail_id")
    private String detailId;
    @JsonProperty("order_id")
    private String orderId;
    private String isbn;
    private Integer quantity;
    @JsonProperty("unit_price")
    private Double unitPrice;
    @JsonProperty("discount_rate")
    private Double discountRate;
    private Double subtotal;
}

