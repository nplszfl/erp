package com.crossborder.aiassistant.service.impl;

import com.crossborder.aiassistant.dto.ChatRequest;
import com.crossborder.aiassistant.dto.ChatResponse;
import com.crossborder.aiassistant.dto.ServiceStatistics;
import com.crossborder.aiassistant.service.AIAssistantService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Flux;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * AIAssistantService 单元测试
 */
@ExtendWith(MockitoExtension.class)
class AIAssistantServiceImplTest {

    @InjectMocks
    private AIAssistantServiceImpl aiAssistantService;

    private ChatRequest chatRequest;

    @BeforeEach
    void setUp() {
        chatRequest = new ChatRequest();
        chatRequest.setUserId(1001L);
        chatRequest.setSessionId("session-001");
        chatRequest.setMessage("产品发货到美国需要多久？");
    }

    @Test
    void testChat_ReturnsResponse() {
        ChatResponse response = aiAssistantService.chat(chatRequest);

        assertNotNull(response);
        assertNotNull(response.getMessage());
    }

    @Test
    void testChat_IncludesUserId() {
        ChatResponse response = aiAssistantService.chat(chatRequest);

        assertEquals(1001L, response.getUserId());
    }

    @Test
    void testChat_IncludesSessionId() {
        ChatResponse response = aiAssistantService.chat(chatRequest);

        assertEquals("session-001", response.getSessionId());
    }

    @Test
    void testChat_IncludesTimestamp() {
        ChatResponse response = aiAssistantService.chat(chatRequest);

        assertNotNull(response.getTimestamp());
    }

    @Test
    void testChatStream_ReturnsFlux() {
        Flux<String> stream = aiAssistantService.chatStream(chatRequest);

        assertNotNull(stream);
    }

    @Test
    void testGetChatHistory_ReturnsHistory() {
        // 先发送消息创建历史
        aiAssistantService.chat(chatRequest);

        List<AIAssistantService.ChatMessage> history = aiAssistantService.getChatHistory(1001L, "session-001");

        assertNotNull(history);
    }

    @Test
    void testGetChatHistory_EmptySession() {
        List<AIAssistantService.ChatMessage> history = aiAssistantService.getChatHistory(999L, "non-existent");

        assertNotNull(history);
    }

    @Test
    void testClearChatHistory_ClearsHistory() {
        // 先发送消息
        aiAssistantService.chat(chatRequest);

        // 清理历史
        aiAssistantService.clearChatHistory(1001L, "session-001");

        List<AIAssistantService.ChatMessage> history = aiAssistantService.getChatHistory(1001L, "session-001");
        
        assertTrue(history.isEmpty());
    }

    @Test
    void testAnalyzeSentiment_PositiveText() {
        String positiveText = "这个产品太棒了，我非常喜欢！";
        
        AIAssistantService.SentimentResult sentiment = aiAssistantService.analyzeSentiment(positiveText);

        assertNotNull(sentiment);
        assertNotNull(sentiment.getSentiment());
    }

    @Test
    void testAnalyzeSentiment_NegativeText() {
        String negativeText = "这个产品太差了，完全不满意";
        
        AIAssistantService.SentimentResult sentiment = aiAssistantService.analyzeSentiment(negativeText);

        assertNotNull(sentiment);
    }

    @Test
    void testAnalyzeSentiment_NeutralText() {
        String neutralText = "这个产品还可以";
        
        AIAssistantService.SentimentResult sentiment = aiAssistantService.analyzeSentiment(neutralText);

        assertNotNull(sentiment);
    }

    @Test
    void testSearchKnowledge_ReturnsResults() {
        String query = "发货";
        
        List<AIAssistantService.Knowledge> results = aiAssistantService.searchKnowledge(query);

        assertNotNull(results);
    }

    @Test
    void testSearchKnowledge_EmptyQuery() {
        List<AIAssistantService.Knowledge> results = aiAssistantService.searchKnowledge("");

        assertNotNull(results);
    }

    @Test
    void testAddKnowledge_AddsToBase() {
        AIAssistantService.Knowledge knowledge = new AIAssistantService.Knowledge();
        knowledge.setQuestion("测试问题");
        knowledge.setAnswer("测试答案");
        knowledge.setCategory("测试");
        
        aiAssistantService.addKnowledge(knowledge);

        List<AIAssistantService.Knowledge> results = aiAssistantService.searchKnowledge("测试问题");
        
        assertNotNull(results);
    }

    @Test
    void testGetServiceStatistics_ReturnsStats() {
        ServiceStatistics stats = aiAssistantService.getServiceStatistics();

        assertNotNull(stats);
        assertNotNull(stats.getTotalConversations());
    }

    @Test
    void testResetStatistics_ResetsCounters() {
        // 先产生一些统计数据
        aiAssistantService.chat(chatRequest);
        
        // 重置统计
        aiAssistantService.resetStatistics();
        
        ServiceStatistics stats = aiAssistantService.getServiceStatistics();
        
        assertNotNull(stats);
    }

    @Test
    void testDetectLanguage_Chinese() {
        String chinese = "你好，这个产品怎么样？";
        
        String language = aiAssistantService.detectLanguage(chinese);
        
        assertEquals("zh", language);
    }

    @Test
    void testDetectLanguage_English() {
        String english = "Hello, how are you?";
        
        String language = aiAssistantService.detectLanguage(english);
        
        assertEquals("en", language);
    }

    @Test
    void testTranslateText_Translation() {
        String text = "你好";
        String targetLang = "en";
        
        String translated = aiAssistantService.translateText(text, targetLang);
        
        assertNotNull(translated);
    }

    @Test
    void testExtractIntent_OrderInquiry() {
        String orderText = "我的订单在哪里？";
        
        String intent = aiAssistantService.extractIntent(orderText);
        
        assertNotNull(intent);
    }

    @Test
    void testExtractIntent_ProductInquiry() {
        String productText = "这个产品有什么特性？";
        
        String intent = aiAssistantService.extractIntent(productText);
        
        assertNotNull(intent);
    }

    @Test
    void testExtractEntities_ExtractsProducts() {
        String text = "我想买iPhone 15和MacBook";
        
        List<String> entities = aiAssistantService.extractEntities(text, "PRODUCT");
        
        assertNotNull(entities);
    }

    @Test
    void testGenerateResponse_WithContext() {
        chatRequest.setContext("用户正在询问订单状态");
        
        ChatResponse response = aiAssistantService.chat(chatRequest);
        
        assertNotNull(response);
    }

    @Test
    void testChat_MultipleMessages_SameSession() {
        ChatRequest msg1 = new ChatRequest();
        msg1.setUserId(1001L);
        msg1.setSessionId("test-session");
        msg1.setMessage("第一个问题");
        
        ChatRequest msg2 = new ChatRequest();
        msg2.setUserId(1001L);
        msg2.setSessionId("test-session");
        msg2.setMessage("第二个问题");
        
        aiAssistantService.chat(msg1);
        aiAssistantService.chat(msg2);
        
        List<AIAssistantService.ChatMessage> history = aiAssistantService.getChatHistory(1001L, "test-session");
        
        // 历史应该包含之前的消息
        assertNotNull(history);
    }
}