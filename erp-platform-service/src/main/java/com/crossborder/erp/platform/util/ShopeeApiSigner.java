package com.crossborder.erp.platform.util;

import com.crossborder.erp.platform.config.ShopeeConfig;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Shopee API签名工具类
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class ShopeeApiSigner {

    private final ShopeeConfig shopeeConfig;

    /**
     * 生成Shopee API签名
     * @param path API路径
     * @param params 请求参数
     * @return 签名后的参数Map（包含signature）
     */
    public Map<String, String> signRequest(String path, Map<String, String> params) {
        try {
            // 1. 添加必需参数
            Map<String, String> allParams = new TreeMap<>(params);
            allParams.put("partner_id", shopeeConfig.getPartnerId().toString());
            allParams.put("timestamp", getCurrentTimestamp());

            // 2. 构建待签名字符串
            String stringToSign = buildStringToSign(path, allParams);

            log.debug("待签名字符串: {}", stringToSign);

            // 3. 计算HMAC-SHA256签名
            String signature = calculateSignature(stringToSign);

            // 4. 添加签名到参数中
            allParams.put("sign", signature);

            return allParams;

        } catch (Exception e) {
            log.error("Shopee API签名失败", e);
            throw new RuntimeException("签名失败", e);
        }
    }

    /**
     * 构建待签名字符串
     * 格式：partner_id + timestamp + access_key + shop_id + path + 所有参数按key排序拼接
     */
    private String buildStringToSign(String path, Map<String, String> params) {
        StringBuilder sb = new StringBuilder();

        // partner_id
        sb.append(shopeeConfig.getPartnerId());

        // timestamp
        String timestamp = params.get("timestamp");
        if (timestamp != null) {
            sb.append(timestamp);
        }

        // access_key
        sb.append(shopeeConfig.getApiKey());

        // shop_id
        sb.append(shopeeConfig.getShopId());

        // path（移除查询参数）
        String cleanPath = path.split("\\?")[0];
        sb.append(cleanPath);

        // 其他参数按key排序
        for (Map.Entry<String, String> entry : params.entrySet()) {
            String key = entry.getKey();
            String value = entry.getValue();

            // 跳过已经处理的参数
            if ("partner_id".equals(key) || "timestamp".equals(key) ||
                "sign".equals(key)) {
                continue;
            }

            sb.append(key).append(value);
        }

        return sb.toString();
    }

    /**
     * 计算HMAC-SHA256签名
     */
    private String calculateSignature(String data) throws Exception {
        // 将API Key作为密钥
        byte[] keyBytes = shopeeConfig.getApiKey().getBytes(StandardCharsets.UTF_8);

        // 创建HMAC-SHA256
        Mac mac = Mac.getInstance("HmacSHA256");
        SecretKeySpec secretKeySpec = new SecretKeySpec(keyBytes, "HmacSHA256");
        mac.init(secretKeySpec);

        // 计算签名
        byte[] signatureBytes = mac.doFinal(data.getBytes(StandardCharsets.UTF_8));

        // 转换为十六进制字符串
        return bytesToHex(signatureBytes).toLowerCase();
    }

    /**
     * 字节数组转十六进制字符串
     */
    private String bytesToHex(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        for (byte b : bytes) {
            String hex = Integer.toHexString(b & 0xFF);
            if (hex.length() == 1) {
                sb.append('0');
            }
            sb.append(hex);
        }
        return sb.toString();
    }

    /**
     * 获取当前时间戳（Unix时间戳）
     */
    private String getCurrentTimestamp() {
        return String.valueOf(System.currentTimeMillis() / 1000);
    }
}
