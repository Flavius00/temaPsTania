package com.example.demo.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

/**
 * Entitatea Tenant - extinde User pentru chiriașii de spații comerciale.
 *
 * Conține informații specifice chiriașilor:
 * - Numele companiei
 * - Tipul de business
 * - CUI-ul fiscal
 * - Lista contractelor de închiriere
 *
 * Principii SOLID respectate:
 * - Single Responsibility: doar datele specifice chiriașului
 * - Liskov Substitution: poate fi folosită în locul clasei User
 */
@Entity
@Table(name = "tenants")
@DiscriminatorValue("TENANT")
@PrimaryKeyJoinColumn(name = "user_id")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString(callSuper = true, exclude = {"contracts"}) // Excludem lista pentru a evita referințele circulare
@EqualsAndHashCode(callSuper = true)
public class Tenant extends User {

    @Size(max = 100, message = "Numele companiei nu poate avea mai mult de 100 de caractere")
    @Column(name = "company_name", length = 100)
    private String companyName;

    @Size(max = 100, message = "Tipul de business nu poate avea mai mult de 100 de caractere")
    @Column(name = "business_type", length = 100)
    private String businessType;

    @Size(max = 20, message = "CUI-ul nu poate avea mai mult de 20 de caractere")
    @Column(name = "tax_id", length = 20)
    private String taxId;

    @Column(name = "registration_number", length = 50)
    private String registrationNumber;

    @Column(name = "bank_account", length = 50)
    private String bankAccount;

    @Column(name = "bank_name", length = 100)
    private String bankName;

    @Column(name = "preferred_space_type")
    @Enumerated(EnumType.STRING)
    private SpaceType preferredSpaceType;

    @Column(name = "max_budget")
    private Double maxBudget;

    @Column(name = "min_area")
    private Double minArea;

    @Column(name = "max_area")
    private Double maxArea;

    /**
     * Relația One-to-Many cu contractele de închiriere.
     *
     * Un chiriaș poate avea multiple contracte de închiriere.
     * Folosim FetchType.LAZY pentru performanță optimă.
     */
    @OneToMany(
            mappedBy = "tenant",
            cascade = CascadeType.ALL,
            fetch = FetchType.LAZY,
            orphanRemoval = true
    )
    @Builder.Default
    private List<RentalContract> contracts = new ArrayList<>();

    /**
     * Constructor pentru Tenant cu rolul setat automat.
     */
    @Builder
    public Tenant(String name, String email, String username, String password,
                  String phone, String address, String profilePictureUrl,
                  String companyName, String businessType, String taxId,
                  String registrationNumber, String bankAccount, String bankName,
                  SpaceType preferredSpaceType, Double maxBudget,
                  Double minArea, Double maxArea) {
        super();
        setName(name);
        setEmail(email);
        setUsername(username);
        setPassword(password);
        setPhone(phone);
        setAddress(address);
        setProfilePictureUrl(profilePictureUrl);
        setRole(UserRole.TENANT);
        this.companyName = companyName;
        this.businessType = businessType;
        this.taxId = taxId;
        this.registrationNumber = registrationNumber;
        this.bankAccount = bankAccount;
        this.bankName = bankName;
        this.preferredSpaceType = preferredSpaceType;
        this.maxBudget = maxBudget;
        this.minArea = minArea;
        this.maxArea = maxArea;
        this.contracts = new ArrayList<>();
    }

    /**
     * Adaugă un contract de închiriere la lista chiriașului.
     *
     * @param contract contractul de adăugat
     */
    public void addContract(RentalContract contract) {
        if (contracts == null) {
            contracts = new ArrayList<>();
        }
        contracts.add(contract);
        contract.setTenant(this);
    }

    /**
     * Elimină un contract de închiriere din lista chiriașului.
     *
     * @param contract contractul de eliminat
     */
    public void removeContract(RentalContract contract) {
        if (contracts != null) {
            contracts.remove(contract);
            contract.setTenant(null);
        }
    }

    /**
     * Returnează numărul de contracte active ale chiriașului.
     *
     * @return numărul de contracte active
     */
    public long getActiveContractsCount() {
        return contracts != null ? contracts.stream()
                .filter(contract -> "ACTIVE".equals(contract.getStatus()))
                .count() : 0;
    }

    /**
     * Verifică dacă chiriașul are contracte active.
     *
     * @return true dacă există contracte active
     */
    public boolean hasActiveContracts() {
        return getActiveContractsCount() > 0;
    }

    /**
     * Returnează contractul activ pentru un spațiu specific.
     *
     * @param spaceId ID-ul spațiului
     * @return contractul activ sau null dacă nu există
     */
    public RentalContract getActiveContractForSpace(Long spaceId) {
        return contracts != null ? contracts.stream()
                .filter(contract -> "ACTIVE".equals(contract.getStatus()) &&
                        contract.getSpace() != null &&
                        spaceId.equals(contract.getSpace().getId()))
                .findFirst()
                .orElse(null) : null;
    }
}