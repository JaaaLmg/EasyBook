package com.ja.easybookbackend.pojo;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Supplier {
    @JsonProperty("supplier_id")
    private String supplierId;
    @JsonProperty("supplier_name")
    private String supplierName;
    @JsonProperty("contact_person")
    private String contactPerson;
    @JsonProperty("contact_phone")
    private String contactPhone;
    private String email;
    private String address;
    @JsonProperty("bank_account")
    private String bankAccount;
    @JsonProperty("tax_number")
    private String taxNumber;
    @JsonProperty("cooperation_status")
    private String cooperationStatus;
    private Double rating;
    @JsonProperty("create_time")
    private LocalDateTime createTime;
    @JsonProperty("update_time")
    private LocalDateTime updateTime;
}
