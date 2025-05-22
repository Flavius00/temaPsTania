package com.example.demo.entity;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.format.annotation.DateTimeFormat;
import java.time.LocalDate;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "rental_contract")
public class RentalContract {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "space_id")
    private ComercialSpace space;

    @ManyToOne
    @JoinColumn(name = "tenant_id")
    private Tenant tenant;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @Column(name = "start_date")
    private LocalDate startDate;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @Column(name = "end_date")
    private LocalDate endDate;

    @Column(name = "monthly_rent")
    private Double monthlyRent;

    @Column(name = "security_deposit")
    private Double securityDeposit;

    @Column(name = "status")
    private String status;

    @Column(name = "is_paid")
    private Boolean isPaid;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @Column(name = "date_created")
    private LocalDate dateCreated;

    @Column(name = "contract_number")
    private String contractNumber;

    @Column(columnDefinition = "TEXT")
    private String notes;

    @Column(name = "payment_method")
    private String paymentMethod;

    private String signature;
}