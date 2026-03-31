# Super-Ai-Agent 笔记
## 1、AI 大模型接入
---- -------
### 1.1、接入方式
代码位置: `com.wenxi.superaiagent.demo.invoke`
- SDK 方式 ( SdkAiInvoke.java )
- HTTP 方式 ( HttpAiInvoke.java )
- Spring AI ( SpringAiInvoke.java )
- LangChain4j ( LangChainAiInvoke.java )

## 2、AI 应用方案设计
---- -------
### 2.1、Prompt 系统提示词设计 ( AI生成 )
```markdown
我要开发一个【恋爱大师】AI 应用，用户在恋爱过程中难免遇到各种难题，让 AI 为用户提供贴心情感指导。
请你帮我进行需求分析，满足我的目标。

具体需要:
1. 分析目标用户群体及其核心需求
2. 提出主要功能模块和服务内容
3. 考虑产品的差异化竞争点
4. 分析可能的技术挑战和解决方案
```
优化后的提示词（ AI生成 ）:
```markdown
扮演深耕恋爱心理领域的专家。开场向用户表明身份，告知用户可倾诉恋爱难题。
围绕单身、恋爱、已婚三种状态提问： 
单身状态询问社交圈拓展及追求心仪对象的困扰；
恋爱状态询问沟通、习惯差异引发的矛盾；
已婚状态询问家庭责任与亲属关系处理的问题。
引导用户详述事情经过、对方反应及自身想法，以便给出专属解决方案。
```
### 2.2、多轮对话实现
#### ChatClient 特性
- SpringBoot 注入的 ChatModel 来调用大模型完成对话。
- 构造 ChatClient 对象，可实现功能更丰富、更灵活的 AI 对象客户端（推荐）。
```java
// 1、基础用法 - SpringBoot 注入的 ChatModel
ChatResponse response = chatModel.call(new Prompt("什么是 ChatModel ?"));

// 2、高级用法 - ChatClient AI 客户端对象
ChatClient chatClient = ChatClient.builder(chatModel)
        .defaultSystem("你是恋爱顾问")
        .build();
String response = chatClient
        .prompt()
        .user("你好")
        .call()
        .content();
```
- ChatClient : 1）、建造者方式构造；  2）构造器方式
```java
// 1、建造者方式构造
ChatClient chatClient=ChatClient.builder(chatModel)
        .defaultSystem("你是恋爱顾问")
        .build();

// 2、构造器方式构造
@Service
private class ChatService {
    private final ChatClient chatClient;

    public ChatService(ChatClient.Builder builder) {
        this.chatClient = builder
                .defaultSystem("你是恋爱顾问")
                .build();
    }
}
```
- ChatClient 支持多种响应格式，比如返回 ChatResponse 对象、返回实体对象、流式返回：

```java
// ChatClient 支持多种响应格式
// 1、返回 ChatResponse 对象（包含元数据 meta ，如 token 使用量）

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.core.ParameterizedTypeReference;ChatResponse chatResponse=chatClient.prompt()
        .user("什么是 ChatClient ?")
        .call()
        .chatResponse();

// 2、返回实体类对象（自动将 AI 输出映射为 Java 对象）
// 2.1、返回单个实体
record ActorFilms(String actor, List<String> movies) {
}
    ActorFilms actorFilms = chatClient.prompt()
            .user("请列出汤姆·克鲁斯出演的所有电影")
            .call()
            .entity(ActorFilms.class);

    // 2.2、返回泛型集合
    List<ActorFilms> actorFilmsList = chatClient.prompt()
            .user("请列出汤姆·克鲁斯出演的所有电影")
            .call()
            .entity(new ParameterizedTypeReference<List<ActorFilms>>(){});
    
    // 3、流式返回（适用于打字机效果）
    Flux<String> streamResponse = chatClient.prompt()
            .user("请列出汤姆·克鲁斯出演的所有电影")
            .stream()
            .content();
    
    // 也可以流式返回 ChatResponse 
    Flux<ChatResponse> streamResponse = chatClient.prompt()
            .user("请列出汤姆·克鲁斯出演的所有电影")
            .stream()
            .chatResponse();
```
- ChatClient 设置默认参数（例如：系统提示词 system_prompt），还可以对话时动态修改系统提示词的变量，类似模板：
```java
// 定义默认系统提示词
ChatClient chatClient = ChatClient.builder(chatModel)
        .defaultSystemPrompt("你是一个超级智能助手，请你以{voice}的语气回答用户问题")
        .build();

// 定义临时变量
String voice = "温柔";
String message = "什么是 Spring AI ?"
        
// 对话时动态更改系统提示词的变量
chatClient.prompt()
        .system(sp -> sp.param("voice",voice))
        .user(message)
        .call()
        .content();
```
- 此外， ChatClient 还支持指定默认对话选项(ChatOption)、默认拦截器(Advisors)、默认函数调用等。

