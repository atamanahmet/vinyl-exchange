package com.vinyl.VinylExchange.security.config;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.HandshakeInterceptor;

import com.vinyl.VinylExchange.security.util.JwtCookieUtil;
import com.vinyl.VinylExchange.security.util.JwtTokenUtil;

import jakarta.servlet.http.HttpServletRequest;

public class JwtHandshakeInterceptor implements HandshakeInterceptor {

    private final Logger logger = LoggerFactory.getLogger(JwtHandshakeInterceptor.class);

    private final JwtCookieUtil jwtCookieUtil;
    private final JwtTokenUtil jwtTokenUtil;

    public JwtHandshakeInterceptor(JwtCookieUtil jwtCookieUtil, JwtTokenUtil jwtTokenUtil) {
        this.jwtCookieUtil = jwtCookieUtil;
        this.jwtTokenUtil = jwtTokenUtil;
    }

    @Override
    public boolean beforeHandshake(
            ServerHttpRequest request,
            ServerHttpResponse response,
            WebSocketHandler wsHandler,
            Map<String, Object> attributes) throws Exception {

        if (!(request instanceof ServletServerHttpRequest)) {
            logger.warn("request is not servlet");
        }

        ServletServerHttpRequest servletRequest = (ServletServerHttpRequest) request;

        HttpServletRequest httpServletRequest = servletRequest.getServletRequest();

        try {
            String token = jwtCookieUtil.extractTokenFromRequest(httpServletRequest);

            if (token == null) {
                logger.warn("Token is null from websocket hadnshake cookie. ip: " + httpServletRequest.getRemoteAddr());

                return false;
            }

            if (!jwtTokenUtil.validateToken(token)) {
                logger.warn("Token is present but invalid from websocket handshake");
                return false;
            }

        } catch (Exception e) {
            // TODO: handle exception
        }
    }

    @Override
    public void afterHandshake(ServerHttpRequest arg0, ServerHttpResponse arg1, WebSocketHandler wsHandler,
            Exception arg3) {
        // TODO Auto-generated method stub

    }

}
