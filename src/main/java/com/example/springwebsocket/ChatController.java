package com.example.springwebsocket;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.ListOperations;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;

import java.util.Date;

@Controller
@RequiredArgsConstructor
public class ChatController {

    private final RedisService redisService;
    private final ObjectMapper objectMapper; // Add this to convert objects to JSON

    @MessageMapping("/message")
    @SendTo("/topic/messages")
    public OutputMessage sendMessage(Message message) {
        OutputMessage outputMessage = new OutputMessage(message.getFrom(), message.getText(), new Date());
        try {
            String jsonMessage = objectMapper.writeValueAsString(outputMessage);

            redisService.addMessageToList("chatHistory", jsonMessage);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        return outputMessage;
    }
}