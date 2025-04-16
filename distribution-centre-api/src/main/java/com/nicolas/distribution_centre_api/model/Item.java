package com.nicolas.distribution_centre_api.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
public class Item {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private Brand brand;
    private int quantity;
    private int yearOfCreation;
    private double price;

    public Item() {}

    public Item(String name, Brand brand, int quantity, int yearOfCreation, double price) {
        this.name = name;
        this.brand = brand;
        this.quantity = quantity;
        this.yearOfCreation = yearOfCreation;
        this.price = price;
    }
}