package com.vinyl.VinylExchange.security.service;

import java.util.UUID;

import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;

import com.vinyl.VinylExchange.security.principal.UserDetailsImpl;
import com.vinyl.VinylExchange.exception.NoCurrentUserException;
import com.vinyl.VinylExchange.domain.entity.User;
import com.vinyl.VinylExchange.repository.UserRepository;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    private final UserRepository userRepository;

    public UserDetailsServiceImpl(UserRepository userRepository) {

        this.userRepository = userRepository;
    }

    @Override
    public UserDetailsImpl loadUserByUsername(String username) {

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new NoCurrentUserException());

        return new UserDetailsImpl(user);
    }

    public UserDetailsImpl loadUserByUserId(UUID userId) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NoCurrentUserException());

        return new UserDetailsImpl(user);
    }
}
