package com.vinyl.VinylExchange.auth;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import org.springframework.security.core.annotation.AuthenticationPrincipal;

import org.springframework.stereotype.Controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import com.vinyl.VinylExchange.auth.dto.AuthResponse;
import com.vinyl.VinylExchange.auth.dto.LoginRequest;
import com.vinyl.VinylExchange.auth.dto.RegisterRequest;
import com.vinyl.VinylExchange.security.principal.UserPrincipal;
import com.vinyl.VinylExchange.security.util.JwtCookieUtil;

import com.vinyl.VinylExchange.user.dto.UserDTO;

import jakarta.servlet.http.Cookie;
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
    public ResponseEntity<UserDTO> getCurrentUser(@AuthenticationPrincipal UserPrincipal userPrincipal) {

        if (userPrincipal == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        UserDTO userDTO = new UserDTO(userPrincipal);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(userDTO);
    }

}
