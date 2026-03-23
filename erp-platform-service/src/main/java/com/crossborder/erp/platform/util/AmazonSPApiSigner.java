package com.crossborder.erp.platform.util;

import com.crossborder.erp.platform.config.AmazonConfig;
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
 * 亚马逊SP-API签名工具类
 * 使用AWS Signature Version 4签名
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class AmazonSPApiSigner {

    private final AmazonConfig amazonConfig;

    /**
     * 签名HTTP请求
     */
    public Map<String, String> signRequest(String uri, String method, String body) {
        try {
            // 1. 获取当前时间
            String datetime = getFormattedDatetime();
            String dateStamp = datetime.substring(0, 8);

            // 2. 使用静态凭证（用于开发环境）
            String accessKeyId = amazonConfig.getAwsAccessKeyId();
            String secretKey = amazonConfig.getAwsSecretKey();
            String region = amazonConfig.getRegion();

            // 3. 计算签名
            String signature = calculateSignature(uri, method, body, datetime, dateStamp, accessKeyId, secretKey, region);

            // 4. 构建请求头
            Map<String, String> headers = new HashMap<>();
            headers.put("x-amz-date", datetime);
            headers.put("Content-Type", "application/json");
            headers.put("Authorization", "AWS4-HMAC-SHA256 " +
                    "Credential=" + accessKeyId + "/" + dateStamp + "/" + region + "/execute-api/aws4_request," +
                    "SignedHeaders=content-type;host;x-amz-date," +
                    "Signature=" + signature);

            return headers;
        } catch (Exception e) {
            log.error("签名亚马逊API请求失败", e);
            throw new RuntimeException("签名请求失败", e);
        }
    }

    /**
     * 获取格式化的时间（YYYYMMDD'T'HHMMSS'Z'）
     */
    private String getFormattedDatetime() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd'T'HHmmss'Z'");
        sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
        return sdf.format(new Date());
    }

    /**
     * 计算签名
     */
    private String calculateSignature(String uri, String method, String body, String datetime, 
                                       String dateStamp, String accessKeyId, String secretKey, String region) throws Exception {
        
        // 1. 规范化URI
        String canonicalUri = normalizeUri(uri);

        // 2. 规范化查询字符串
        String canonicalQueryString = getCanonicalQueryString(uri);

        // 3. 规范化请求头
        String host = amazonConfig.getApiBaseUrl().replace("https://", "").replace("http://", "");
        String canonicalHeaders = "content-type:application/json\n" +
                                 "host:" + host + "\n" +
                                 "x-amz-date:" + datetime + "\n";

        // 4. 签名头列表
        String signedHeaders = "content-type;host;x-amz-date";

        // 5. 计算请求体哈希
        String payloadHash = sha256Hex(body);

        // 6. 构建规范请求
        String canonicalRequest = method + "\n" +
                                 canonicalUri + "\n" +
                                 canonicalQueryString + "\n" +
                                 canonicalHeaders + "\n" +
                                 signedHeaders + "\n" +
                                 payloadHash;

        log.debug("规范请求:\n{}", canonicalRequest);

        // 7. 计算待签字符串
        String credentialScope = dateStamp + "/" + region + "/execute-api/aws4_request";
        String stringToSign = "AWS4-HMAC-SHA256\n" + datetime + "\n" + credentialScope + "\n" + sha256Hex(canonicalRequest);

        log.debug("待签字符串:\n{}", stringToSign);

        // 8. 计算签名
        byte[] signingKey = getSignatureKey(secretKey, dateStamp, region, "execute-api");
        byte[] signature = hmacSHA256(signingKey, stringToSign);

        return bytesToHex(signature);
    }

    /**
     * 获取规范化查询字符串
     */
    private String getCanonicalQueryString(String uri) {
        StringBuilder queryString = new StringBuilder();
        int queryIndex = uri.indexOf('?');
        if (queryIndex > 0) {
            String query = uri.substring(queryIndex + 1);
            String[] params = query.split("&");
            Arrays.sort(params);
            for (int i = 0; i < params.length; i++) {
                if (i > 0) {
                    queryString.append("&");
                }
                queryString.append(params[i]);
            }
        }
        return queryString.toString();
    }

    /**
     * 规范化URI
     */
    private String normalizeUri(String uri) {
        // 移除query参数
        int queryIndex = uri.indexOf('?');
        if (queryIndex > 0) {
            uri = uri.substring(0, queryIndex);
        }
        return uri;
    }

    /**
     * SHA256哈希并转为十六进制
     */
    private String sha256Hex(String text) throws Exception {
        MessageDigest md = MessageDigest.getInstance("SHA-256");
        md.update(text.getBytes(StandardCharsets.UTF_8));
        return bytesToHex(md.digest());
    }

    /**
     * bytes转十六进制
     */
    private String bytesToHex(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        for (byte b : bytes) {
            sb.append(String.format("%02x", b));
        }
        return sb.toString();
    }

    /**
     * HMAC-SHA256
     */
    private byte[] hmacSHA256(byte[] key, String data) throws Exception {
        Mac mac = Mac.getInstance("HmacSHA256");
        SecretKeySpec secretKeySpec = new SecretKeySpec(key, "HmacSHA256");
        mac.init(secretKeySpec);
        return mac.doFinal(data.getBytes(StandardCharsets.UTF_8));
    }

    /**
     * 获取签名密钥
     */
    private byte[] getSignatureKey(String key, String date, String region, String service) throws Exception {
        byte[] kDate = hmacSHA256(("AWS4" + key).getBytes(StandardCharsets.UTF_8), date);
        byte[] kRegion = hmacSHA256(kDate, region);
        byte[] kService = hmacSHA256(kRegion, service);
        return hmacSHA256(kService, "aws4_request");
    }
}