package com.example.springwebsocket;

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

    private final StringRedisTemplate redisTemplate;

    @MessageMapping("/message")
    @SendTo("/topic/messages")
    public OutputMessage sendMessage(Message message) {
        OutputMessage outputMessage = new OutputMessage(message.getFrom(), message.getText(), new Date());

        // 메시지를 Redis에 저장합니다.
        ListOperations<String, String> opsForList = redisTemplate.opsForList();
        opsForList.rightPush("chatHistory", outputMessage.toString());

        return outputMessage;
    }
}