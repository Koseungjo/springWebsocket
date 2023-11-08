package com.example.springwebsocket.chat;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionConnectedEvent;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;
import org.springframework.context.event.EventListener;

import java.util.Date;
import java.util.List;

@Component
@RequiredArgsConstructor
public class WebSocketEventListener {

    private static final Logger logger = LoggerFactory.getLogger(WebSocketEventListener.class);
    private final SimpMessagingTemplate messagingTemplate;
    private final StringRedisTemplate redisTemplate;
    private final SessionTracker sessionTracker;

    @EventListener
    public void handleWebSocketConnectListener(SessionConnectedEvent event) {
        StompHeaderAccessor headerAccessor = StompHeaderAccessor.wrap(event.getMessage());

        String sessionId = headerAccessor.getSessionId();

        if (sessionTracker.isNewConnection(sessionId)) {
            List<String> chatHistory = redisTemplate.opsForList().range("Chat_History_2", 0, -1);

            if (chatHistory != null) {
                chatHistory.forEach(message -> messagingTemplate.convertAndSendToUser(sessionId, "/topic/messages", message));
            }
        }

        logger.info("Received a new web socket connection from session ID: " + sessionId);
    }

    @EventListener
    public void handleWebSocketDisconnectListener(SessionDisconnectEvent event) {
        StompHeaderAccessor headerAccessor = StompHeaderAccessor.wrap(event.getMessage());
        String sessionId = headerAccessor.getSessionId();

        if(sessionId != null) {
            String username = sessionTracker.getUsername(sessionId);
            sessionTracker.removeSession(sessionId);

            logger.info("User Disconnected : " + username);

            OutputMessage chatMessage = new OutputMessage(username, "님이 퇴장하셨습니다.", new Date());
            messagingTemplate.convertAndSend("/topic/messages", chatMessage);
        }
    }
}
