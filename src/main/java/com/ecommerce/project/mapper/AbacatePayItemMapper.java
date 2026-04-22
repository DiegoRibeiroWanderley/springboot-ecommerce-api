package com.ecommerce.project.mapper;

import com.ecommerce.project.abacatePay.AbacatePayItem;
import com.ecommerce.project.model.Product;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface AbacatePayItemMapper {

    @Mapping(source = "productId", target = "externalId")
    @Mapping(source = "productName", target = "name")
    AbacatePayItem toAbacatePayItem(Product product);
}
