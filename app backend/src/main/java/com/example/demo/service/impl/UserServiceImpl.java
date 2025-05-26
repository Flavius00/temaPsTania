// app backend/src/main/java/com/example/demo/service/impl/UserServiceImpl.java
package com.example.demo.service.impl;

import com.example.demo.exception.BadRequestException;
import com.example.demo.exception.DuplicateResourceException;
import com.example.demo.exception.ResourceNotFoundException;
import com.example.demo.model.RentalContract;
import com.example.demo.model.Tenant;
import com.example.demo.model.User;
import com.example.demo.repository.RentalContractRepository;
import com.example.demo.repository.UserRepository;
import com.example.demo.service.UserService;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final RentalContractRepository rentalContractRepository;

    public UserServiceImpl(UserRepository userRepository, RentalContractRepository rentalContractRepository) {
        this.userRepository = userRepository;
        this.rentalContractRepository = rentalContractRepository;
    }

    @Override
    public User addUser(User user) {
        // Validation
        if (user == null) {
            throw new BadRequestException("User cannot be null");
        }
        if (user.getUsername() == null || user.getUsername().isEmpty()) {
            throw new BadRequestException("Username is required");
        }
        if (user.getPassword() == null || user.getPassword().isEmpty()) {
            throw new BadRequestException("Password is required");
        }
        if (user.getEmail() == null || user.getEmail().isEmpty()) {
            throw new BadRequestException("Email is required");
        }

        // Check for duplicate username
        User existingUser = findByUsername(user.getUsername());
        if (existingUser != null) {
            throw new DuplicateResourceException("Username already exists: " + user.getUsername());
        }

        // Check for duplicate email
        User userWithSameEmail = userRepository.findAll().stream()
                .filter(u -> user.getEmail().equals(u.getEmail()))
                .findFirst()
                .orElse(null);

        if (userWithSameEmail != null) {
            throw new DuplicateResourceException("Email already in use: " + user.getEmail());
        }

        return userRepository.save(user);
    }

    @Override
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    @Override
    public User getUserById(Long id) {
        if (id == null) {
            throw new BadRequestException("User ID cannot be null");
        }

        User user = userRepository.findById(id);
        if (user == null) {
            throw new ResourceNotFoundException("User not found with ID: " + id);
        }

        return user;
    }

    @Override
    public User updateUser(User user) {
        // Validation
        if (user == null) {
            throw new BadRequestException("User cannot be null");
        }
        if (user.getId() == null) {
            throw new BadRequestException("User ID is required for update");
        }

        // Check if user exists
        User existingUser = userRepository.findById(user.getId());
        if (existingUser == null) {
            throw new ResourceNotFoundException("User not found with ID: " + user.getId());
        }

        // If email is being changed, check for duplicates
        if (!existingUser.getEmail().equals(user.getEmail())) {
            User userWithSameEmail = userRepository.findAll().stream()
                    .filter(u -> user.getEmail().equals(u.getEmail()) && !u.getId().equals(user.getId()))
                    .findFirst()
                    .orElse(null);

            if (userWithSameEmail != null) {
                throw new DuplicateResourceException("Email already in use: " + user.getEmail());
            }
        }

        return userRepository.update(user);
    }

    @Override
    public void deleteUser(Long id) {
        if (id == null) {
            throw new BadRequestException("User ID cannot be null");
        }

        // Check if user exists
        User existingUser = userRepository.findById(id);
        if (existingUser == null) {
            throw new ResourceNotFoundException("User not found with ID: " + id);
        }

        userRepository.deleteById(id);
    }

    @Override
    public User findByUsername(String username) {
        if (username == null || username.isEmpty()) {
            throw new BadRequestException("Username cannot be null or empty");
        }

        return userRepository.findAll().stream()
                .filter(user -> user.getUsername().equals(username))
                .findFirst()
                .orElse(null);
    }

    @Override
    public List<RentalContract> getUserContracts(Long userId) {
        if (userId == null) {
            throw new BadRequestException("User ID cannot be null");
        }

        // Check if user exists
        User user = userRepository.findById(userId);
        if (user == null) {
            throw new ResourceNotFoundException("User not found with ID: " + userId);
        }

        // Return contracts only if user is a tenant
        if (user.getRole() == User.UserRole.TENANT) {
            return rentalContractRepository.findByUserId(userId);
        }

        return new ArrayList<>();
    }
}