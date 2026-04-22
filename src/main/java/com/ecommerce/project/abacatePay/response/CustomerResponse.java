package com.ecommerce.project.abacatePay.response;

import com.ecommerce.project.abacatePay.request.CustomerRequest;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CustomerResponse {

    private Data data;
    private boolean error;

    @lombok.Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Data {
        private String id;
        private CustomerRequest metadata;
    }
}
