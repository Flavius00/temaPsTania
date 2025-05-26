package com.example.demo.mapper;

import com.example.demo.dto.UserDTO;
import com.example.demo.entity.User;
import org.mapstruct.*;

import java.util.List;

/**
 * Mapper pentru convertirea între entitatea User și DTO-urile corespunzătoare.
 *
 * Folosește MapStruct pentru generarea automată a codului de mapping.
 *
 * Principii SOLID respectate:
 * - Single Responsibility: doar convertirea între User și UserDTO
 * - Open/Closed: poate fi extins cu noi metode fără modificarea celor existente
 * - Dependency Inversion: depinde de abstracțiuni (interfețe)
 */
@Mapper(
        componentModel = "spring",
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
        unmappedTargetPolicy = ReportingPolicy.IGNORE
)
public interface UserMapper {

    /**
     * Convertește o entitate User într-un UserDTO.
     *
     * @param user entitatea User
     * @return UserDTO corespunzător
     */
    @Mapping(target = "password", ignore = true) // Nu mapăm parola pentru securitate
    UserDTO toDTO(User user);

    /**
     * Convertește un UserDTO într-o entitate User.
     *
     * @param userDTO DTO-ul de convertit
     * @return entitatea User corespunzătoare
     */
    @Mapping(target = "id", ignore = true) // ID-ul se generează automat
    @Mapping(target = "createdAt", ignore = true) // Timestamp-urile se generează automat
    @Mapping(target = "updatedAt", ignore = true)
    User toEntity(UserDTO userDTO);

    /**
     * Convertește o listă de entități User într-o listă de UserDTO.
     *
     * @param users lista de entități
     * @return lista de DTO-uri
     */
    List<UserDTO> toDTOList(List<User> users);

    /**
     * Convertește o listă de UserDTO într-o listă de entități User.
     *
     * @param userDTOs lista de DTO-uri
     * @return lista de entități
     */
    List<User> toEntityList(List<UserDTO> userDTOs);

    /**
     * Convertește o entitate User într-un AuthDTO (pentru autentificare).
     *
     * @param user entitatea User
     * @return AuthDTO corespunzător
     */
    UserDTO.AuthDTO toAuthDTO(User user);

    /**
     * Convertește o entitate User într-un ListDTO (pentru listări).
     *
     * @param user entitatea User
     * @return ListDTO corespunzător
     */
    UserDTO.ListDTO toListDTO(User user);

    /**
     * Convertește o listă de entități User într-o listă de ListDTO.
     *
     * @param users lista de entități
     * @return lista de ListDTO-uri
     */
    List<UserDTO.ListDTO> toListDTOList(List<User> users);

    /**
     * Convertește un CreateDTO într-o entitate User.
     *
     * @param createDTO DTO-ul pentru creare
     * @return entitatea User corespunzătoare
     */
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "active", constant = "true") // Utilizatorii noi sunt activi implicit
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    User fromCreateDTO(UserDTO.CreateDTO createDTO);

    /**
     * Actualizează o entitate User din UpdateDTO.
     *
     * @param updateDTO DTO-ul cu datele de actualizare
     * @param user entitatea de actualizat
     */
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "username", ignore = true) // Username-ul nu se poate schimba
    @Mapping(target = "email", ignore = true) // Email-ul nu se poate schimba prin update simplu
    @Mapping(target = "role", ignore = true) // Rolul nu se poate schimba prin update simplu
    @Mapping(target = "password", ignore = true) // Parola se schimbă separat
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    void updateEntityFromDTO(UserDTO.UpdateDTO updateDTO, @MappingTarget User user);

    /**
     * Mapare personalizată pentru calcularea inițialelor.
     *
     * @param name numele utilizatorului
     * @param username username-ul utilizatorului
     * @return inițialele
     */
    @Named("calculateInitials")
    default String calculateInitials(String name, String username) {
        if (name != null && !name.trim().isEmpty()) {
            String[] nameParts = name.trim().split("\\s+");
            if (nameParts.length == 1) {
                return nameParts[0].substring(0, Math.min(2, nameParts[0].length())).toUpperCase();
            } else {
                return (nameParts[0].charAt(0) + "" + nameParts[nameParts.length - 1].charAt(0)).toUpperCase();
            }
        } else if (username != null && !username.isEmpty()) {
            return username.substring(0, Math.min(2, username.length())).toUpperCase();
        }
        return "??";
    }

    /**
     * Mapare personalizată pentru verificarea informațiilor de contact complete.
     *
     * @param email email-ul utilizatorului
     * @param phone telefonul utilizatorului
     * @return true dacă informațiile sunt complete
     */
    @Named("hasCompleteContactInfo")
    default boolean hasCompleteContactInfo(String email, String phone) {
        return email != null && !email.trim().isEmpty() &&
                phone != null && !phone.trim().isEmpty();
    }

    /**
     * Mapare după actualizare pentru a seta timestamp-ul updatedAt.
     *
     * @param user utilizatorul actualizat
     */
    @AfterMapping
    default void setUpdatedTimestamp(@MappingTarget User user) {
        user.setUpdatedAt(java.time.LocalDateTime.now());
    }

    /**
     * Mapare înainte de creare pentru validări suplimentare.
     *
     * @param createDTO DTO-ul de creare
     */
    @BeforeMapping
    default void validateCreateDTO(UserDTO.CreateDTO createDTO) {
        if (createDTO.getUsername() == null || createDTO.getUsername().trim().isEmpty()) {
            throw new IllegalArgumentException("Username-ul este obligatoriu");
        }
        if (createDTO.getEmail() == null || createDTO.getEmail().trim().isEmpty()) {
            throw new IllegalArgumentException("Email-ul este obligatoriu");
        }
        if (createDTO.getPassword() == null || createDTO.getPassword().length() < 6) {
            throw new IllegalArgumentException("Parola trebuie să aibă cel puțin 6 caractere");
        }
    }
}