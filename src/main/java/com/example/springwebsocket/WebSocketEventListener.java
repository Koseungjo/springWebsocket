package com.example.springwebsocket;

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

        // 세션이 처음 연결됐는지 확인하는 로직이 필요합니다. 예를 들면:
        if (sessionTracker.isNewConnection(sessionId)) {
            List<String> chatHistory = redisTemplate.opsForList().range("chatHistory", 0, -1);

            // 채팅 기록을 새로 연결된 클라이언트에게 전송합니다.
            if (chatHistory != null) {
                chatHistory.forEach(message -> messagingTemplate.convertAndSendToUser(sessionId, "/topic/messages", message));
            }
        }

        logger.info("Received a new web socket connection");
    }


    @EventListener
    public void handleWebSocketDisconnectListener(SessionDisconnectEvent event) {
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