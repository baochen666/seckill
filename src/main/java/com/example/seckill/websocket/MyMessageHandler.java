package com.example.seckill.websocket;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.stereotype.Service;

@Service
public class MyMessageHandler {

    @Autowired
    private SimpMessagingTemplate template;

    public void sendMessageToUI(Object message) {
        this.template.convertAndSend("/topic/messages", message);
    }
}
