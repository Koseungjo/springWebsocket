package com.example.springwebsocket;

import org.springframework.stereotype.Component;

import java.util.concurrent.ConcurrentHashMap;

@Component
public class SessionTracker {
    private ConcurrentHashMap<String, Boolean> sessionMap = new ConcurrentHashMap<>();

    public boolean isNewConnection(String sessionId) {
        return sessionMap.putIfAbsent(sessionId, Boolean.TRUE) == null;
    }

    public void removeSession(String sessionId) {
        sessionMap.remove(sessionId);
    }
}
