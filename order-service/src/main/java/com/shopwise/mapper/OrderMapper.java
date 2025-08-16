package com.shopwise.mapper;

import com.shopwise.dto.OrderItemReqDto;
import com.shopwise.dto.OrderReqDto;
import com.shopwise.dto.OrderResponseDto;
import com.shopwise.entity.OrderItems;
import com.shopwise.entity.Order;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface OrderMapper {
    public OrderItems toOrderItemsEntity(OrderItemReqDto orderItem);
    public OrderResponseDto toOrderResponseDto(Order orders);
}
