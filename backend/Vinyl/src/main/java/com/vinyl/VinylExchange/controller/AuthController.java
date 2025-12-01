package com.vinyl.VinylExchange.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import com.vinyl.VinylExchange.domain.dto.AuthResponseDTO;
import com.vinyl.VinylExchange.domain.dto.LoginRequestDTO;
import com.vinyl.VinylExchange.domain.dto.RegisterRequestDTO;
import com.vinyl.VinylExchange.domain.dto.UserResponseDTO;
import com.vinyl.VinylExchange.exception.NoCurrentUserException;
import com.vinyl.VinylExchange.security.util.JwtCookieUtil;
import com.vinyl.VinylExchange.service.AuthService;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;

@Controller
public class AuthController {

    private final AuthService authService;
    private final JwtCookieUtil jwtCookieUtil;

    public AuthController(AuthService authService, JwtCookieUtil jwtCookieUtil) {

        this.authService = authService;
        this.jwtCookieUtil = jwtCookieUtil;
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@Valid @RequestBody RegisterRequestDTO registerRequest,
            HttpServletResponse response) {

        AuthResponseDTO authResponseDTO = authService.registerUser(registerRequest);

        Cookie cookie = jwtCookieUtil.createJwtCookie(authResponseDTO.token());

        response.addCookie(cookie);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .build();
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequestDTO loginRequest,
            HttpServletResponse response) {

        AuthResponseDTO authResponseDTO = authService.authenticateUser(loginRequest);

        Cookie cookie = jwtCookieUtil.createJwtCookie(authResponseDTO.token());

        response.addCookie(cookie);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(authResponseDTO.userResponseDTO());
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logOut(HttpServletResponse response) {

        jwtCookieUtil.revokeJwtCookie(response);

        return ResponseEntity.noContent().build();
    }

    @GetMapping("/api/me")
    public ResponseEntity<?> getCurrentUser(HttpServletRequest request, HttpServletResponse response) {

        if (request.getCookies() == null)
            throw new NoCurrentUserException();

        String token = jwtCookieUtil.getTokenFromRequest(request);

        if (token == null)
            return ResponseEntity.noContent().build();

        UserResponseDTO userResponseDTO = authService.getUserDTO(token);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(userResponseDTO);
    }

}
