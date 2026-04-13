package com.blog.service;

import com.blog.config.AiConfig.AiProperties;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.*;

@Service
public class AiService {

    private static final Logger log = LoggerFactory.getLogger(AiService.class);

    private static final String SYSTEM_PROMPT =
            "你是一位专业的技术博客作者，擅长写深入浅出的技术文章。\n" +
            "重要规则：\n" +
            "1. 文章必须用 Markdown 格式\n" +
            "2. 开头第一行必须是 ## 标题（一级标题用 ##，不要用 #）\n" +
            "3. 文章内容必须至少500字，包含多个段落\n" +
            "4. 不要只返回一个话题名称，必须写完整的博客文章\n" +
            "5. 你有工具可以调用来分析文章内容并推荐合适的分类和标签";

    private static final String USER_PROMPT_TEMPLATE =
            "请根据以下话题，写一篇完整的专业技术博客文章：\n\n话题：%s\n\n" +
            "要求：\n" +
            "1. 开头用 ## 写标题（必须）\n" +
            "2. 至少500字，分多个段落\n" +
            "3. 技术有深度，有实践价值\n" +
            "4. 全程用中文回答，只返回文章内容，不要任何前缀说明";

    @Autowired
    private AiProperties aiProperties;

    @Autowired
    private McpToolService mcpToolService;

    private final HttpClient httpClient = HttpClient.newBuilder()
            .connectTimeout(Duration.ofSeconds(30))
            .build();

