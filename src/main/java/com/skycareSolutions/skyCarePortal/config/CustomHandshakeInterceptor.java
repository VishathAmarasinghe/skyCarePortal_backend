package com.skycareSolutions.skyCarePortal.config;

import com.skycareSolutions.skyCarePortal.utils.UserSessionRegistry;
import org.springframework.web.socket.server.HandshakeInterceptor;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.HandshakeInterceptor;

import java.util.Map;
import java.util.UUID;


public class CustomHandshakeInterceptor implements HandshakeInterceptor {
    @Override
    public boolean beforeHandshake(ServerHttpRequest request, ServerHttpResponse response,
                                   WebSocketHandler wsHandler, Map<String, Object> attributes) {
        if (request instanceof ServletServerHttpRequest) {
            ServletServerHttpRequest servletRequest = (ServletServerHttpRequest) request;
            String username = servletRequest.getServletRequest().getParameter("username"); // Get username from request

            if (username != null && !username.isEmpty()) {
                String sessionId = UUID.randomUUID().toString(); // Generate a unique session ID
                attributes.put("username", username);
                attributes.put("sessionId", sessionId);
//                UserSessionRegistry.addUserSession(sessionId,username);
                System.out.println("session id and username added "+username);
                return true;
            }
        }
        return false; // Reject connection if no username is provided
    }

    @Override
    public void afterHandshake(ServerHttpRequest request, ServerHttpResponse response,
                               WebSocketHandler wsHandler, Exception exception) {
        // Do nothing after handshake
    }
}
