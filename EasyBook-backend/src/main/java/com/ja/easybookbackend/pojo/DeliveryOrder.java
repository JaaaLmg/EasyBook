package com.ja.easybookbackend.pojo;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DeliveryOrder {
    @JsonProperty("delivery_id")
    private String deliveryId;
    @JsonProperty("order_id")
    private String orderId;
    @JsonProperty("delivery_no")
    private String deliveryNo;
    @JsonProperty("delivery_company")
    private String deliveryCompany;
    @JsonProperty("tracking_no")
    private String trackingNo;
    @JsonProperty("delivery_address")
    private String deliveryAddress;
    @JsonProperty("delivery_status")
    private String deliveryStatus;
    @JsonProperty("shipping_fee")
    private Double shippingFee;
    @JsonProperty("prepare_time")
    private LocalDateTime prepareTime;
    @JsonProperty("ship_time")
    private LocalDateTime shipTime;
}

