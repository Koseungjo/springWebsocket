package com.example.springwebsocket.chat;

import lombok.EqualsAndHashCode;
import lombok.ToString;
import lombok.Value;

import java.util.Date;

@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Value
public class OutputMessage extends Message {
    Date time;

    public OutputMessage(String from, String text, Date time) {
        super(from, text);
        this.time = time;
    }
}
