package com.example.habittt.common;

import jakarta.websocket.*;
import jakarta.websocket.server.PathParam;
import jakarta.websocket.server.ServerEndpoint;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Component
@ServerEndpoint("/ws/{userId}")
public class WebSocketServer {

    private static final ConcurrentHashMap<String, Session> SESSIONS = new ConcurrentHashMap<>();

    // 【修复】使用静态变量存储 ApplicationContext
    private static ApplicationContext applicationContext;

    @Autowired
    public void setApplicationContext(ApplicationContext context) {
        WebSocketServer.applicationContext = context;
    }

    @OnOpen
    public void onOpen(Session session, @PathParam("userId") String userId) {
        SESSIONS.put(userId, session);
        log.info("用户 {} 已建立 WebSocket 连接，当前在线人数: {}", userId, SESSIONS.size());
    }

    @OnClose
    public void onClose(@PathParam("userId") String userId) {
        SESSIONS.remove(userId);
        log.info("用户 {} 断开 WebSocket 连接", userId);
    }

    @OnMessage
    public void onMessage(String message, @PathParam("userId") String userId) {
        log.info("收到用户 {} 的消息: {}", userId, message);
    }

    @OnError
    public void onError(Session session, Throwable error) {
        log.error("WebSocket 发生错误: {}", error.getMessage());
        error.printStackTrace();
    }

    /**
     * 向指定用户发送消息
     */
    public static void sendMessageToUser(String userId, String message) {
        Session session = SESSIONS.get(userId);
        if (session != null && session.isOpen()) {
            try {
                session.getBasicRemote().sendText(message);
            } catch (Exception e) {
                log.error("发送消息给用户 {} 失败: {}", userId, e.getMessage());
            }
        } else {
            log.warn("用户 {} 不在线或连接已关闭", userId);
        }
    }
}