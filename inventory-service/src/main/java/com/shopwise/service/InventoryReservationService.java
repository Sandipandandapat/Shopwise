package com.shopwise.service;

import com.shopwise.events.OrderEvent;
import com.shopwise.events.OrderItemEvent;
import com.shopwise.kafka.InventoryOutcomePublisher;
import com.shopwise.model.Inventory;
import com.shopwise.model.InventoryReservation;
import com.shopwise.model.ReservationStatus;
import com.shopwise.repository.InventoryRepository;
import com.shopwise.repository.InventoryReservationRepository;
import jakarta.annotation.Nullable;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class InventoryReservationService {

    private final InventoryRepository inventoryRepository;
    private final InventoryReservationRepository reservationRepository;
    private final InventoryOutcomePublisher outcomePublisher;

    @Transactional
    public void reserveForOrder(OrderEvent event, @Nullable String correlationId){
        boolean allReserved = true;

        for(OrderItemEvent item: event.getItems()){
            String productId = item.getProductId();
            int quantity = item.getQuantity();

            var existing  = reservationRepository.findByOrderIdAndProductId(event.getOrderId(),productId)
                    .orElse(null);

            if(existing != null && ReservationStatus.RESERVED.equals(existing.getStatus())){
                continue;
            }

            inventoryRepository.findByProductId(productId)
                    .orElseGet(() -> inventoryRepository.save(Inventory.builder()
                                    .productId(productId)
                                    .onHand(0)
                                    .reserved(0)
                                    .build()));

            int updated = inventoryRepository.tryReserve(productId,quantity);
            if(updated == 1){
                saveReservation(event.getOrderId(), productId, quantity, ReservationStatus.RESERVED);
            } else {
                allReserved = false;
                saveReservation(event.getOrderId(), productId, quantity, ReservationStatus.REJECTED);
            }
        }

        if (allReserved) {
            outcomePublisher.publishReserved(event.getOrderId(), event.getUserId(), event.getTotalAmount(), correlationId);
        } else {
            releasePartialReservations(event.getOrderId(), event.getItems());
            outcomePublisher.publishRejected(event.getOrderId(),
                    "OUT_OF_STOCK", correlationId);
        }
    }

    private void saveReservation(Long orderId, String productId, int quantity, ReservationStatus status){
        var res = reservationRepository.findByOrderIdAndProductId(orderId,productId)
                .orElseGet(() -> InventoryReservation.builder()
                        .orderId(orderId)
                        .productId(productId)
                        .quantity(quantity)
                        .build());
        res.setStatus(status);
        res.setCreatedAt(LocalDateTime.now());
        reservationRepository.save(res);
    }

    private void releasePartialReservations(Long orderId, List<OrderItemEvent> items){
        for(OrderItemEvent item: items) {
            reservationRepository.findByOrderIdAndProductId(orderId, item.getProductId())
                    .filter( r -> ReservationStatus.RESERVED.equals(r.getStatus()))
                    .ifPresent(r -> {
                        inventoryRepository.releaseReservation(r.getProductId(),
                                r.getQuantity());
                        r.setStatus(ReservationStatus.RELEASED);
                        reservationRepository.save(r);
                    });
        }
    }

    public List<InventoryReservation> getReservedItems(Long orderId) {
        return reservationRepository
                .findByOrderIdAndStatus(orderId, ReservationStatus.RESERVED);
    }

    @Transactional
    public void markFulfilled(Long orderId) {
        List<InventoryReservation> reservations = getReservedItems(orderId);
        reservations.forEach(r -> {
            r.setStatus(ReservationStatus.FULFILLED);
            r.setUpdatedAt(LocalDateTime.now());
        });
        reservationRepository.saveAll(reservations);
        log.info("Marked reservations as FULFILLED for orderId: {}", orderId);
    }

    @Transactional
    public void markReleased(Long orderId) {
        List<InventoryReservation> reservations = getReservedItems(orderId);
        reservations.forEach(r -> {
            r.setStatus(ReservationStatus.RELEASED);
            r.setUpdatedAt(LocalDateTime.now());
        });
        reservationRepository.saveAll(reservations);
        log.info("Marked reservations as RELEASED for orderId: {}", orderId);
    }
}
