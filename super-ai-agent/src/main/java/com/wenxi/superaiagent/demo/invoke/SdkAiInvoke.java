package com.wenxi.superaiagent.demo.invoke;

import com.alibaba.dashscope.aigc.generation.Generation;
import com.alibaba.dashscope.aigc.generation.GenerationParam;
import com.alibaba.dashscope.aigc.generation.GenerationResult;
import com.alibaba.dashscope.common.Message;
import com.alibaba.dashscope.common.Role;
import com.alibaba.dashscope.exception.InputRequiredException;
import com.alibaba.dashscope.exception.NoApiKeyException;
import com.alibaba.dashscope.utils.JsonUtils;

import java.util.Arrays;

/**
 * 大模型接入 —— SDK方式
 */
public class SdkAiInvoke {
    public static GenerationResult callWithMessage() throws NoApiKeyException, InputRequiredException {
        Generation gen = new Generation();
        // 设置系统提示词参数
        Message systemMsg = Message.builder()
                .role(Role.SYSTEM.getValue())
                .content("你是一个智能助手，请根据用户输入回答问题")
                .build();
        // 设置用户提示词参数
        Message userMsg = Message.builder()
                .role(Role.USER.getValue())
                .content("如何学习 AI 智能体")
                .build();
        GenerationParam param = GenerationParam.builder()
                .apiKey(TestApiKey.API_KEY)
                .model("qwen-plus")
                .messages(Arrays.asList(systemMsg, userMsg))
                .resultFormat(GenerationParam.ResultFormat.MESSAGE)
                .build();
        return gen.call(param);
    }

    public static void main(String[] args) {
        try {
            GenerationResult result = callWithMessage();
            // 将结果转化为 Json 格式数据
            System.out.println(JsonUtils.toJson(result));
        } catch (NoApiKeyException | InputRequiredException e) {
            System.err.println("An error occurred while calling the generation service: " + e.getMessage());
        }
        System.exit(0);
    }

}
