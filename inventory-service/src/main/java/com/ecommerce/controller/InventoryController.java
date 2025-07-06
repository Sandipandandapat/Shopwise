package com.ecommerce.controller;

import com.ecommerce.dto.InventoryRequestDto;
import com.ecommerce.dto.InventoryResponseDto;
import com.ecommerce.model.Inventory;
import com.ecommerce.service.InventoryService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("inventory")
public class InventoryController {
    private final InventoryService inventoryService;

    public InventoryController(InventoryService inventoryService){
        this.inventoryService=inventoryService;
    }

    @PostMapping("add")
    public ResponseEntity<Void> addStock(@RequestBody InventoryRequestDto request){
        inventoryService.addStock(request);
        return ResponseEntity.ok().build();
    }

    @GetMapping("{productId}")
    public ResponseEntity<InventoryResponseDto> getStockDetails(@PathVariable String productId){
        InventoryResponseDto response = inventoryService.getStockDetails(productId);
        return ResponseEntity.ok(response);
    }

}
