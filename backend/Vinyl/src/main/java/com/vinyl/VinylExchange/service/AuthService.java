package com.vinyl.VinylExchange.service;

import java.util.UUID;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;

import org.springframework.stereotype.Service;

import com.vinyl.VinylExchange.domain.dto.AuthResponseDTO;
import com.vinyl.VinylExchange.domain.dto.LoginRequestDTO;
import com.vinyl.VinylExchange.domain.dto.RegisterRequestDTO;
import com.vinyl.VinylExchange.domain.dto.UserDTO;
import com.vinyl.VinylExchange.domain.entity.User;

import com.vinyl.VinylExchange.exception.NoCurrentUserException;
import com.vinyl.VinylExchange.exception.RegistrationValidationException;

import com.vinyl.VinylExchange.repository.UserRepository;
import com.vinyl.VinylExchange.security.enums.Role;
import com.vinyl.VinylExchange.security.principal.UserPrincipal;
import com.vinyl.VinylExchange.security.util.JwtTokenUtil;

@Service
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;
    private final JwtTokenUtil jwtTokenUtil;

    public AuthService(
            AuthenticationManager authenticationManager,
            UserRepository userRepository,
            PasswordEncoder passwordEncoder,
            JwtTokenUtil jwtTokenUtil) {

        this.authenticationManager = authenticationManager;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtTokenUtil = jwtTokenUtil;
    }

    public AuthResponseDTO authenticateUser(LoginRequestDTO loginRequestDTO) {
        if (loginRequestDTO == null)
            throw new BadCredentialsException("Invalid credentials");

        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequestDTO.username(), loginRequestDTO.password()));

        UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();

        String token = jwtTokenUtil.generateToken(
                userPrincipal.getId(),
                userPrincipal.getUsername(),
                userPrincipal.getRoles());

        return new AuthResponseDTO(
                new UserDTO(userPrincipal.getUsername(), userPrincipal.getEmail()), token);
    }

    public AuthResponseDTO registerUser(RegisterRequestDTO registerRequest) {

        validateRegistration(registerRequest);

        User user = new User();

        user.setUsername(registerRequest.username());
        user.setEmail(registerRequest.email());
        user.setPassword(passwordEncoder.encode(registerRequest.password()));
        user.getRoles().add(Role.USER);

        try {
            User savedUser = userRepository.save(user);

            String token = jwtTokenUtil.generateToken(savedUser);

            return new AuthResponseDTO(
                    new UserDTO(savedUser.getUsername(), savedUser.getEmail()), token);

        } catch (Exception e) {
            System.out.println(e.getMessage());
            return null;
        }

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

    public UserDTO getUserDTO(String token) {

        UUID userId = jwtTokenUtil.extractUserId(token);

        User user = userRepository
                .findById(userId)
                .orElseThrow(() -> new NoCurrentUserException());

        return new UserDTO(user);
    }

    public User getUserFromToken(String token) {

        UUID userId = jwtTokenUtil.extractUserId(token);

        User user = userRepository
                .findById(userId)
                .orElseThrow(() -> new NoCurrentUserException());

        return user;
    }
}
