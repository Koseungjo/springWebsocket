package com.example.springwebsocket;

import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;

import java.util.Date;

@Controller
public class ChatController {

    @MessageMapping("/message")
    @SendTo("/topic/messages")
    public OutputMessage sendMessage(Message message) {
        return new OutputMessage(message.getFrom(), message.getText(), new Date());
    }
}