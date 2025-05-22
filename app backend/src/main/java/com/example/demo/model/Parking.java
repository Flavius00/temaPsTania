package com.example.demo.entity;

import jakarta.persistence.*;
import lombok.*;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "parking")
public class Parking {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "number_of_spots")
    private Integer numberOfSpots;

    @Column(name = "price_per_spot")
    private Double pricePerSpot;

    private Boolean covered;

    @Column(name = "parking_type")
    private String parkingType;

    @OneToOne(mappedBy = "parking")
    private ComercialSpace space;
}