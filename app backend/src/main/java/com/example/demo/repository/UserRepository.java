package com.example.demo.repository;

import com.example.demo.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Repository pentru entitatea User.
 *
 * Extinde JpaRepository pentru operațiunile CRUD de bază și adaugă
 * metode de căutare specifice pentru utilizatori.
 *
 * Principii SOLID respectate:
 * - Interface Segregation: interfață specifică pentru operațiunile cu User
 * - Dependency Inversion: depinde de abstracțiuni, nu de implementări concrete
 */
@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    /**
     * Caută un utilizator după username.
     *
     * @param username username-ul căutat
     * @return utilizatorul găsit sau Optional.empty()
     */
    Optional<User> findByUsername(String username);

    /**
     * Caută un utilizator după email.
     *
     * @param email email-ul căutat
     * @return utilizatorul găsit sau Optional.empty()
     */
    Optional<User> findByEmail(String email);

    /**
     * Verifică dacă există un utilizator cu username-ul specificat.
     *
     * @param username username-ul de verificat
     * @return true dacă există
     */
    boolean existsByUsername(String username);

    /**
     * Verifică dacă există un utilizator cu email-ul specificat.
     *
     * @param email email-ul de verificat
     * @return true dacă există
     */
    boolean existsByEmail(String email);

    /**
     * Caută utilizatori după rol.
     *
     * @param role rolul căutat
     * @return lista utilizatorilor cu rolul specificat
     */
    List<User> findByRole(User.UserRole role);

    /**
     * Caută utilizatori activi.
     *
     * @param active statusul de activitate
     * @return lista utilizatorilor activi/inactivi
     */
    List<User> findByActive(Boolean active);

    /**
     * Caută utilizatori după nume (case insensitive).
     *
     * @param name numele căutat
     * @return lista utilizatorilor cu numele specificat
     */
    List<User> findByNameContainingIgnoreCase(String name);

    /**
     * Caută utilizatori creați după o anumită dată.
     *
     * @param date data de referință
     * @return lista utilizatorilor creați după data specificată
     */
    List<User> findByCreatedAtAfter(LocalDateTime date);

    /**
     * Caută utilizatori după rol și status de activitate.
     *
     * @param role rolul căutat
     * @param active statusul de activitate
     * @return lista utilizatorilor care îndeplinesc criteriile
     */
    List<User> findByRoleAndActive(User.UserRole role, Boolean active);

    /**
     * Caută utilizatori cu email-uri care conțin un anumit text.
     *
     * @param emailPart partea din email căutată
     * @return lista utilizatorilor cu email-uri potrivite
     */
    List<User> findByEmailContainingIgnoreCase(String emailPart);

    /**
     * Numără utilizatorii după rol.
     *
     * @param role rolul pentru care se numără
     * @return numărul de utilizatori cu rolul specificat
     */
    long countByRole(User.UserRole role);

    /**
     * Numără utilizatorii activi.
     *
     * @param active statusul de activitate
     * @return numărul de utilizatori activi/inactivi
     */
    long countByActive(Boolean active);

    /**
     * Query personalizată pentru căutarea utilizatorilor după criterii multiple.
     *
     * @param name numele de căutat (poate fi null)
     * @param email email-ul de căutat (poate fi null)
     * @param role rolul de căutat (poate fi null)
     * @return lista utilizatorilor care îndeplinesc criteriile
     */
    @Query("SELECT u FROM User u WHERE " +
            "(:name IS NULL OR LOWER(u.name) LIKE LOWER(CONCAT('%', :name, '%'))) AND " +
            "(:email IS NULL OR LOWER(u.email) LIKE LOWER(CONCAT('%', :email, '%'))) AND " +
            "(:role IS NULL OR u.role = :role) AND " +
            "u.active = true " +
            "ORDER BY u.createdAt DESC")
    List<User> findUsersByCriteria(@Param("name") String name,
                                   @Param("email") String email,
                                   @Param("role") User.UserRole role);

    /**
     * Query pentru obținerea utilizatorilor cu cele mai multe login-uri recente.
     *
     * @param limit numărul maxim de rezultate
     * @return lista utilizatorilor activi recent
     */
    @Query("SELECT u FROM User u WHERE u.active = true ORDER BY u.updatedAt DESC")
    List<User> findMostRecentlyActiveUsers(@Param("limit") int limit);

    /**
     * Query pentru căutarea utilizatorilor fără telefon.
     *
     * @return lista utilizatorilor fără număr de telefon
     */
    @Query("SELECT u FROM User u WHERE u.phone IS NULL OR u.phone = ''")
    List<User> findUsersWithoutPhone();

    /**
     * Query pentru căutarea utilizatorilor după orașele din adresă.
     *
     * @param city orașul căutat
     * @return lista utilizatorilor din orașul specificat
     */
    @Query("SELECT u FROM User u WHERE LOWER(u.address) LIKE LOWER(CONCAT('%', :city, '%'))")
    List<User> findUsersByCity(@Param("city") String city);

    /**
     * Șterge utilizatorii inactivi creați înainte de o anumită dată.
     *
     * @param date data de referință
     * @return numărul de utilizatori șterși
     */
    @Query("DELETE FROM User u WHERE u.active = false AND u.createdAt < :date")
    int deleteInactiveUsersBefore(@Param("date") LocalDateTime date);
}