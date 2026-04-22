package com.ecommerce.project.abacatePay.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CustomerRequest {

    private String name;
    private String cellphone;
    private String email;
    private String taxId;
}
