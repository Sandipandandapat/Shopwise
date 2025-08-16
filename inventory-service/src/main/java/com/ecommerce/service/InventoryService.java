package com.ecommerce.service;

import com.ecommerce.dto.InventoryRequestDto;
import com.ecommerce.dto.InventoryResponseDto;

import java.util.List;

public interface InventoryService {
    void addStock(List<InventoryRequestDto> request);

    InventoryResponseDto getStockDetails(String productId);

    boolean isStockAvailable(String productId, int quantity);

    void decreaseStock(String productId, int quantity);
}
