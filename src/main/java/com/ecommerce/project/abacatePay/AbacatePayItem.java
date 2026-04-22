package com.ecommerce.project.abacatePay;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AbacatePayItem {

    String externalId;
    String name;
    String description;
    Integer quantity;
    Double price;
}
