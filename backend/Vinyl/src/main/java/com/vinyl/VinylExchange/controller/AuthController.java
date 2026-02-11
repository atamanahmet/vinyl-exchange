package com.vinyl.VinylExchange.controller;

import com.vinyl.VinylExchange.domain.entity.User;
import com.vinyl.VinylExchange.service.AuthService;
import com.vinyl.VinylExchange.session.UserUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import org.springframework.stereotype.Controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import com.vinyl.VinylExchange.dto.AuthResponse;
import com.vinyl.VinylExchange.dto.LoginRequest;
import com.vinyl.VinylExchange.dto.RegisterRequest;
import com.vinyl.VinylExchange.security.util.JwtCookieUtil;

import com.vinyl.VinylExchange.dto.UserDTO;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;

import jakarta.validation.Valid;

@Controller
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;
    private final JwtCookieUtil jwtCookieUtil;

    @PostMapping("/register")
    public ResponseEntity<HttpStatus> register(
            @Valid @RequestBody RegisterRequest registerRequest,
            HttpServletResponse response) {

        AuthResponse authResponseDTO = authService.registerUser(registerRequest, null);

        Cookie cookie = jwtCookieUtil.createJwtCookie(authResponseDTO.token());

        response.addCookie(cookie);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .build();
    }

    @PostMapping("/login")
    public ResponseEntity<UserDTO> login(
            @Valid @RequestBody LoginRequest loginRequest,
            HttpServletResponse response) {

        AuthResponse authResponseDTO = authService.authenticateUser(loginRequest);

        Cookie cookie = jwtCookieUtil.createJwtCookie(authResponseDTO.token());

        response.addCookie(cookie);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(authResponseDTO.userDTO());
    }

    @PostMapping("/logout")
    public ResponseEntity<HttpStatus> logOut(HttpServletResponse response) {

        jwtCookieUtil.revokeJwtCookie(response);

        return ResponseEntity
                .noContent()
                .build();
    }

    @GetMapping("/api/me")
    public ResponseEntity<UserDTO> getCurrentUser() {

        User currentUser = UserUtil.getCurrentUser();

        UserDTO userDTO = new UserDTO(currentUser);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(userDTO);
    }

}
