package com.shopwise.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderReqDto {
    @NotNull(message = "User Id is required")
    private long userId;
    @NotEmpty(message = "Order items can not be empty")
    @Valid
    private List<OrderItemReqDto> orderItems = new ArrayList<>();
}
