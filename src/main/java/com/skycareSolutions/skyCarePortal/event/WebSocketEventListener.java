package com.skycareSolutions.skyCarePortal.event;
import com.skycareSolutions.skyCarePortal.utils.UserSessionRegistry;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;


import org.springframework.stereotype.Component;

@Component
public class WebSocketEventListener {

    @EventListener
    public void handleWebSocketDisconnectListener(SessionDisconnectEvent event) {
        String sessionId = event.getSessionId();
        UserSessionRegistry.removeUserSession(sessionId); // Remove user from registry
    }
}
