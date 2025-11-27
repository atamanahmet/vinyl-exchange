package com.vinyl.VinylExchange.security.util;

import org.springframework.stereotype.Component;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.vinyl.VinylExchange.domain.entity.User;
import com.vinyl.VinylExchange.security.config.JwtConfig;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;

@Component
public class JwtTokenUtil {

    private final JwtConfig jwtConfig;

    public JwtTokenUtil(JwtConfig jwtConfig) {
        this.jwtConfig = jwtConfig;
    }

    public String generateToken(User user) {

        String token = JWT.create()
                .withSubject(user.getId().toString())
                .withClaim("username", user.getUsername())
                .sign(Algorithm.HMAC512(jwtConfig.getSECRET()));

        System.out.println("Created Token: " + token);

        return token;
    }

    public boolean validateToken(String token, User user) {

        if (user == null || token == null || user.getId() == null) {
            return false;
        }

        Long id = null;

        try {
            id = extractUserId(token);

        } catch (Exception e) {
            System.out.println(e.getLocalizedMessage());
        }

        return id != null && id.equals(user.getId());
    }

    // validation from request
    public boolean validateToken(HttpServletRequest request, User user) {

        if (request == null || user == null)
            return false;

        String token = extractToken(request);

        return validateToken(token, user);
    }

    // xtracting id from token
    public Long extractUserId(String token) {

        try {
            String idStr = JWT
                    .require(Algorithm.HMAC512(jwtConfig.getSECRET()))
                    .build()
                    .verify(token)
                    .getSubject();

            return Long.valueOf(idStr);

        } catch (Exception e) {

            System.out.println("Invalid Token");
            System.out.println(e.getLocalizedMessage());

            return null;
        }
    }

    // xtracting username from token
    public String extractUsername(String token) {

        if (token == null)
            return null;

        try {
            String username = JWT
                    .require(Algorithm.HMAC512(jwtConfig.getSECRET()))
                    .build()
                    .verify(token)
                    .getClaim("username")
                    .asString();

            return username;

        } catch (Exception e) {

            System.out.println("Invalid Token");
            System.out.println(e.getLocalizedMessage());

            return null;
        }
    }

    // xtracting username from request
    public String extractUsername(HttpServletRequest request) {

        try {
            String token = extractToken(request);
            return extractUsername(token);

        } catch (Exception e) {
            System.out.println("Invalid Token");
            System.out.println(e.getLocalizedMessage());

            return null;
        }
    }

    // xtracting token from request
    public String extractToken(HttpServletRequest request) {

        if (request == null || request.getCookies() == null)
            return null;

        return extractToken(request.getCookies());
    }

    // xtracting token from cookies
    public String extractToken(Cookie[] cookies) {

        if (cookies == null)
            return null;

        for (Cookie cookie : cookies) {
            if (cookie.getName().equals("jwt_token") && cookie.getValue() != null) {

                System.out.println("token from cookie: " + cookie.getValue());

                return cookie.getValue();
            }
        }

        System.out.println("Invalid Token");

        return null;
    }
}
