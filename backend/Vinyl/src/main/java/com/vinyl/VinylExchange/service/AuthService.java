package com.vinyl.VinylExchange.service;

import java.util.Set;
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
import com.vinyl.VinylExchange.domain.entity.Role;
import com.vinyl.VinylExchange.domain.entity.User;
import com.vinyl.VinylExchange.domain.entity.UserStatus;
import com.vinyl.VinylExchange.domain.enums.RoleName;
import com.vinyl.VinylExchange.exception.NoCurrentUserException;
import com.vinyl.VinylExchange.exception.RegistrationValidationException;
import com.vinyl.VinylExchange.repository.UserRepository;
import com.vinyl.VinylExchange.security.principal.UserPrincipal;
import com.vinyl.VinylExchange.security.util.JwtTokenUtil;

import jakarta.transaction.Transactional;

@Service
@Transactional
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;
    private final JwtTokenUtil jwtTokenUtil;
    private final RoleService roleService;

    public AuthService(
            AuthenticationManager authenticationManager,
            UserRepository userRepository,
            PasswordEncoder passwordEncoder,
            JwtTokenUtil jwtTokenUtil,
            RoleService roleService) {

        this.authenticationManager = authenticationManager;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtTokenUtil = jwtTokenUtil;
        this.roleService = roleService;
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
        user.setStatus(UserStatus.PENDING);

        Role userRole = roleService.getRoleByName(RoleName.ROLE_USER);

        user.setRoles(Set.of(userRole));

        System.out.println(user.getRoles());

        User savedUser = userRepository.save(user);

        String token = jwtTokenUtil.generateToken(savedUser);

        return new AuthResponseDTO(
                new UserDTO(savedUser.getUsername(), savedUser.getEmail()), token);
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

    public Set<Role> giveUserAdminRole(String username) {

        User user = userRepository.findByUsername(username).orElseGet(null);

        if (user != null) {
            Role adminRole = roleService.getRoleByName(RoleName.ROLE_ADMIN);
            user.getRoles().add(adminRole);
            userRepository.save(user);
        }
        return user.getRoles();

    }
}
