package com.vinyl.VinylExchange.security.util;

import org.springframework.stereotype.Component;

import com.auth0.jwt.exceptions.JWTVerificationException;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class JwtCookieUtil {

    public Cookie createJwtCookie(String token) {

        Cookie cookie = new Cookie("jwt", token);

        cookie.setHttpOnly(true);
        cookie.setSecure(true); // https
        cookie.setPath("/");
        cookie.setMaxAge(60 * 60); // 1 hour

        return cookie;
    }

    public void revokeJwtCookie(HttpServletResponse response) {

        Cookie cookie = new Cookie("jwt", null);

        cookie.setHttpOnly(true);
        cookie.setSecure(true); // https
        cookie.setPath("/");
        cookie.setMaxAge(0); // cookie removal

        response.addCookie(cookie);
    }

    public String extractTokenFromRequest(HttpServletRequest request) {

        if (request == null || request.getCookies() == null)
            throw new JWTVerificationException("Invalid token");

        Cookie[] cookies = request.getCookies();

        for (Cookie cookie : cookies) {
            if (cookie.getName().equals("jwt")) {
                return cookie.getValue();
            }
        }
        return null;
    }
}
