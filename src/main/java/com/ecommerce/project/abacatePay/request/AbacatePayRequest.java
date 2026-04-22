package com.ecommerce.project.abacatePay.request;

import com.ecommerce.project.abacatePay.AbacatePayItem;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AbacatePayRequest {

    String frequency;
    List<String> methods;
    List<AbacatePayItem> products;
    String returnUrl;
    String completionUrl;
    String customerId;
}
