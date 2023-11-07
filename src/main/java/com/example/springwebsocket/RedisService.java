package com.example.springwebsocket;

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

    public List<String> getMessagesFromList(String listKey, long start, long end) {
        DataType dataType = redisTemplate.type(listKey);

        if (dataType == DataType.LIST) {
            // 데이터 타입이 리스트라면 그대로 반환
            ListOperations<String, String> listOps = redisTemplate.opsForList();
            return listOps.range(listKey, start, end);
        } else if (dataType == DataType.STRING) {
            // 데이터 타입이 문자열이라면 리스트에 포장하여 반환
            String singleMessage = redisTemplate.opsForValue().get(listKey).toString();
            return Collections.singletonList(singleMessage);
        } else {
            // 다른 데이터 타입은 빈 리스트나 적절한 처리를 할 수 있습니다.
            return Collections.emptyList();
        }
    }

    public String getValues(String key){
        //opsForValue : Strings를 쉽게 Serialize / Deserialize 해주는 Interface
        ValueOperations<String, String> values = redisTemplate.opsForValue();
        return values.get(key);
    }



    public void setValues(String key, String value){
        ValueOperations<String, String> values = redisTemplate.opsForValue();
        values.set(key,value);
    }

    public void setSets(String key,String... values){
        redisTemplate.opsForSet().add(key,values);
    }

    public Set getSets(String key){
        return redisTemplate.opsForSet().members(key);
    }



}