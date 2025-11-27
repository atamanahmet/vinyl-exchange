package com.vinyl.VinylExchange.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import com.vinyl.VinylExchange.domain.dto.RegisterRequestDTO;
import com.vinyl.VinylExchange.domain.dto.UserResponseDTO;
import com.vinyl.VinylExchange.domain.entity.User;
import com.vinyl.VinylExchange.security.util.JwtCookieUtil;
import com.vinyl.VinylExchange.service.UserService;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;

@Controller
public class UserController {
    private final UserService userService;
    private final JwtCookieUtil jwtCookieUtil;

    public UserController(UserService userService, JwtCookieUtil jwtCookieUtil) {
        this.userService = userService;
        this.jwtCookieUtil = jwtCookieUtil;
    }

    @PostMapping("/logout")
    public ResponseEntity<String> logOut() {

        return new ResponseEntity<>("LoggedOut", HttpStatus.OK);
    }

    @PostMapping("/register")
    public ResponseEntity<UserResponseDTO> register(@Valid @RequestBody RegisterRequestDTO registerRequest,
            HttpServletResponse response) {

        User user = userService.registerUser(registerRequest);

        Cookie cookie = jwtCookieUtil.createJwtCookie(user);
        response.addCookie(cookie);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(new UserResponseDTO(user));
    }

}
