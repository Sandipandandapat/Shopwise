package com.shopwise.controller;

import com.shopwise.dto.InventoryRequestDto;
import com.shopwise.dto.InventoryResponseDto;
import com.shopwise.service.InventoryService;
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
    public ResponseEntity<Void> addStock(@RequestBody List<InventoryRequestDto> request){
        inventoryService.addStock(request);
        return ResponseEntity.ok().build();
    }

    @GetMapping("{productId}")
    public ResponseEntity<InventoryResponseDto> getStockDetails(@PathVariable String productId){
        InventoryResponseDto response = inventoryService.getStockDetails(productId);
        return ResponseEntity.ok(response);
    }

    @GetMapping("check")
    public ResponseEntity<Boolean> checkStockAvailability(@RequestParam String productId, @RequestParam int quantity){
        return ResponseEntity.ok(inventoryService.isStockAvailable(productId,quantity));
    }

}
