package com.example.springwebsocket.chat;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.connection.DataType;
import org.springframework.data.redis.core.ListOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class RedisService {
    private final RedisTemplate redisTemplate;

    public void addMessageToList(String listKey, String message){
        ListOperations<String, String> listOps = redisTemplate.opsForList();
        listOps.rightPush(listKey, message);
    }

    public void deleteMessage(String key) {
        redisTemplate.delete(key);
    }

    public List<String> getMessagesFromList(String listKey, long start, long end) {
        DataType dataType = redisTemplate.type(listKey);

        if (dataType == DataType.LIST) {
            ListOperations<String, String> listOps = redisTemplate.opsForList();
            return listOps.range(listKey, start, end);
        } else if (dataType == DataType.STRING) {
            String singleMessage = redisTemplate.opsForValue().get(listKey).toString();
            return Collections.singletonList(singleMessage);
        } else {
            return Collections.emptyList();
        }
    }

}