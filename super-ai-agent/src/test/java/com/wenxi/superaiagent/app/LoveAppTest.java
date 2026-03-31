package com.wenxi.superaiagent.app;

import jakarta.annotation.Resource;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.UUID;

@SpringBootTest
class LoveAppTest {

    @Resource
    private LoveApp loveApp;

    /**
     * CHAT_MEMORY_RETRIEVE_SIZE_KEY = 1 ，测试对话记忆数 = 1
     */
    @Test
    void doChat() {
        String chatId = UUID.randomUUID().toString();
        // 第一轮对话
        String message = "你好，我是 KK";
        String answer = loveApp.doChat(message, chatId);
        Assertions.assertNotNull(answer);

        // 第二轮对话
        message = "我的对象是周也 ，我们最近打算去约会，你有什么建议呢？";
        answer = loveApp.doChat(message, chatId);
        Assertions.assertNotNull(answer);

        // 第三轮对话
        message = "我的对象是谁，我刚跟你说过，你帮我回忆下。";
        answer = loveApp.doChat(message,chatId);
        Assertions.assertNotNull(answer);

    }
}