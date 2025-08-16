package com.shopwise.service;

import com.shopwise.dto.OrderReqDto;
import com.shopwise.dto.OrderResponseDto;

import java.util.List;

public interface OrderService {
    OrderResponseDto createOrder(OrderReqDto orderRequest);

    List<OrderResponseDto> getOrdersByUser(long userId);
}
