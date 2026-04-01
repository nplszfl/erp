package com.omnitrade.erp.social.client;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.omnitrade.erp.social.config.TikTokApiConfig;
import com.omnitrade.erp.social.model.TikTokShopConfig;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.*;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * TikTok Shop API 客户端
 * 负责与TikTok Open API进行交互
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class TikTokApiClient {
    
    private final TikTokApiConfig apiConfig;
    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;
    
    /**
     * 获取授权URL
     */
    public String getAuthorizationUrl(String state) {
        StringBuilder url = new StringBuilder();
        url.append("https://www.tiktok.com/v2/auth/authorize/");
        url.append("?client_key=").append(apiConfig.getAppKey());
        url.append("&response_type=code");
        url.append("&redirect_uri=").append(apiConfig.getRedirectUri());
        url.append("&scope=video.upload,video.list,user.info.basic,shop.pm.orders");
        url.append("&state=").append(state);
        return url.toString();
    }
    
    /**
     * 通过授权码获取访问令牌
     */
    public TokenResponse getAccessToken(String authorizationCode) {
        String url = apiConfig.getEffectiveBaseUrl() + "/oauth/access_token/";
        
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        
        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add("client_key", apiConfig.getAppKey());
        body.add("client_secret", apiConfig.getAppSecret());
        body.add("code", authorizationCode);
        body.add("grant_type", "authorization_code");
        
        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(body, headers);
        
        try {
            ResponseEntity<String> response = restTemplate.postForEntity(url, request, String.class);
            return parseTokenResponse(response.getBody());
        } catch (Exception e) {
            log.error("获取TikTok访问令牌失败", e);
            throw new RuntimeException("获取访问令牌失败: " + e.getMessage(), e);
        }
    }
    
    /**
     * 刷新访问令牌
     */
    public TokenResponse refreshAccessToken(String refreshToken) {
        String url = apiConfig.getEffectiveBaseUrl() + "/oauth/refresh_token/";
        
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        
        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add("client_key", apiConfig.getAppKey());
        body.add("client_secret", apiConfig.getAppSecret());
        body.add("refresh_token", refreshToken);
        body.add("grant_type", "refresh_token");
        
        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(body, headers);
        
        try {
            ResponseEntity<String> response = restTemplate.postForEntity(url, request, String.class);
            return parseTokenResponse(response.getBody());
        } catch (Exception e) {
            log.error("刷新TikTok访问令牌失败", e);
            throw new RuntimeException("刷新访问令牌失败: " + e.getMessage(), e);
        }
    }
    
    /**
     * 获取店铺列表
     */
    public TikTokShopListResponse getShops(String accessToken) {
        String url = apiConfig.getEffectiveBaseUrl() + "/shop/list/";
        
        HttpHeaders headers = createAuthHeaders(accessToken);
        
        HttpEntity<String> request = new HttpEntity<>(headers);
        
        try {
            ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, request, String.class);
            return objectMapper.readValue(response.getBody(), TikTokShopListResponse.class);
        } catch (Exception e) {
            log.error("获取店铺列表失败", e);
            throw new RuntimeException("获取店铺列表失败: " + e.getMessage(), e);
        }
    }
    
    /**
     * 获取订单列表
     */
    public TikTokOrderListResponse getOrders(String accessToken, String shopId, 
                                              String startTime, String endTime, 
                                              String pageToken, int pageSize) {
        String url = apiConfig.getEffectiveBaseUrl() + "/order/list/";
        
        HttpHeaders headers = createAuthHeaders(accessToken);
        
        Map<String, Object> bodyMap = new HashMap<>();
        bodyMap.put("shop_id", shopId);
        if (startTime != null) bodyMap.put("create_time_from", startTime);
        if (endTime != null) bodyMap.put("create_time_to", endTime);
        if (pageToken != null) bodyMap.put("page_token", pageToken);
        bodyMap.put("page_size", pageSize);
        
        HttpEntity<Map<String, Object>> request = new HttpEntity<>(bodyMap, headers);
        
        try {
            ResponseEntity<String> response = restTemplate.postForEntity(url, request, String.class);
            return objectMapper.readValue(response.getBody(), TikTokOrderListResponse.class);
        } catch (Exception e) {
            log.error("获取订单列表失败", e);
            throw new RuntimeException("获取订单列表失败: " + e.getMessage(), e);
        }
    }
    
    /**
     * 获取订单详情
     */
    public TikTokOrderDetailResponse getOrderDetail(String accessToken, String shopId, String orderId) {
        String url = apiConfig.getEffectiveBaseUrl() + "/order/detail/";
        
        HttpHeaders headers = createAuthHeaders(accessToken);
        
        Map<String, Object> bodyMap = new HashMap<>();
        bodyMap.put("shop_id", shopId);
        bodyMap.put("order_id", orderId);
        
        HttpEntity<Map<String, Object>> request = new HttpEntity<>(bodyMap, headers);
        
        try {
            ResponseEntity<String> response = restTemplate.postForEntity(url, request, String.class);
            return objectMapper.readValue(response.getBody(), TikTokOrderDetailResponse.class);
        } catch (Exception e) {
            log.error("获取订单详情失败, orderId: {}", orderId, e);
            throw new RuntimeException("获取订单详情失败: " + e.getMessage(), e);
        }
    }
    
    /**
     * 获取商品列表
     */
    public TikTokProductListResponse getProducts(String accessToken, String shopId, 
                                                   String pageToken, int pageSize) {
        String url = apiConfig.getEffectiveBaseUrl() + "/product/list/";
        
        HttpHeaders headers = createAuthHeaders(accessToken);
        
        Map<String, Object> bodyMap = new HashMap<>();
        bodyMap.put("shop_id", shopId);
        if (pageToken != null) bodyMap.put("page_token", pageToken);
        bodyMap.put("page_size", pageSize);
        
        HttpEntity<Map<String, Object>> request = new HttpEntity<>(bodyMap, headers);
        
        try {
            ResponseEntity<String> response = restTemplate.postForEntity(url, request, String.class);
            return objectMapper.readValue(response.getBody(), TikTokProductListResponse.class);
        } catch (Exception e) {
            log.error("获取商品列表失败", e);
            throw new RuntimeException("获取商品列表失败: " + e.getMessage(), e);
        }
    }
    
    /**
     * 获取商品详情
     */
    public TikTokProductDetailResponse getProductDetail(String accessToken, String shopId, String productId) {
        String url = apiConfig.getEffectiveBaseUrl() + "/product/detail/";
        
        HttpHeaders headers = createAuthHeaders(accessToken);
        
        Map<String, Object> bodyMap = new HashMap<>();
        bodyMap.put("shop_id", shopId);
        bodyMap.put("product_id", productId);
        
        HttpEntity<Map<String, Object>> request = new HttpEntity<>(bodyMap, headers);
        
        try {
            ResponseEntity<String> response = restTemplate.postForEntity(url, request, String.class);
            return objectMapper.readValue(response.getBody(), TikTokProductDetailResponse.class);
        } catch (Exception e) {
            log.error("获取商品详情失败, productId: {}", productId, e);
            throw new RuntimeException("获取商品详情失败: " + e.getMessage(), e);
        }
    }
    
    /**
     * 创建商品（用于直播挂载）
     */
    public TikTokProductCreateResponse createProduct(String accessToken, String shopId, Map<String, Object> productInfo) {
        String url = apiConfig.getEffectiveBaseUrl() + "/product/create/";
        
        HttpHeaders headers = createAuthHeaders(accessToken);
        
        Map<String, Object> bodyMap = new HashMap<>();
        bodyMap.put("shop_id", shopId);
        bodyMap.put("product", productInfo);
        
        HttpEntity<Map<String, Object>> request = new HttpEntity<>(bodyMap, headers);
        
        try {
            ResponseEntity<String> response = restTemplate.postForEntity(url, request, String.class);
            return objectMapper.readValue(response.getBody(), TikTokProductCreateResponse.class);
        } catch (Exception e) {
            log.error("创建商品失败", e);
            throw new RuntimeException("创建商品失败: " + e.getMessage(), e);
        }
    }
    
    /**
     * 获取直播状态
     */
    public TikTokLiveStatusResponse getLiveStatus(String accessToken, String shopId) {
        String url = apiConfig.getEffectiveBaseUrl() + "/live/status/";
        
        HttpHeaders headers = createAuthHeaders(accessToken);
        
        Map<String, Object> bodyMap = new HashMap<>();
        bodyMap.put("shop_id", shopId);
        
        HttpEntity<Map<String, Object>> request = new HttpEntity<>(bodyMap, headers);
        
        try {
            ResponseEntity<String> response = restTemplate.postForEntity(url, request, String.class);
            return objectMapper.readValue(response.getBody(), TikTokLiveStatusResponse.class);
        } catch (Exception e) {
            log.error("获取直播状态失败", e);
            throw new RuntimeException("获取直播状态失败: " + e.getMessage(), e);
        }
    }
    
    /**
     * 获取直播数据
     */
    public TikTokLiveStatsResponse getLiveStats(String accessToken, String shopId, String liveId) {
        String url = apiConfig.getEffectiveBaseUrl() + "/live/stats/";
        
        HttpHeaders headers = createAuthHeaders(accessToken);
        
        Map<String, Object> bodyMap = new HashMap<>();
        bodyMap.put("shop_id", shopId);
        bodyMap.put("live_id", liveId);
        
        HttpEntity<Map<String, Object>> request = new HttpEntity<>(bodyMap, headers);
        
        try {
            ResponseEntity<String> response = restTemplate.postForEntity(url, request, String.class);
            return objectMapper.readValue(response.getBody(), TikTokLiveStatsResponse.class);
        } catch (Exception e) {
            log.error("获取直播数据失败, liveId: {}", liveId, e);
            throw new RuntimeException("获取直播数据失败: " + e.getMessage(), e);
        }
    }
    
    /**
     * 挂载商品到直播
     */
    public TikTokLiveProductResponse addProductToLive(String accessToken, String shopId, 
                                                       String liveId, String productId) {
        String url = apiConfig.getEffectiveBaseUrl() + "/live/product/add/";
        
        HttpHeaders headers = createAuthHeaders(accessToken);
        
        Map<String, Object> bodyMap = new HashMap<>();
        bodyMap.put("shop_id", shopId);
        bodyMap.put("live_id", liveId);
        bodyMap.put("product_id", productId);
        
        HttpEntity<Map<String, Object>> request = new HttpEntity<>(bodyMap, headers);
        
        try {
            ResponseEntity<String> response = restTemplate.postForEntity(url, request, String.class);
            return objectMapper.readValue(response.getBody(), TikTokLiveProductResponse.class);
        } catch (Exception e) {
            log.error("挂载商品到直播失败, liveId: {}, productId: {}", liveId, productId, e);
            throw new RuntimeException("挂载商品到直播失败: " + e.getMessage(), e);
        }
    }
    
    /**
     * 从直播移除商品
     */
    public TikTokLiveProductResponse removeProductFromLive(String accessToken, String shopId, 
                                                            String liveId, String productId) {
        String url = apiConfig.getEffectiveBaseUrl() + "/live/product/remove/";
        
        HttpHeaders headers = createAuthHeaders(accessToken);
        
        Map<String, Object> bodyMap = new HashMap<>();
        bodyMap.put("shop_id", shopId);
        bodyMap.put("live_id", liveId);
        bodyMap.put("product_id", productId);
        
        HttpEntity<Map<String, Object>> request = new HttpEntity<>(bodyMap, headers);
        
        try {
            ResponseEntity<String> response = restTemplate.postForEntity(url, request, String.class);
            return objectMapper.readValue(response.getBody(), TikTokLiveProductResponse.class);
        } catch (Exception e) {
            log.error("从直播移除商品失败, liveId: {}, productId: {}", liveId, productId, e);
            throw new RuntimeException("从直播移除商品失败: " + e.getMessage(), e);
        }
    }
    
    private HttpHeaders createAuthHeaders(String accessToken) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(accessToken);
        return headers;
    }
    
    private TokenResponse parseTokenResponse(String responseBody) {
        try {
            JsonNode root = objectMapper.readTree(responseBody);
            TokenResponse response = new TokenResponse();
            response.setAccessToken(root.path("data").path("access_token").asText());
            response.setRefreshToken(root.path("data").path("refresh_token").asText());
            response.setExpiresIn(root.path("data").path("expires_in").asLong());
            response.setTokenType(root.path("data").path("token_type").asText());
            
            String openId = root.path("data").path("open_id").asText();
            response.setOpenId(openId);
            
            return response;
        } catch (Exception e) {
            log.error("解析Token响应失败", e);
            throw new RuntimeException("解析Token响应失败: " + e.getMessage(), e);
        }
    }
    
    // ========== 内部类定义 ==========
    
    @lombok.Data
    public static class TokenResponse {
        private String accessToken;
        private String refreshToken;
        private Long expiresIn;
        private String tokenType;
        private String openId;
    }
    
    @lombok.Data
    public static class TikTokShopListResponse {
        private String errorCode;
        private String errorMessage;
        private TikTokShopListData data;
    }
    
    @lombok.Data
    public static class TikTokShopListData {
        private List<TikTokShop> shops;
        private String pageToken;
        private int totalCount;
    }
    
    @lombok.Data
    public static class TikTokShop {
        private String shopId;
        private String shopName;
        private String region;
        private String status;
    }
    
    @lombok.Data
    public static class TikTokOrderListResponse {
        private String errorCode;
        private String errorMessage;
        private TikTokOrderListData data;
    }
    
    @lombok.Data
    public static class TikTokOrderListData {
        private List<Map<String, Object>> orders;
        private String pageToken;
        private int totalCount;
    }
    
    @lombok.Data
    public static class TikTokOrderDetailResponse {
        private String errorCode;
        private String errorMessage;
        private Map<String, Object> data;
    }
    
    @lombok.Data
    public static class TikTokProductListResponse {
        private String errorCode;
        private String errorMessage;
        private TikTokProductListData data;
    }
    
    @lombok.Data
    public static class TikTokProductListData {
        private List<Map<String, Object>> products;
        private String pageToken;
        private int totalCount;
    }
    
    @lombok.Data
    public static class TikTokProductDetailResponse {
        private String errorCode;
        private String errorMessage;
        private Map<String, Object> data;
    }
    
    @lombok.Data
    public static class TikTokProductCreateResponse {
        private String errorCode;
        private String errorMessage;
        private TikTokProductCreateData data;
    }
    
    @lombok.Data
    public static class TikTokProductCreateData {
        private String productId;
    }
    
    @lombok.Data
    public static class TikTokLiveStatusResponse {
        private String errorCode;
        private String errorMessage;
        private TikTokLiveStatusData data;
    }
    
    @lombok.Data
    public static class TikTokLiveStatusData {
        private String liveId;
        private String status; // SCHEDULED, LIVE, ENDED
        private String scheduledStartTime;
        private String actualStartTime;
        private String endTime;
    }
    
    @lombok.Data
    public static class TikTokLiveStatsResponse {
        private String errorCode;
        private String errorMessage;
        private TikTokLiveStatsData data;
    }
    
    @lombok.Data
    public static class TikTokLiveStatsData {
        private int viewerCount;
        private int peakViewers;
        private int likeCount;
        private int shareCount;
        private int commentCount;
        private int newFollowers;
        private double totalSales;
        private int totalOrders;
        private double totalGmv;
    }
    
    @lombok.Data
    public static class TikTokLiveProductResponse {
        private String errorCode;
        private String errorMessage;
        private TikTokLiveProductData data;
    }
    
    @lombok.Data
    public static class TikTokLiveProductData {
        private String liveId;
        private String productId;
        private String status;
    }
}