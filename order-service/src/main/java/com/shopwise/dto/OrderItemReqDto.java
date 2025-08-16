package com.shopwise.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderItemReqDto {
    @NotNull(message = "Product Id is required")
    private String productId;
    @Min(value = 1, message = "Quantity must be at least 1")
    private int quantity;
}
