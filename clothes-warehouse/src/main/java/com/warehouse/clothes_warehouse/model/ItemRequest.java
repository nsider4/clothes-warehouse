package com.warehouse.clothes_warehouse.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import lombok.*;

@Entity
@Table(name = "item_requests")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ItemRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "item_id", nullable = false)
    private Long itemId;

    @Column(nullable = false)
    private int quantity;

    @Column(name = "source_centre_id", nullable = false)
    @Min(value = 1, message = "Quantity must be greater than 0.")
    private Long sourceCentreId;

    @Column(name = "destination_centre_id", nullable = false)
    private Long destinationCentreId;

    @Column(nullable = false)
    private String status = "Pending";
}
