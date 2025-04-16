package com.warehouse.clothes_warehouse.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Entity
@Table(name = "items")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Item {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Name is required")
    private String name;

    @Enumerated(EnumType.STRING)
    @NotNull(message = "Brand is required")
    private Brand brand;

    @Min(value = 1001, message = "Price must be more than 1000")
    @NotNull(message = "Price is required")
    private BigDecimal price;

    @NotNull(message = "Year of creation is required")
    @Min(value = 2022, message = "Year of creation must be after 2021")
    private Integer yearOfCreation;
}