package com.vinyl.VinylExchange.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class UserController {
    @PostMapping("/logout")
    public ResponseEntity<String> logOut() {
        return new ResponseEntity<>("LoggedOut", HttpStatus.OK);
    }
}
