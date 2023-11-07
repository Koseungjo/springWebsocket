package com.example.springwebsocket;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionConnectedEvent;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

@Component
@RequiredArgsConstructor
public class WebSocketEventListener {

    private static final Logger logger = LoggerFactory.getLogger(WebSocketEventListener.class);
    private final SimpMessagingTemplate messagingTemplate;
    private final StringRedisTemplate redisTemplate;
    private final SessionTracker sessionTracker;
    private ObjectMapper objectMapper = new ObjectMapper();


    @EventListener
    public void handleWebSocketConnectListener(SessionConnectedEvent event) throws JsonProcessingException {
        StompHeaderAccessor headerAccessor = StompHeaderAccessor.wrap(event.getMessage());
        String sessionId = headerAccessor.getSessionId();

        if (sessionTracker.isNewConnection(sessionId)) {
            List<String> chatHistory = redisTemplate.opsForList().range("chatHistory", 0, -1);

            if (chatHistory != null) {
                // JSON 배열 형태로 변환
                String chatHistoryJson = objectMapper.writeValueAsString(chatHistory);
                messagingTemplate.convertAndSendToUser(sessionId, "/queue/chatHistory", chatHistoryJson);
            }
        }

        logger.info("Received a new web socket connection");
    }


    @EventListener
    public void handleWebSocketDisconnectListener(SessionDisconnectEvent event) throws JsonProcessingException {
        StompHeaderAccessor headerAccessor = StompHeaderAccessor.wrap(event.getMessage());
        String sessionId = headerAccessor.getSessionId();
        sessionTracker.removeSession(sessionId);
        String username = (String) headerAccessor.getSessionAttributes().get("username");

        if(username != null) {
            logger.info("User Disconnected : " + username);

            OutputMessage chatMessage = new OutputMessage(username, "님이 퇴장하셨습니다.", new Date());
            messagingTemplate.convertAndSend("/topic/public", chatMessage);
        }
    }
}