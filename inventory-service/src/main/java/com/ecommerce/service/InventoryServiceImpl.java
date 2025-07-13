package com.ecommerce.service;

import com.ecommerce.controller.ProductServiceClient;
import com.ecommerce.dto.InventoryRequestDto;
import com.ecommerce.dto.InventoryResponseDto;
import com.ecommerce.model.Inventory;
import com.ecommerce.mapper.InventoryMapper;
import com.ecommerce.repository.InventoryRepository;
import feign.FeignException;
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
                            .quantity(0)
                            .build());

            inventory.setQuantity(inventory.getQuantity() + request.getQuantity());
            inventoryRepo.save(inventory);
        });
    }

    @Override
    public InventoryResponseDto getStockDetails(String productId) {
        return inventoryRepo.findByProductId(productId)
                .map(mapper::toInventoryResponseDto)
                .orElseThrow(()->new RuntimeException("productId not found"));
    }

}
