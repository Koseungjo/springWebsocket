package com.example.springwebsocket;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.sql.Timestamp;
import java.util.Set;

@RestController
@RequiredArgsConstructor
public class RedisController {
    private final RedisService redisService;

    @PostMapping(value = "/redis/test/setString")
    @ResponseBody
    public void setValue(@RequestBody RedisData redisData){
        Timestamp currentTimestamp = new Timestamp(System.currentTimeMillis());
        redisService.setValues(redisData.getTestKey(),redisData.getTestValue());
    }

    @GetMapping(value = "/redis/test/getString")
    @ResponseBody
    public String getValue(String testkey){
        return redisService.getValues(testkey);
    }


    @RequestMapping(value = "/redis/test/setSets")
    @ResponseBody
    public void setSets(String testkey,String... testvalues){
        redisService.setSets(testkey,testvalues);
    }

    @RequestMapping(value = "/redis/test/getSets")
    @ResponseBody
    public Set getSets(String key){
        return redisService.getSets(key);
    }

}