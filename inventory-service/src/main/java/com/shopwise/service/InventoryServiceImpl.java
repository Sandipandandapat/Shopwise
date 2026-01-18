package com.shopwise.service;

import com.shopwise.controller.ProductServiceClient;
import com.shopwise.dto.InventoryRequestDto;
import com.shopwise.dto.InventoryResponseDto;
import com.shopwise.model.Inventory;
import com.shopwise.mapper.InventoryMapper;
import com.shopwise.repository.InventoryRepository;
import feign.FeignException;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class InventoryServiceImpl implements InventoryService{

    private final InventoryMapper mapper;
    private final InventoryRepository inventoryRepo;
    private final ProductServiceClient productServiceClient;

    @Autowired
    public InventoryServiceImpl(InventoryMapper mapper, InventoryRepository inventoryRepo,
                                ProductServiceClient productServiceClient){
        this.mapper=mapper;
        this.inventoryRepo=inventoryRepo;
        this.productServiceClient=productServiceClient;
    }

    @Override
    public void addStock(List<InventoryRequestDto> requestList) {
        requestList.forEach(request-> {
            String productId = request.getProductId();
            int quantity = request.getQuantity();
            try {
                productServiceClient.checkProductExists(productId);
            }catch (FeignException e){
                throw new RuntimeException("Product does not exist");
            }

            Inventory inventory = inventoryRepo.findByProductId(productId)
                    .orElse(Inventory.builder()
                            .productId(productId)
                            .onHand(0)
                            .build());

            inventory.setOnHand(inventory.getOnHand() + request.getQuantity());
            inventoryRepo.save(inventory);
        });
    }

    @Override
    public InventoryResponseDto getStockDetails(String productId) {
        return inventoryRepo.findByProductId(productId)
                .map(mapper::toInventoryResponseDto)
                .orElseThrow(()->new RuntimeException("productId not found"));
    }

    @Override
    public boolean isStockAvailable(String productId, int quantity) {
        return inventoryRepo.findByProductId(productId)
                .map(inventory -> inventory.getOnHand() >= quantity)
                .orElse(false);
    }

    @Transactional
    @Override
    public void decreaseStock(String productId, int quantity) {
        Inventory inventory = inventoryRepo.findByProductId(productId).get();
        inventory.setOnHand(inventory.getOnHand()-quantity);
        inventoryRepo.save(inventory);
    }

}
