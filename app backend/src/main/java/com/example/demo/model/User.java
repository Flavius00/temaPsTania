package com.example.demo.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

/**
 * Entitatea User - clasa de bază pentru toți utilizatorii sistemului.
 *
 * Folosește strategia JOINED pentru moștenire, permițând specializarea
 * în Owner, Tenant și Admin cu tabele separate dar legate.
 *
 * Principii SOLID respectate:
 * - Single Responsibility: doar datele de bază ale utilizatorului
 * - Open/Closed: poate fi extinsă prin moștenire fără modificare
 * - Liskov Substitution: subclasele pot fi folosite în locul acestei clase
 */
@Entity
@Table(name = "users")
@Inheritance(strategy = InheritanceType.JOINED)
@DiscriminatorColumn(name = "user_type", discriminatorType = DiscriminatorType.STRING)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString(exclude = {"password"}) // Excludem parola din toString pentru securitate
@EqualsAndHashCode(of = {"id", "username"}) // Folosim doar câmpurile unice pentru equals/hashCode
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @NotBlank(message = "Numele este obligatoriu")
    @Size(min = 2, max = 100, message = "Numele trebuie să aibă între 2 și 100 de caractere")
    @Column(name = "name", nullable = false, length = 100)
    private String name;

    @NotBlank(message = "Email-ul este obligatoriu")
    @Email(message = "Email-ul trebuie să fie valid")
    @Column(name = "email", nullable = false, unique = true, length = 150)
    private String email;

    @NotBlank(message = "Username-ul este obligatoriu")
    @Size(min = 3, max = 50, message = "Username-ul trebuie să aibă între 3 și 50 de caractere")
    @Column(name = "username", nullable = false, unique = true, length = 50)
    private String username;

    @NotBlank(message = "Parola este obligatorie")
    @Size(min = 6, message = "Parola trebuie să aibă cel puțin 6 caractere")
    @Column(name = "password", nullable = false)
    private String password;

    @Size(max = 20, message = "Numărul de telefon nu poate avea mai mult de 20 de caractere")
    @Column(name = "phone", length = 20)
    private String phone;

    @Size(max = 255, message = "Adresa nu poate avea mai mult de 255 de caractere")
    @Column(name = "address")
    private String address;

    @Column(name = "profile_picture_url")
    private String profilePictureUrl;

    @Enumerated(EnumType.STRING)
    @Column(name = "role", nullable = false)
    private UserRole role;

    @Column(name = "active")
    @Builder.Default
    private Boolean active = true;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    /**
     * Enum pentru rolurile utilizatorilor în sistem.
     *
     * Respectă principiul Open/Closed - noi roluri pot fi adăugate
     * fără modificarea codului existent.
     */
    public enum UserRole {
        ADMIN("Administrator"),
        OWNER("Proprietar"),
        TENANT("Chiriaș");

        private final String displayName;

        UserRole(String displayName) {
            this.displayName = displayName;
        }

        public String getDisplayName() {
            return displayName;
        }
    }

    /**
     * Verifică dacă utilizatorul este activ.
     *
     * @return true dacă utilizatorul este activ
     */
    public boolean isActive() {
        return active != null && active;
    }

    /**
     * Activează utilizatorul.
     */
    public void activate() {
        this.active = true;
    }

    /**
     * Dezactivează utilizatorul.
     */
    public void deactivate() {
        this.active = false;
    }
}