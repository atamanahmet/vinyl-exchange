package com.vinyl.VinylExchange.service;

import java.util.HashMap;
import java.util.Map;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.vinyl.VinylExchange.domain.dto.RegisterRequestDTO;
import com.vinyl.VinylExchange.domain.dto.UserResponseDTO;
import com.vinyl.VinylExchange.domain.entity.User;
import com.vinyl.VinylExchange.exception.RegistrationValidationException;
import com.vinyl.VinylExchange.repository.UserRepository;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {

        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public User registerUser(RegisterRequestDTO registerRequest) {

        validateRegistration(registerRequest);

        User user = new User();

        user.setUsername(registerRequest.username());
        user.setEmail(registerRequest.email());
        user.setPassword(passwordEncoder.encode(registerRequest.password()));
        return userRepository.save(user);

    }

    public void validateRegistration(RegisterRequestDTO registerRequest) {

        Map<String, String> errors = new HashMap<>();

        if (userRepository.existsByUsername(registerRequest.username())) {

            errors.put("username", "Username already exist");
        }

        if (userRepository.existsByEmail(registerRequest.email())) {

            errors.put("email", "Email already registered");
        }

        if (!errors.isEmpty()) {
            throw new RegistrationValidationException(errors);
        }
    }

}
