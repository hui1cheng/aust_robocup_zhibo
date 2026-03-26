package com.hui.controller;

import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;

@Controller
public class DanmakuController {

    @MessageMapping("/send/{roomId}") // 客户端发送到 /app/send/room1
    @SendTo("/topic/messages/{roomId}") // 自动转发给订阅了 /topic/messages/room1 的人
    public DanmakuMessage broadcast(
        @DestinationVariable String roomId,
        DanmakuMessage message
    ) {
        // 这里可以做敏感词过滤、持久化到数据库等逻辑
        return message; 
    }
}

// 消息实体类
class DanmakuMessage {
    public String text;
    public String from;
    public String color;
}