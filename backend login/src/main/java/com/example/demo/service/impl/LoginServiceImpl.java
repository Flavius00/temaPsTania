package com.example.demo.service.impl;

import com.example.demo.model.UserLogIn;
import com.example.demo.repository.UserLogInRepository;
import com.example.demo.service.LoginService;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class LoginServiceImpl implements LoginService {
    private final UserLogInRepository userRepository;

    public LoginServiceImpl(UserLogInRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserLogIn login(String username, String password) {
        System.out.println("Login attempt - Username: " + username + ", Password: " + password);

        Optional<UserLogIn> optionalUser = userRepository.findByUsername(username);

        if (optionalUser.isPresent()) {
            UserLogIn user = optionalUser.get();
            System.out.println("User found, checking password...");

            if (user.getPassword().equals(password)) {
                System.out.println("Password match, login successful");
                return user;
            } else {
                System.out.println("Password mismatch");
            }
        } else {
            System.out.println("User not found with username: " + username);
        }

        return null;
    }
}
