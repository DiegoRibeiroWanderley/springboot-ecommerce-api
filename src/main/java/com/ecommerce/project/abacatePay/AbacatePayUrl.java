package com.ecommerce.project.abacatePay;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AbacatePayUrl {

    private String returnUrl;
    private String completionUrl;
}
