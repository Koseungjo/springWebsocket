package com.example.springwebsocket;

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