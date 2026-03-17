package com.crossborder.productdescription.dto;

import lombok.Data;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.util.List;
import java.util.Map;

/**
 * 产品描述生成请求DTO
 */
@Data
public class DescriptionGenerationRequest {

    /** 产品ID */
    @NotNull(message = "产品ID不能为空")
    private Long productId;

    /** 产品名称 */
    @NotBlank(message = "产品名称不能为空")
    private String productName;

    /** 产品关键词/标签 */
    private List<String> keywords;

    /** 产品特性 */
    private List<String> features;

    /** 目标语言（zh-中文，en-英文，ja-日文，auto-自动检测） */
    private String targetLanguage = "auto";

    /** 目标平台（Amazon、eBay、Shopee等） */
    private String targetPlatform = "general";

    /** 是否启用SEO优化 */
    private Boolean enableSEO = true;

    /** 是否生成多个版本（A/B测试） */
    private Boolean generateMultipleVersions = false;

    /** 生成版本数量 */
    private Integer versionCount = 1;

    /** 描述长度限制（短/中/长） */
    private String length = "medium";

    /** 是否使用模板 */
    private Boolean useTemplate = true;

    /** 模板ID */
    private String templateId;

    /** 自定义提示词 */
    private String customPrompt;

    /** 温度参数（0.0-2.0） */
    private Double temperature = 0.7;

    /** 最大Token数 */
    private Integer maxTokens = 2000;

    /** 是否提炼亮点 */
    private Boolean extractHighlights = true;

    /** 是否生成短标题 */
    private Boolean generateShortTitle = true;
}
