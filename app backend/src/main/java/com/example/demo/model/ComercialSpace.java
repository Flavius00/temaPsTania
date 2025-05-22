package com.example.demo.entity;

import jakarta.persistence.*;
import lombok.*;
import java.util.List;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "comercial_space")
public class ComercialSpace {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(columnDefinition = "TEXT")
    private String description;

    private Double area;

    @Column(name = "price_per_month")
    private Double pricePerMonth;

    private String address;

    private Double latitude;

    private Double longitude;

    @ElementCollection
    @CollectionTable(name = "space_amenities", joinColumns = @JoinColumn(name = "space_id"))
    @Column(name = "amenity")
    private List<String> amenities;

    private Boolean available;

    @ManyToOne
    @JoinColumn(name = "owner_id")
    private Owner owner;

    @ManyToOne
    @JoinColumn(name = "building_id")
    private Building building;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "parking_id")
    private Parking parking;

    @Column(name = "space_type")
    @Enumerated(EnumType.STRING)
    private SpaceType spaceType;

    private Integer floors;

    @Column(name = "number_of_rooms")
    private Integer numberOfRooms;

    @Column(name = "has_reception")
    private Boolean hasReception;

    @Column(name = "shop_window_size")
    private Double shopWindowSize;

    @Column(name = "has_customer_entrance")
    private Boolean hasCustomerEntrance;

    @Column(name = "max_occupancy")
    private Integer maxOccupancy;

    @Column(name = "ceiling_height")
    private Double ceilingHeight;

    @Column(name = "has_loading_dock")
    private Boolean hasLoadingDock;

    @Column(name = "security_level")
    private String securityLevel;

    @OneToMany(mappedBy = "space", cascade = CascadeType.ALL)
    private List<RentalContract> contracts;
}