package com.example.springwebsocket.chat;

import lombok.Data;

@Data
public class Message {
    private String from;
    private String text;

    public Message(String from, String text) {
        this.from = from;
        this.text = text;
    }
}
