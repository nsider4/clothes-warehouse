package com.warehouse.clothes_warehouse.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
public class ItemDTO {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private String brand;
    private int quantity;

    public ItemDTO() {}

    public ItemDTO(String name, String brand, int quantity) {
        this.name = name;
        this.brand = brand;
        this.quantity = quantity;
    }
}
