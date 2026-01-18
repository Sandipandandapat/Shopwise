package com.shopwise.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table(
        name = "inventory_reservation",
        uniqueConstraints = @UniqueConstraint(
                name = "uc_reservation_order_product",
                columnNames = {"orderId","productId"}
        )
)
public class InventoryReservation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long orderId;
    private String productId;
    private int quantity;

    private String status;
    private LocalDateTime createdAt;
}
