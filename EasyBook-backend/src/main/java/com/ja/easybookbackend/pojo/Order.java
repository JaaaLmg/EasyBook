package com.ja.easybookbackend.pojo;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Order {
    @JsonProperty("order_id")
    private String orderId;
    @JsonProperty("order_no")
    private String orderNo;
    @JsonProperty("customer_id")
    private String customerId;
    @JsonProperty("total_amount")
    private Double totalAmount;
    @JsonProperty("discount_amount")
    private Double discountAmount;
    @JsonProperty("actual_amount")
    private Double actualAmount;
    @JsonProperty("shipping_fee")
    private Double shippingFee;
    @JsonProperty("order_status")
    private String orderStatus;
    @JsonProperty("shipping_address")
    private String shippingAddress;
    @JsonProperty("recipient_name")
    private String recipientName;
    @JsonProperty("recipient_phone")
    private String recipientPhone;
    @JsonProperty("payment_method")
    private String paymentMethod;
    @JsonProperty("payment_time")
    private LocalDateTime paymentTime;
    @JsonProperty("order_time")
    private LocalDateTime orderTime;
    @JsonProperty("shipping_time")
    private LocalDateTime shippingTime;
    @JsonProperty("delivery_time")
    private LocalDateTime deliveryTime;
    private String notes;
}

