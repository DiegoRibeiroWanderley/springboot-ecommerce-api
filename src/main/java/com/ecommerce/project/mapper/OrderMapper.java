package com.ecommerce.project.mapper;

import com.ecommerce.project.model.Order;
import com.ecommerce.project.payload.OrderDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", uses = OrderItemMapper.class)
public interface OrderMapper {

    @Mapping(target = "addressId", ignore = true)
    OrderDTO toOrderDTO(Order order);
}
