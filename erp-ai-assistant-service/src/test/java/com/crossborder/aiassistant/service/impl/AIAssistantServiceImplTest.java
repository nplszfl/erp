package com.crossborder.aiassistant.service.impl;

import com.crossborder.aiassistant.dto.ChatRequest;
import com.crossborder.aiassistant.dto.ChatResponse;
import com.crossborder.aiassistant.service.AIAssistantService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * AI智能客服服务测试
 */
class AIAssistantServiceImplTest {

    private AIAssistantService aiAssistantService;

    @BeforeEach
    void setUp() {
        aiAssistantService = new AIAssistantServiceImpl();
    }

    @Test
    @DisplayName("测试基础对话功能")
    void testChat_BasicConversation() {
        // 准备测试数据
        ChatRequest request = new ChatRequest();
        request.setUserId(1L);
        request.setMessage("你好");
        request.setUseRAG(true);

        // 执行测试
        ChatResponse response = aiAssistantService.chat(request);

        // 验证结果
        assertNotNull(response);
        assertNotNull(response.getResponseId());
        assertNotNull(response.getSessionId());
        assertNotNull(response.getContent());
        assertNotNull(response.getLanguage());
        assertTrue(response.getResponseTime() > 0);
    }

    @Test
    @DisplayName("测试中文语言检测")
    void testChat_ChineseLanguageDetection() {
        // 准备测试数据
        ChatRequest request = new ChatRequest();
        request.setUserId(1L);
        request.setMessage("我想查询我的订单");

        // 执行测试
        ChatResponse response = aiAssistantService.chat(request);

        // 验证结果
        assertNotNull(response);
        assertEquals("zh", response.getLanguage());
    }

    @Test
    @DisplayName("测试英文语言检测")
    void testChat_EnglishLanguageDetection() {
        // 准备测试数据
        ChatRequest request = new ChatRequest();
        request.setUserId(1L);
        request.setMessage("I want to check my order status");

        // 执行测试
        ChatResponse response = aiAssistantService.chat(request);

        // 验证结果
        assertNotNull(response);
        assertEquals("en", response.getLanguage());
    }

    @Test
    @DisplayName("测试RAG知识检索")
    void testChat_WithRAG() {
        // 准备测试数据
        ChatRequest request = new ChatRequest();
        request.setUserId(1L);
        request.setMessage("如何查询订单");
        request.setUseRAG(true);

        // 执行测试
        ChatResponse response = aiAssistantService.chat(request);

        // 验证结果
        assertNotNull(response);
        assertNotNull(response.getRetrievedChunks());
        assertTrue(response.getRetrievedChunks().size() > 0);
    }

    @Test
    @DisplayName("测试Token使用统计")
    void testChat_TokenUsage() {
        // 准备测试数据
        ChatRequest request = new ChatRequest();
        request.setUserId(1L);
        request.setMessage("产品价格是多少");

        // 执行测试
        ChatResponse response = aiAssistantService.chat(request);

        // 验证结果
        assertNotNull(response);
        assertNotNull(response.getUsage());
        assertTrue(response.getUsage().getPromptTokens() > 0);
        assertTrue(response.getUsage().getCompletionTokens() > 0);
        assertTrue(response.getUsage().getTotalTokens() > 0);
    }

    @Test
    @DisplayName("测试自定义模型")
    void testChat_CustomModel() {
        // 准备测试数据
        ChatRequest request = new ChatRequest();
        request.setUserId(1L);
        request.setMessage("你好");
        request.setModel("gpt-4");

        // 执行测试
        ChatResponse response = aiAssistantService.chat(request);

        // 验证结果
        assertNotNull(response);
        assertEquals("gpt-4", response.getModel());
    }

    @Test
    @DisplayName("测试情感分析 - 正面")
    void testAnalyzeSentiment_Positive() {
        // 执行测试
        AIAssistantService.SentimentAnalysis analysis = aiAssistantService.analyzeSentiment("这个产品非常好，我很满意");

        // 验证结果
        assertNotNull(analysis);
        assertEquals("positive", analysis.getSentiment());
        assertTrue(analysis.getScore() > 0);
    }

    @Test
    @DisplayName("测试情感分析 - 负面")
    void testAnalyzeSentiment_Negative() {
        // 执行测试
        AIAssistantService.SentimentAnalysis analysis = aiAssistantService.analyzeSentiment("这个产品太差了，非常失望");

        // 验证结果
        assertNotNull(analysis);
        assertEquals("negative", analysis.getSentiment());
        assertTrue(analysis.getScore() < 0);
    }

    @Test
    @DisplayName("测试情感分析 - 中性")
    void testAnalyzeSentiment_Neutral() {
        // 执行测试
        AIAssistantService.SentimentAnalysis analysis = aiAssistantService.analyzeSentiment("我想查询订单状态");

        // 验证结果
        assertNotNull(analysis);
        assertEquals("neutral", analysis.getSentiment());
    }

    @Test
    @DisplayName("测试意图识别 - 订单查询")
    void testRecognizeIntent_OrderInquiry() {
        // 执行测试
        AIAssistantService.IntentRecognition recognition = aiAssistantService.recognizeIntent("我想查询我的订单");

        // 验证结果
        assertNotNull(recognition);
        assertEquals("order_inquiry", recognition.getIntent());
        assertTrue(recognition.getConfidence() > 0);
    }

