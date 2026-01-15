package com.ecommerce.project.mapper;

import com.ecommerce.project.model.Cart;
import com.ecommerce.project.payload.CartDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface CartMapper {
    @Mapping(target = "products", ignore = true)
    CartDTO toDTO(Cart cart);
}
