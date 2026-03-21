package com.shopwise.service;

import com.shopwise.controller.ProductServiceClient;
import com.shopwise.dto.InventoryRequestDto;
import com.shopwise.dto.InventoryResponseDto;
import com.shopwise.model.Inventory;
import com.shopwise.mapper.InventoryMapper;
import com.shopwise.model.InventoryReservation;
import com.shopwise.repository.InventoryRepository;
import feign.FeignException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class InventoryServiceImpl implements InventoryService{

    private final InventoryMapper mapper;
    private final InventoryRepository inventoryRepo;
    private final ProductServiceClient productServiceClient;
    private final InventoryReservationService reservationService;


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
                .map(inventory -> (inventory.getOnHand()-inventory.getReserved()) >= quantity)
                .orElse(false);
    }

    @Transactional
    @Override
    public void deductStockForOrder(Long orderId) {
        List<InventoryReservation> reservations = reservationService.getReservedItems(orderId);

        for (InventoryReservation reservation:reservations){
            Inventory inventory = inventoryRepo.findByProductId(reservation.getProductId())
                    .orElseThrow(() -> new RuntimeException("Inventory not found for product: "+reservation.getProductId()));
            inventory.setOnHand(inventory.getOnHand()-reservation.getQuantity());
            inventory.setReserved(inventory.getReserved()-reservation.getQuantity());
            inventoryRepo.save(inventory);
            log.info("Deducted stock for product id: {}, now on-hand: {}",reservation.getProductId(),inventory.getOnHand());
        }
        reservationService.markFulfilled(orderId);
    }

    @Transactional
    @Override
    public void releaseStockForOrder(Long orderId) {
        List<InventoryReservation> reservations = reservationService.getReservedItems(orderId);

        for (InventoryReservation reservation:reservations){
            Inventory inventory = inventoryRepo.findByProductId(reservation.getProductId())
                    .orElseThrow(() -> new RuntimeException("Inventory not found for product: "+reservation.getProductId()));
            inventory.setReserved(inventory.getReserved()-reservation.getQuantity());
            inventoryRepo.save(inventory);
            log.info("Released reserved for product id: {}",reservation.getProductId());
        }
        reservationService.markReleased(orderId);
    }

}
