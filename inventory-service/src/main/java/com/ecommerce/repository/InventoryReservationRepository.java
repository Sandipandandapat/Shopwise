package com.ecommerce.repository;

import com.ecommerce.model.InventoryReservation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface InventoryReservationRepository extends JpaRepository<InventoryReservation, Long> {
    Optional<InventoryReservation> findByOrderIdAndProductId(Long orderId, String productId);
    List<InventoryReservation> findAllByOrderId(Long orderId);
}
