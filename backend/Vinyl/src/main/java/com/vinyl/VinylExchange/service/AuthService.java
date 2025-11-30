package com.vinyl.VinylExchange.service;

import java.util.UUID;

import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.vinyl.VinylExchange.domain.dto.AuthResponseDTO;
import com.vinyl.VinylExchange.domain.dto.LoginRequestDTO;
import com.vinyl.VinylExchange.domain.dto.RegisterRequestDTO;
import com.vinyl.VinylExchange.domain.dto.UserResponseDTO;
import com.vinyl.VinylExchange.domain.entity.User;
import com.vinyl.VinylExchange.exception.NoCurrentUserException;
import com.vinyl.VinylExchange.exception.RegistrationValidationException;
import com.vinyl.VinylExchange.repository.UserRepository;
import com.vinyl.VinylExchange.security.util.JwtTokenUtil;

@Service
public class AuthService {

    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;
    private final JwtTokenUtil jwtTokenUtil;

    public AuthService(UserRepository userRepository, PasswordEncoder passwordEncoder, JwtTokenUtil jwtTokenUtil) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtTokenUtil = jwtTokenUtil;
    }

    public AuthResponseDTO authenticateUser(LoginRequestDTO loginRequestDTO) {
        if (loginRequestDTO == null)
            throw new BadCredentialsException("Invalid credentials");

        User user = validateLogin(loginRequestDTO);

        String token = jwtTokenUtil.generateToken(user);

        return new AuthResponseDTO(new UserResponseDTO(user), token);
    }

    public AuthResponseDTO registerUser(RegisterRequestDTO registerRequest) {

        validateRegistration(registerRequest);

        User user = new User();

        user.setUsername(registerRequest.username());
        user.setEmail(registerRequest.email());
        user.setPassword(passwordEncoder.encode(registerRequest.password()));

        userRepository.save(user);

        String token = jwtTokenUtil.generateToken(user);

        return new AuthResponseDTO(new UserResponseDTO(user), token);

    }

    public void validateRegistration(RegisterRequestDTO registerRequest) {

        if (userRepository.existsByUsername(registerRequest.username())
                || userRepository.existsByEmail(registerRequest.email())) {

            throw new RegistrationValidationException("Registration failed: invalid username or email");
        }
    }

    public User validateLogin(LoginRequestDTO loginRequestDTO) {

        User user = userRepository
                .findByUsername(loginRequestDTO.username())
                .orElseThrow(() -> new BadCredentialsException("Invalid credentials"));

        if (!passwordEncoder.matches(loginRequestDTO.password(), user.getPassword())) {
            throw new BadCredentialsException("Invalid credentials");
        }

        return user;
    }

    public UserResponseDTO getUserDTO(String token) {

        UUID userId = jwtTokenUtil.extractUserId(token);

        User user = userRepository
                .findById(userId)
                .orElseThrow(() -> new NoCurrentUserException());

        return new UserResponseDTO(user);
    }

    public User getUserFromToken(String token) {

        UUID userId = jwtTokenUtil.extractUserId(token);

        User user = userRepository
                .findById(userId)
                .orElseThrow(() -> new NoCurrentUserException());

        return user;
    }
}
