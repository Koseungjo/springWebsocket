package com.example.springwebsocket;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.ListOperations;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Date;
import java.util.List;

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

            redisService.addMessageToList("Chat_History", jsonMessage);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        return outputMessage;
    }

    // 여기에 새 메소드를 추가합니다.
    @GetMapping("/history")
    public ResponseEntity<List<String>> getMessagesFromList(
            @RequestParam(required = false, defaultValue = "0") long start,
            @RequestParam(required = false, defaultValue = "-1") long end) {

        List<String> messages = redisService.getMessagesFromList("Chat_History", start, end);
        return ResponseEntity.ok(messages);
    }
}