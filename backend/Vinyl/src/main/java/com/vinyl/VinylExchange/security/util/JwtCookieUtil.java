package com.vinyl.VinylExchange.security.util;

import org.springframework.stereotype.Component;

import com.vinyl.VinylExchange.domain.entity.User;

import jakarta.servlet.http.Cookie;

@Component
public class JwtCookieUtil {
    private final JwtTokenUtil jwtTokenUtil;

    public JwtCookieUtil(JwtTokenUtil jwtTokenUtil) {
        this.jwtTokenUtil = jwtTokenUtil;
    }

    public Cookie createJwtCookie(User user) {
        String token = jwtTokenUtil.generateToken(user);

        Cookie cookie = new Cookie("jwt", token);

        cookie.setHttpOnly(true);
        cookie.setSecure(true); // https
        cookie.setPath("/");
        cookie.setMaxAge(60 * 60); // 1 hour test
        return cookie;
    }
}
