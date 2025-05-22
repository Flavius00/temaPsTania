package com.example.demo.entity;

import jakarta.persistence.*;
import lombok.*;
import java.util.List;

@Entity
@Table(name = "tenant")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Tenant extends User {

    @Column(name = "company_name")
    private String companyName;

    @Column(name = "business_type")
    private String businessType;

    @Column(name = "tax_id")
    private String taxId;

    @OneToMany(mappedBy = "tenant", cascade = CascadeType.ALL)
    private List<RentalContract> contracts;
}