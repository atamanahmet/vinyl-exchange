package com.vinyl.VinylExchange.user;

import org.springframework.stereotype.Controller;

import com.vinyl.VinylExchange.security.util.JwtCookieUtil;

@Controller
public class UserController {
    // private final UserService userService;
    // private final JwtCookieUtil jwtCookieUtil;

    public UserController(UserService userService, JwtCookieUtil jwtCookieUtil) {
        // this.userService = userService;
        // this.jwtCookieUtil = jwtCookieUtil;
    }

}
