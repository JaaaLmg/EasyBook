package com.ja.easybookbackend.pojo;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreditRule {
    private Integer level;
    @JsonProperty("level_name")
    private String levelName;
    @JsonProperty("discount_rate")
    private Double discountRate;
    @JsonProperty("overdraft_enabled")
    private Boolean overdraftEnabled;
    @JsonProperty("overdraft_limit")
    private Integer overdraftLimit;
    @JsonProperty("min_total_purchase")
    private Double minTotalPurchase;
    private String description;
}

