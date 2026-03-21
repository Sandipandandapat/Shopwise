package com.shopwise.service;

import com.shopwise.dto.InventoryRequestDto;
import com.shopwise.dto.InventoryResponseDto;

import java.util.List;

public interface InventoryService {
    void addStock(List<InventoryRequestDto> request);

    InventoryResponseDto getStockDetails(String productId);

    boolean isStockAvailable(String productId, int quantity);

    void deductStockForOrder(Long orderId);

    void releaseStockForOrder(Long orderId);
}
