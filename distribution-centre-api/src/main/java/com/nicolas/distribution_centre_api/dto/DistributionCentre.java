package com.nicolas.distribution_centre_api.dto;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Entity
public class DistributionCentre {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private double latitude;
    private double longitude;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Item> items;

    public DistributionCentre() {}

    public DistributionCentre(String name, double latitude, double longitude) {
        this.name = name;
        this.latitude = latitude;
        this.longitude = longitude;
    }
}
