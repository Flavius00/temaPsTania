package com.example.demo.dto;

import com.example.demo.entity.User;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

import java.time.LocalDateTime;

/**
 * DTO pentru entitatea User.
 *
 * Acest DTO este folosit pentru transferul datelor despre utilizatori
 * între diferitele layere ale aplicației și către/de la client.
 *
 * Principii SOLID respectate:
 * - Single Responsibility: doar transferul datelor despre utilizatori
 * - Open/Closed: poate fi extins fără modificarea codului existent
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class UserDTO {

    private Long id;

    private String name;

    private String email;

    private String username;

    // Nota: Nu includem parola în DTO pentru securitate

    private String phone;

    private String address;

    private String profilePictureUrl;

    private User.UserRole role;

    private Boolean active;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    /**
     * Constructor pentru crearea unui UserDTO cu datele de bază.
     *
     * @param name numele utilizatorului
     * @param email email-ul utilizatorului
     * @param username username-ul utilizatorului
     * @param role rolul utilizatorului
     */
    public UserDTO(String name, String email, String username, User.UserRole role) {
        this.name = name;
        this.email = email;
        this.username = username;
        this.role = role;
        this.active = true;
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
     * Returnează numele afișat pentru rol.
     *
     * @return numele afișat pentru rol
     */
    public String getRoleDisplayName() {
        return role != null ? role.getDisplayName() : "Necunoscut";
    }

    /**
     * Verifică dacă utilizatorul are informații de contact complete.
     *
     * @return true dacă are email și telefon
     */
    public boolean hasCompleteContactInfo() {
        return email != null && !email.trim().isEmpty() &&
                phone != null && !phone.trim().isEmpty();
    }

    /**
     * Returnează inițialele utilizatorului pentru avatar.
     *
     * @return inițialele utilizatorului
     */
    public String getInitials() {
        if (name == null || name.trim().isEmpty()) {
            return username != null && !username.isEmpty() ?
                    username.substring(0, Math.min(2, username.length())).toUpperCase() : "??";
        }

        String[] nameParts = name.trim().split("\\s+");
        if (nameParts.length == 1) {
            return nameParts[0].substring(0, Math.min(2, nameParts[0].length())).toUpperCase();
        } else {
            return (nameParts[0].charAt(0) + "" + nameParts[nameParts.length - 1].charAt(0)).toUpperCase();
        }
    }

    /**
     * Verifică dacă utilizatorul are o imagine de profil.
     *
     * @return true dacă are imagine de profil
     */
    public boolean hasProfilePicture() {
        return profilePictureUrl != null && !profilePictureUrl.trim().isEmpty();
    }

    /**
     * DTO pentru operațiuni de autentificare (fără informații sensibile).
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class AuthDTO {
        private Long id;
        private String username;
        private String name;
        private User.UserRole role;
        private Boolean active;
    }

    /**
     * DTO pentru listarea utilizatorilor (informații minime).
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class ListDTO {
        private Long id;
        private String name;
        private String email;
        private User.UserRole role;
        private Boolean active;
        private LocalDateTime createdAt;
    }

    /**
     * DTO pentru crearea utilizatorilor (include parola).
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CreateDTO {
        private String name;
        private String email;
        private String username;
        private String password;
        private String phone;
        private String address;
        private User.UserRole role;
    }

    /**
     * DTO pentru actualizarea utilizatorilor (fără parolă).
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class UpdateDTO {
        private String name;
        private String email;
        private String phone;
        private String address;
        private String profilePictureUrl;
        private Boolean active;
    }
}