    private final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * 调用 MiniMax Anthropic 兼容接口生成文章内容
     */
    public String generateArticle(String topic) {
        String userPrompt = String.format(USER_PROMPT_TEMPLATE, topic);

        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("model", aiProperties.getModel());
        requestBody.put("max_tokens", 4096);
        requestBody.put("system", SYSTEM_PROMPT);

        List<Map<String, String>> messages = List.of(
                Map.of("role", "user", "content", userPrompt)
        );
        requestBody.put("messages", messages);

        try {
            String jsonBody = objectMapper.writeValueAsString(requestBody);

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(aiProperties.getBaseUrl() + "/v1/messages"))
                    .header("Content-Type", "application/json")
                    .header("x-api-key", aiProperties.getApiKey())
                    .header("anthropic-version", "2023-06-01")
                    .POST(HttpRequest.BodyPublishers.ofString(jsonBody))
                    .timeout(Duration.ofSeconds(120))
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            String content = parseContentFromResponse(response.body());

            // 校验返回内容质量：内容太短说明AI没有正常生成
            if (content != null && content.length() < 50) {
                return "[AI_ERROR] 内容过短：" + content;
            }
            return content;
        } catch (Exception e) {
            return "[AI_ERROR] " + e.getMessage();
        }
    }

    /**
     * 使用 MCP 工具生成文章，自动选择分类和标签
     * @param topic 话题
     * @return 包含文章内容和分类/标签推荐的完整结果
     */
    public Map<String, Object> generateArticleWithMcp(String topic) {
        log.info("[AiService] 使用MCP工具生成文章，话题：{}", topic);

        // 第一步：生成文章内容
        String content = generateArticle(topic);
        if (content.startsWith("[AI_ERROR]") || content.startsWith("[PARSE_ERROR]")) {
            throw new RuntimeException("AI生成内容失败：" + content);
        }

        // 第二步：使用 MCP 工具分析内容并推荐分类和标签
        String fullContent = topic + "\n\n" + content;
        Map<String, Object> recommendation = mcpToolService.analyzeAndRecommend(fullContent);

        log.info("[AiService] MCP推荐结果：categoryId={}, tagIds={}, reason={}",
            recommendation.get("categoryId"),
            recommendation.get("tagIds"),
            recommendation.get("reason"));

        // 第三步：构建返回结果
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("content", content);
        result.put("categoryId", recommendation.get("categoryId"));
        result.put("categoryName", recommendation.get("categoryName"));
        result.put("tagIds", recommendation.get("tagIds"));
        result.put("tagNames", recommendation.get("tagNames"));
        result.put("recommendReason", recommendation.get("reason"));

        return result;
    }

    /**
     * 使用 MCP 工具分析本地项目并生成文章
     * @param projectSummary 项目文件摘要
     * @param userPrompt 用户输入的分析要求
     * @return 包含文章内容和分类/标签推荐的完整结果
     */
    public Map<String, Object> analyzeProjectWithMcp(String projectSummary, String userPrompt) {
        log.info("[AiService] 使用MCP工具分析项目内容");

        String aiPrompt = String.format(
                "你是一位专业的技术博客作者。请分析以下项目文件内容，然后根据用户要求生成一篇高质量的技术文章。\n\n" +
                "【用户要求】\n%s\n\n" +
                "【项目文件内容】\n%s\n\n" +
                "【写作要求】\n" +
                "1. 文章标题要吸引人，能准确反映项目核心内容\n" +
                "2. 内容要深入分析项目的技术特点、架构设计、实现亮点\n" +
                "3. 可以补充项目的适用场景、优缺点、使用建议等\n" +
                "4. 语言专业但易懂，适合技术博客读者\n" +
                "5. 字数 1500-2500 字\n" +
                "6. 使用 Markdown 格式输出\n" +
                "7. 直接输出文章正文，不需要任何说明",
                userPrompt.isEmpty() ? "请全面分析这个项目，撰写一篇技术博客文章" : userPrompt,
                projectSummary
        );

        // 构建请求
        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("model", aiProperties.getModel());
        requestBody.put("max_tokens", 8192);
        requestBody.put("system", SYSTEM_PROMPT);

        List<Map<String, String>> messages = List.of(
                Map.of("role", "user", "content", aiPrompt)
        );
        requestBody.put("messages", messages);

        try {
            String jsonBody = objectMapper.writeValueAsString(requestBody);

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(aiProperties.getBaseUrl() + "/v1/messages"))
                    .header("Content-Type", "application/json")
                    .header("x-api-key", aiProperties.getApiKey())
                    .header("anthropic-version", "2023-06-01")
                    .POST(HttpRequest.BodyPublishers.ofString(jsonBody))
                    .timeout(Duration.ofSeconds(180))
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            String content = parseContentFromResponse(response.body());

            if (content.startsWith("[AI_ERROR]") || content.startsWith("[PARSE_ERROR]")) {
                throw new RuntimeException("AI分析项目内容失败：" + content);
            }

            // 使用 MCP 工具分析内容并推荐分类和标签
            Map<String, Object> recommendation = mcpToolService.analyzeAndRecommend(content);

            log.info("[AiService] 项目分析MCP推荐结果：categoryId={}, tagIds={}",
                recommendation.get("categoryId"),
                recommendation.get("tagIds"));

            // 构建返回结果
            Map<String, Object> result = new LinkedHashMap<>();
            result.put("content", content);
            result.put("categoryId", recommendation.get("categoryId"));
            result.put("categoryName", recommendation.get("categoryName"));
            result.put("tagIds", recommendation.get("tagIds"));
            result.put("tagNames", recommendation.get("tagNames"));
            result.put("recommendReason", recommendation.get("reason"));

            return result;

        } catch (Exception e) {
            log.error("[AiService] MCP分析失败：{}", e.getMessage());
            throw new RuntimeException("MCP分析失败：" + e.getMessage());
        }
    }

    private String parseContentFromResponse(String response) {
        try {
            JsonNode root = objectMapper.readTree(response);
            JsonNode contentArray = root.get("content");
            if (contentArray != null && contentArray.isArray() && !contentArray.isEmpty()) {
                for (JsonNode item : contentArray) {
                    if ("text".equals(item.get("type").asText())) {
                        return item.get("text").asText();
                    }
                }
            }
            if (root.has("type") && "error".equals(root.get("type").asText())) {
                return "[AI_ERROR] " + (root.has("error") ? root.get("error").asText() : "unknown error");
            }
            return response;
        } catch (Exception e) {
            return "[PARSE_ERROR] " + e.getMessage() + "\nRaw: " + response;
        }
    }
}