    @Test
    @DisplayName("测试意图识别 - 价格咨询")
    void testRecognizeIntent_PriceInquiry() {
        // 执行测试
        AIAssistantService.IntentRecognition recognition = aiAssistantService.recognizeIntent("这个产品价格多少钱");

        // 验证结果
        assertNotNull(recognition);
        assertEquals("price_inquiry", recognition.getIntent());
        assertTrue(recognition.getConfidence() > 0);
    }

    @Test
    @DisplayName("测试意图识别 - 物流查询")
    void testRecognizeIntent_ShippingInquiry() {
        // 执行测试
        AIAssistantService.IntentRecognition recognition = aiAssistantService.recognizeIntent("我的订单物流信息是什么");

        // 验证结果
        assertNotNull(recognition);
        assertEquals("shipping_inquiry", recognition.getIntent());
    }

    @Test
    @DisplayName("测试意图识别 - 产品咨询")
    void testRecognizeIntent_ProductInquiry() {
        // 执行测试
        AIAssistantService.IntentRecognition recognition = aiAssistantService.recognizeIntent("这个产品的详细信息");

        // 验证结果
        assertNotNull(recognition);
        assertEquals("product_inquiry", recognition.getIntent());
    }

    @Test
    @DisplayName("测试添加知识")
    void testAddKnowledge() {
        // 准备测试数据
        AIAssistantService.Knowledge knowledge = new AIAssistantService.Knowledge();
        knowledge.setCategory("测试分类");
        knowledge.setQuestion("测试问题");
        knowledge.setAnswer("测试答案");
        knowledge.setLanguage("zh");
        knowledge.setTags(List.of("测试", "单元测试"));

        // 执行测试
        aiAssistantService.addKnowledge(knowledge);

        // 验证结果 - 搜索知识库
        List<AIAssistantService.Knowledge> results = aiAssistantService.searchKnowledge("测试问题", 5);
        assertTrue(results.size() > 0);
        assertEquals("测试分类", results.get(0).getCategory());
    }

    @Test
    @DisplayName("测试批量添加知识")
    void testBatchAddKnowledge() {
        // 准备测试数据
        AIAssistantService.Knowledge k1 = new AIAssistantService.Knowledge();
        k1.setCategory("批量测试1");
        k1.setQuestion("批量问题1");
        k1.setAnswer("批量答案1");

        AIAssistantService.Knowledge k2 = new AIAssistantService.Knowledge();
        k2.setCategory("批量测试2");
        k2.setQuestion("批量问题2");
        k2.setAnswer("批量答案2");

        // 执行测试
        aiAssistantService.batchAddKnowledge(List.of(k1, k2));

        // 验证结果
        List<AIAssistantService.Knowledge> results = aiAssistantService.searchKnowledge("批量", 10);
        assertTrue(results.size() >= 2);
    }

    @Test
    @DisplayName("测试知识检索")
    void testSearchKnowledge() {
        // 添加测试知识
        AIAssistantService.Knowledge knowledge = new AIAssistantService.Knowledge();
        knowledge.setCategory("检索测试");
        knowledge.setQuestion("如何申请退款");
        knowledge.setAnswer("可以通过订单详情页申请退款");
        aiAssistantService.addKnowledge(knowledge);

        // 执行测试
        List<AIAssistantService.Knowledge> results = aiAssistantService.searchKnowledge("退款", 5);

        // 验证结果
        assertNotNull(results);
        assertTrue(results.size() > 0);
    }

    @Test
    @DisplayName("测试获取对话历史")
    void testGetChatHistory() {
        Long userId = 1L;
        String sessionId = "test-session-001";

        // 先发起对话
        ChatRequest request = new ChatRequest();
        request.setUserId(userId);
        request.setSessionId(sessionId);
        request.setMessage("测试消息");
        aiAssistantService.chat(request);

        // 执行测试
        List<AIAssistantService.ChatMessage> history = aiAssistantService.getChatHistory(userId, sessionId);

        // 验证结果
        assertNotNull(history);
        assertTrue(history.size() > 0);
    }

    @Test
    @DisplayName("测试清空对话历史")
    void testClearChatHistory() {
        Long userId = 1L;
        String sessionId = "test-session-002";

        // 先发起对话
        ChatRequest request = new ChatRequest();
        request.setUserId(userId);
        request.setSessionId(sessionId);
        request.setMessage("测试消息");
        aiAssistantService.chat(request);

        // 执行测试 - 清空历史
        aiAssistantService.clearChatHistory(userId, sessionId);

        // 验证结果
        List<AIAssistantService.ChatMessage> history = aiAssistantService.getChatHistory(userId, sessionId);
        assertEquals(0, history.size());
    }

    @Test
    @DisplayName("测试多语言回复 - 英文")
    void testChat_EnglishResponse() {
        // 准备测试数据 - 英文问题
        ChatRequest request = new ChatRequest();
        request.setUserId(1L);
        request.setMessage("How to check my order status");

        // 执行测试
        ChatResponse response = aiAssistantService.chat(request);

        // 验证结果
        assertNotNull(response);
        assertNotNull(response.getContent());
    }

    @Test
    @DisplayName("测试错误处理 - 空消息")
    void testChat_EmptyMessage() {
        // 准备测试数据 - 空消息
        ChatRequest request = new ChatRequest();
        request.setUserId(1L);
        request.setMessage("");

        // 执行测试
        ChatResponse response = aiAssistantService.chat(request);

        // 验证结果 - 应该仍然返回响应
        assertNotNull(response);
        assertNotNull(response.getContent());
    }
}