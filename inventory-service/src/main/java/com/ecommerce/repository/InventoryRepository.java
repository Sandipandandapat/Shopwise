package com.ecommerce.repository;

import com.ecommerce.model.Inventory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface InventoryRepository extends JpaRepository<Inventory,Long> {
    Optional<Inventory> findByProductId(String productId);

    @Modifying
    @Query("update Inventory i set i.reserved = i.reserved + :qty " +
            "where i.productId = :productId and (i.onHand - i.reserved) >= :qty")
    int tryReserve(@Param("productId") String productId, @Param("qty") int qty);

    @Modifying
    @Query("update Inventory i set i.reserved = i.reserved - :qty " +
            "where i.productId = :productId and i.reserved >= :qty")
    int releaseReservation(@Param("productId") String productId, @Param("qty") int qty);

    @Modifying
    @Query("update Inventory i set i.onHand = i.onHand - :qty, " +
            "i.reserved = i.reserved - :qty " +
            "where i.productId = :productId and i.reserved >= :qty")
    int deductReserved(@Param("productId") String productId, @Param("qty") int qty);
}
