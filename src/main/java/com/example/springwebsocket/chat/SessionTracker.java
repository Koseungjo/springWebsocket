package com.example.springwebsocket.chat;

import org.springframework.stereotype.Component;

import java.util.concurrent.ConcurrentHashMap;

@Component
public class SessionTracker {
    private ConcurrentHashMap<String, String> sessionUserMap = new ConcurrentHashMap<>();
    private ConcurrentHashMap<String, Boolean> sessionMap = new ConcurrentHashMap<>();

    public boolean isNewConnection(String sessionId) {
        return sessionMap.putIfAbsent(sessionId, Boolean.TRUE) == null;
    }

    public void addUserSession(String sessionId, String username) {
        sessionUserMap.put(sessionId, username);
    }

    public String getUsername(String sessionId) {
        return sessionUserMap.get(sessionId);
    }

    public void removeSession(String sessionId) {
        sessionMap.remove(sessionId);
        sessionUserMap.remove(sessionId);
    }
}

