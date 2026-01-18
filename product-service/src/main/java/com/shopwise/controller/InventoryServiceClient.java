package com.shopwise.controller;

import com.shopwise.dto.InventoryRequestDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

@FeignClient(name = "inventory-service")
public interface InventoryServiceClient {
    @PostMapping("inventory/add")
    public void addStock(@RequestBody List<InventoryRequestDto> request);
}
