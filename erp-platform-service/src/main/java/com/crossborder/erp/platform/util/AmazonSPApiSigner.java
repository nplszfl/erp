package com.crossborder.erp.platform.util;

import com.amazonaws.auth.AWS4Signer;
import com.amazonaws.auth.AWSCredentialsProvider;
import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.http.HttpMethodName;
import com.amazonaws.services.securitytoken.AWSSecurityTokenService;
import com.amazonaws.services.securitytoken.model.Credentials;
import com.amazonaws.util.BinaryUtils;
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
            // 1. 获取临时凭证
            Credentials credentials = getTemporaryCredentials();

            // 2. 获取当前时间
            String datetime = getFormattedDatetime();

            // 3. 计算签名
            String signature = calculateSignature(uri, method, body, datetime, credentials);

            // 4. 构建请求头
            Map<String, String> headers = new HashMap<>();
            headers.put("x-amz-date", datetime.substring(0, 8));
            headers.put("x-amz-security-token", credentials.getSessionToken());
            headers.put("x-amz-access-key-id", credentials.getAccessKeyId());
            headers.put("Authorization", "AWS4-HMAC-SHA256 Credential=" + buildCredentialString(datetime) + ",SignedHeaders=" + getSignedHeaders() + ",Signature=" + signature);

            return headers;
        } catch (Exception e) {
            log.error("签名亚马逊API请求失败", e);
            throw new RuntimeException("签名请求失败", e);
        }
    }

    /**
     * 获取临时STS凭证
     */
    private Credentials getTemporaryCredentials() {
        AWSSecurityTokenService sts = AWSSecurityTokenService.builder()
                .withRoleArn(amazonConfig.getRoleArn())
                .withRoleSessionName("sp-api-session-" + System.currentTimeMillis())
                .withRegion(amazonConfig.getRegion())
                .build();

        return sts.newSessionCredentials();
    }

    /**
     * 获取格式化的时间（YYYYMMDDTHHMMSSZ）
     */
    private String getFormattedDatetime() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd'T'HHmmss'Z'");
        sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
        return sdf.format(new Date());
    }

    /**
     * 计算签名
     */
    private String calculateSignature(String uri, String method, String body, String datetime, Credentials credentials) throws Exception {
        // 1. 规范化URI
        String canonicalUri = normalizeUri(uri);

        // 2. 规范化查询字符串
        String canonicalQueryString = "";

        // 3. 规范化请求头
        String canonicalHeaders = "host:" + "sellingpartnerapi-na.amazon.com" + "\n" +
                                "x-amz-date:" + datetime + "\n";

        // 4. 签名头列表
        String signedHeaders = "host;x-amz-date";

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
        String credentialScope = datetime.substring(0, 8) + "/" + amazonConfig.getRegion() + "/execute-api/aws4_request";
        String stringToSign = "AWS4-HMAC-SHA256\n" + datetime + "\n" + credentialScope + "\n" + sha256Hex(canonicalRequest);

        // 8. 计算签名
        byte[] signingKey = getSignatureKey(credentials.getSecretKey(), datetime.substring(0, 8), amazonConfig.getRegion(), "execute-api");
        byte[] signature = hmacSHA256(signingKey, stringToSign);

        return BinaryUtils.toHex(signature);
    }

    /**
     * 构建凭证字符串
     */
    private String buildCredentialString(String datetime) {
        return amazonConfig.getAwsAccessKeyId() + "/" +
               datetime.substring(0, 8) + "/" +
               amazonConfig.getRegion() + "/execute-api/aws4_request";
    }

    /**
     * 获取签名字符串
     */
    private String getSignedHeaders() {
        return "host;x-amz-date";
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
        return BinaryUtils.toHex(md.digest());
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
