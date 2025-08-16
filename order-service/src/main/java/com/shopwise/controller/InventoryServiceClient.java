package com.shopwise.controller;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient( name = "inventory-service")
public interface InventoryServiceClient {
    @GetMapping("inventory/check")
    public boolean isStockAvailable(@RequestParam String productId, @RequestParam int quantity);
}
