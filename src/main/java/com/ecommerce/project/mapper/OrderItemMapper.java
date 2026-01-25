package com.ecommerce.project.mapper;

import com.ecommerce.project.model.OrderItem;
import com.ecommerce.project.payload.OrderItemDTO;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring", uses = {ProductMapper.class})
public interface OrderItemMapper {

    OrderItemDTO toDTO(OrderItem orderItem);
    List<OrderItemDTO> toDTO(List<OrderItem> orderItems);
}