#### Advisors
- Advisors（顾问） 是一个拦截器，用于在对话前、对话后、对话中执行一些操作，例如：记录日志、修改对话内容、修改返回结果等。
- 前置增强 : 调用 AI 前改写一下 Prompt 提示词, 检查一下提示词是否安全。
- 后置增强 : 调用 AI 后记录日志，处理一下返回的结果。
例如：对话记忆拦截器 MessageChatMemoryAdvisor 可以实现多轮对话能力。
```java
ChatClient chatClient = ChatClient.builder(chatModel)
        .defaultAdvisors(
                new MessageChatMemoryAdvisor(chatMemory), // 对话记忆拦截器
                new LogChatAdvisor(), // 日志拦截器
                new QuestionAnswerAdvisor(vectorStore) // Rag 检索增强 advisor
        )
        .build();

String response = this.chatClient.prompt()
        // 对话时动态设置拦截器参数，比如指定对话记忆的 id 和长度
        .advisors(advisor -> advisor.param("chat_memory_conversation_id","123456")
        .param("chat_memory_response_size", 100))
        .user(userText)
        .call()
        .content();
```
#### Chat Memory Advisor
- MessageChatMemoryAdvisor : 从记忆中检索历史对话，并将其作为消息集合添加到提示词中(推荐)
- PromptChatMemoryAdvisor : 从记忆中检索历史对话，并将其添加到提示词的系统文本中
- VectorStoreChatMemoryAdvisor : 可以用向量数据库来存储检索历史对话

#### Chat Memory
Spring AI 内置了几种 Chat Memory,可以将对话存储到不同的数据源中：
- InMemoryChatMemory : 内存存储，对话内容存储在内存中，重启后内容丢失
- CassandraChatMemory : Cassandra 数据库存储，对话内容存储在 Cassandra 数据库中，带有过期时间的持久化存储
- Neo4jChatMemory : Neo4j 图数据库存储，对话内容存储在 Neo4j 图数据库, 没有过期时间限制的持久化存储
- JdbcChatMemory : Jdbc 数据库存储，对话内容存储在 Jdbc 数据库中，没有过期时间的持久化存储

## 3、多轮对话 AI 应用开发
### 3.1、初始化 ChatClient 对象。
使用 SpringBoot 的构造器注入方式来注入阿里云大模型 dashscopeChatModel 对象，并使用该对象来初始化 ChatClient
```java
@Component
@Slf4j
public class LoveApp {

    private final ChatClient chatClient;

    private static final String SYSTEM_PROMPT = "扮演深耕恋爱心理领域的专家。开场向用户表明身份，告知用户可倾诉恋爱难题。\n" +
            "围绕单身、恋爱、已婚三种状态提问： \n" +
            "单身状态询问社交圈拓展及追求心仪对象的困扰；\n" +
            "恋爱状态询问沟通、习惯差异引发的矛盾；\n" +
            "已婚状态询问家庭责任与亲属关系处理的问题。\n" +
            "引导用户详述事情经过、对方反应及自身想法，以便给出专属解决方案。";

    public LoveApp(ChatModel dashscopeChatModel) {
        // 初始化基于内存的对话记忆
        ChatMemory chatMemory = new InMemoryChatMemory();
        // 初始化 ChatClient 客户端
        chatClient = ChatClient.builder(dashscopeChatModel)
                // 默认系统提示词 System_Prompt
                .defaultSystem(SYSTEM_PROMPT)
                .defaultAdvisors(
                        new MessageChatMemoryAdvisor(chatMemory)
                )
                .build();
    }
}
```

### 3.2、doChat 对话方法
```java
    /**
     * 对话方法
     * @param message
     * @param chatId
     * @return
     */
    public String doChat(String message, String chatId) {
        ChatResponse chatResponse = chatClient.prompt()
                .user(message)
                .advisors(spec -> spec.param(CHAT_MEMORY_CONVERSATION_ID_KEY, chatId)
                        .param(CHAT_MEMORY_RETRIEVE_SIZE_KEY, 10))
                .call()
                .chatResponse();
        String content = chatResponse.getResult().getOutput().getText();
        log.info("content: {}", content);
        return content;
    }
```