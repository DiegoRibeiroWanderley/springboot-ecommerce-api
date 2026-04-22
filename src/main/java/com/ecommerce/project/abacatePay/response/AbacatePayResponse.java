package com.ecommerce.project.abacatePay.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AbacatePayResponse {
    private Data data;
    private String error;

    @lombok.Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Data {
        String id;
        String url;
        String status;
        String devMode;
        List<String> methods;
        List<Object> products;
        String frequency;
        Double amount;
        Customer customer;
        boolean allowCoupons;
        List<String> coupons;
    }

    @lombok.Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Customer {
        String id;
        Metadata metadata;
    }

    @lombok.Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Metadata {
        String name;
        String cellphone;
        String email;
        String taxId;
    }
}
