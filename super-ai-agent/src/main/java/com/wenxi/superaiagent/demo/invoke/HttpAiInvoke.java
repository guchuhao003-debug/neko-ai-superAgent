package com.wenxi.superaiagent.demo.invoke;

import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import cn.hutool.json.JSONObject;

import java.util.HashMap;

/**
 * AI 大模型接入 - Http 接入
 */
public class HttpAiInvoke {

    /**
     * API 端点位置
     */
    private static final String API_URL = "https://dashscope.aliyuncs.com/api/v1/services/aigc/text-generation/generation";

    public static void main(String[] args) {

        // 设置请求头参数
        HashMap<String, String> headers = new HashMap<>();
        headers.put("Authorization","Bearer" + TestApiKey.API_KEY);
        headers.put("Content-Type","application/json");

        // 设置请求体参数
        JSONObject requestBody = new JSONObject();
        requestBody.put("model","qwen-plus");

        JSONObject input = new JSONObject();
        JSONObject[] messages = new JSONObject[2];

        JSONObject systemMessage = new JSONObject();
        systemMessage.put("role","system");
        systemMessage.put("content","你是一个超级智能助手，请根据用户需求回答问题");
        messages[0] = systemMessage; // 设置系统消息

        JSONObject userMessage = new JSONObject();
        userMessage.put("role","user");
        userMessage.put("content","什么是 AI Agent ? ");
        messages[1] = userMessage; // 设置用户消息

        input.put("messages",messages);
        requestBody.put("input",input);

        JSONObject parameters = new JSONObject();
        parameters.put("result_format","message");
        requestBody.put("parameters",parameters);

        // 发送请求
        HttpResponse response = HttpRequest.post(API_URL)
                .addHeaders(headers)
                .body(requestBody.toString())
                .execute();

        // 处理响应
        if(response.isOk()) {
            System.out.println("请求成功，响应内容: ");
            System.out.println(response.body());
        } else {
            System.out.println("请求失败，状态码: " + response.getStatus());
            System.out.println("响应内容: " + response.body());
        }
    }
}
