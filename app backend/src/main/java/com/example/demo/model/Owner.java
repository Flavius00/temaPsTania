package com.example.demo.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

/**
 * Entitatea Owner - extinde User pentru proprietarii de spații comerciale.
 *
 * Conține informații specifice proprietarilor:
 * - Numele companiei
 * - CUI-ul fiscal
 * - Lista spațiilor comerciale deținute
 *
 * Principii SOLID respectate:
 * - Single Responsibility: doar datele specifice proprietarului
 * - Liskov Substitution: poate fi folosită în locul clasei User
 */
@Entity
@Table(name = "owners")
@DiscriminatorValue("OWNER")
@PrimaryKeyJoinColumn(name = "user_id")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString(callSuper = true, exclude = {"spaces"}) // Excludem lista pentru a evita referințele circulare
@EqualsAndHashCode(callSuper = true)
public class Owner extends User {

    @Size(max = 100, message = "Numele companiei nu poate avea mai mult de 100 de caractere")
    @Column(name = "company_name", length = 100)
    private String companyName;

    @Size(max = 20, message = "CUI-ul nu poate avea mai mult de 20 de caractere")
    @Column(name = "tax_id", length = 20)
    private String taxId;

    @Size(max = 50, message = "Tipul companiei nu poate avea mai mult de 50 de caractere")
    @Column(name = "company_type", length = 50)
    private String companyType;

    @Column(name = "registration_number", length = 50)
    private String registrationNumber;

    @Column(name = "bank_account", length = 50)
    private String bankAccount;

    @Column(name = "bank_name", length = 100)
    private String bankName;

    /**
     * Relația One-to-Many cu spațiile comerciale.
     *
     * Un proprietar poate avea multiple spații comerciale.
     * Folosim FetchType.LAZY pentru performanță optimă.
     * CascadeType.ALL pentru a gestiona automat operațiunile asupra spațiilor.
     */
    @OneToMany(
            mappedBy = "owner",
            cascade = CascadeType.ALL,
            fetch = FetchType.LAZY,
            orphanRemoval = true
    )
    @Builder.Default
    private List<ComercialSpace> spaces = new ArrayList<>();

    /**
     * Constructor pentru Owner cu rolul setat automat.
     *
     * @param name numele proprietarului
     * @param email email-ul proprietarului
     * @param username username-ul proprietarului
     * @param password parola proprietarului
     * @param companyName numele companiei
     * @param taxId CUI-ul fiscal
     */
    @Builder
    public Owner(String name, String email, String username, String password,
                 String phone, String address, String profilePictureUrl,
                 String companyName, String taxId, String companyType,
                 String registrationNumber, String bankAccount, String bankName) {
        super();
        setName(name);
        setEmail(email);
        setUsername(username);
        setPassword(password);
        setPhone(phone);
        setAddress(address);
        setProfilePictureUrl(profilePictureUrl);
        setRole(UserRole.OWNER);
        this.companyName = companyName;
        this.taxId = taxId;
        this.companyType = companyType;
        this.registrationNumber = registrationNumber;
        this.bankAccount = bankAccount;
        this.bankName = bankName;
        this.spaces = new ArrayList<>();
    }

    /**
     * Adaugă un spațiu comercial la lista proprietarului.
     *
     * @param space spațiul comercial de adăugat
     */
    public void addSpace(ComercialSpace space) {
        if (spaces == null) {
            spaces = new ArrayList<>();
        }
        spaces.add(space);
        space.setOwner(this);
    }

    /**
     * Elimină un spațiu comercial din lista proprietarului.
     *
     * @param space spațiul comercial de eliminat
     */
    public void removeSpace(ComercialSpace space) {
        if (spaces != null) {
            spaces.remove(space);
            space.setOwner(null);
        }
    }

    /**
     * Returnează numărul de spații comerciale ale proprietarului.
     *
     * @return numărul de spații
     */
    public int getSpacesCount() {
        return spaces != null ? spaces.size() : 0;
    }

    /**
     * Verifică dacă proprietarul are spații comerciale disponibile.
     *
     * @return true dacă există spații disponibile
     */
    public boolean hasAvailableSpaces() {
        return spaces != null && spaces.stream()
                .anyMatch(space -> space.getAvailable() != null && space.getAvailable());
    }
}