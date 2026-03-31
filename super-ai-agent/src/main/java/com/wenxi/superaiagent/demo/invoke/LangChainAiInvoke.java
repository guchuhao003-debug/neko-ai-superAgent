package com.wenxi.superaiagent.demo.invoke;


import dev.langchain4j.community.model.dashscope.QwenChatModel;

/**
 * AI 大模型接入 - LangChain4j
 */
public class LangChainAiInvoke {

    public static void main(String[] args) {
        QwenChatModel qwenModel = QwenChatModel.builder()
                .apiKey(TestApiKey.API_KEY)
                .modelName("qwen-max")
                .build();
        String answer = qwenModel.chat("什么是 LangChain4j ?");
        System.out.println(answer);
    }
}
