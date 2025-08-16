package com.shopwise.controller;

import com.shopwise.dto.OrderReqDto;
import com.shopwise.dto.OrderResponseDto;
import com.shopwise.service.OrderService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("order")
public class OrderController {

    private final OrderService orderService;

    public OrderController(OrderService orderService){
        this.orderService = orderService;
    }

    @PostMapping("create")
    public ResponseEntity<OrderResponseDto> createOrder(@RequestBody OrderReqDto orderRequest){
        return ResponseEntity.ok(orderService.createOrder(orderRequest));
    }

    @GetMapping("user/{userId}")
    public ResponseEntity<List<OrderResponseDto>> getUserOrders(@PathVariable long userId){
        return ResponseEntity.ok(orderService.getOrdersByUser(userId));
    }

}
