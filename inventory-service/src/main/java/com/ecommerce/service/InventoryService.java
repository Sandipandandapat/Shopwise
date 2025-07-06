package com.ecommerce.service;

import com.ecommerce.dto.InventoryRequestDto;
import com.ecommerce.dto.InventoryResponseDto;

import java.util.List;

public interface InventoryService {
    void addStock(InventoryRequestDto request);

    InventoryResponseDto getStockDetails(String productId);
}
