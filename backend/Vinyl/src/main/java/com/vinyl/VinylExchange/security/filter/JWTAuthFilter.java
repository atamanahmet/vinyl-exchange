package com.vinyl.VinylExchange.security.filter;

import java.io.IOException;
import java.util.UUID;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;

import org.springframework.stereotype.Component;

import org.springframework.web.filter.OncePerRequestFilter;

import com.auth0.jwt.exceptions.JWTVerificationException;

import com.vinyl.VinylExchange.security.principal.UserPrincipal;
import com.vinyl.VinylExchange.security.service.UserDetailsServiceImpl;
import com.vinyl.VinylExchange.security.util.JwtCookieUtil;
import com.vinyl.VinylExchange.security.util.JwtTokenUtil;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class JWTAuthFilter extends OncePerRequestFilter {
    private final JwtTokenUtil jwtTokenUtil;
    private final JwtCookieUtil jwtCookieUtil;
    private final UserDetailsServiceImpl userDetailsServiceImpl;

    public JWTAuthFilter(JwtTokenUtil jwtTokenUtil, JwtCookieUtil jwtCookieUtil,
            UserDetailsServiceImpl userDetailsServiceImpl) {
        this.jwtTokenUtil = jwtTokenUtil;
        this.jwtCookieUtil = jwtCookieUtil;
        this.userDetailsServiceImpl = userDetailsServiceImpl;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        try {

            // without jwt header?
            if (request.getMethod().equalsIgnoreCase("OPTIONS")) {
                filterChain.doFilter(request, response);
                return;
            }
            String token = jwtCookieUtil.extractTokenFromRequest(request);

            if (token == null) {
                filterChain.doFilter(request, response);
                return;
            }

            if (!jwtTokenUtil.validateToken(token)) {
                filterChain.doFilter(request, response);
                return;
            }

            UUID userId = jwtTokenUtil.extractUserId(token);

            if (SecurityContextHolder.getContext().getAuthentication() != null) {
                filterChain.doFilter(request, response);
                return;
            }
            UserPrincipal userPrincipal = userDetailsServiceImpl.loadUserByUserId(userId);

            if (!userPrincipal.isEnabled() ||
                    !userPrincipal.isAccountNonLocked() ||
                    !userPrincipal.isAccountNonExpired() ||
                    !userPrincipal.isCredentialsNonExpired()) {

                logger.warn("Account validation failed for user: " + userId, null);
                filterChain.doFilter(request, response);
                return;
            }

            UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(userPrincipal, null,
                    userPrincipal.getAuthorities());

            authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

            SecurityContextHolder.getContext().setAuthentication(authToken);

        } catch (JWTVerificationException e) {
            logger.warn("JWT verification failed: ", e);

        } catch (UsernameNotFoundException e) {
            logger.warn("User not found for JWT authentication: ", e);
        } catch (Exception e) {
            logger.error("Unexpected error during JWT authentication", e);
        }

        filterChain.doFilter(request, response);
    }

}
