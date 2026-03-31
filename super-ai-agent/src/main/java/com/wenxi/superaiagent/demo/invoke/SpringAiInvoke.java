package com.wenxi.superaiagent.demo.invoke;

import jakarta.annotation.Resource;
import org.springframework.ai.chat.messages.AssistantMessage;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

/**
 * AI 大模型接入 - Spring AI
 * CommandLineRunner 接口，在 Spring Boot 应用启动后自动注入大模型 ChatModel依赖，并且单次执行 run 方法，达到测试效果
 */
@Component
public class SpringAiInvoke implements CommandLineRunner {

    @Resource
    private ChatModel dashscopeChatModel;

    @Override
    public void run(String... args) throws Exception {
        AssistantMessage output = dashscopeChatModel.call(new Prompt("什么是 Spring AI ?"))
                .getResult()
                .getOutput();
        System.out.println(output.getText());
    }
}
