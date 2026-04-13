package com.blog.service;

import com.blog.config.AiConfig.SchedulerProperties;
import com.blog.common.exception.BusinessException;
import com.blog.entity.Article;
import com.blog.mapper.ArticleMapper;
import com.blog.service.ArticleService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.*;

@Service
public class AutoPublishService {

    private static final Logger log = LoggerFactory.getLogger(AutoPublishService.class);

    private static final int SUMMARY_LENGTH = 200;

    @Autowired
    private AiService aiService;

    @Autowired
    private HotTopicService hotTopicService;

    @Autowired
    private ArticleService articleService;

    @Autowired
    private ArticleMapper articleMapper;

    @Autowired
    private SchedulerProperties schedulerProperties;

    @Autowired
    private AiSettingsService aiSettingsService;

    @Value("${auto-publish.admin-user-id:1}")
    private Long defaultAdminUserId;

    // ========== AI 文章生成 ==========

    /**
     * 根据指定话题生成文章（独立功能）
     * @param topic 话题
     * @return 生成的文章ID
     */
    public Long generateArticle(String topic) {
        log.info("[AutoPublish] 开始生成文章，话题：{}", topic);
        String content = aiService.generateArticle(topic);
        if (content == null || content.isBlank()) {
            throw new BusinessException("AI 生成内容为空");
        }
        String title = extractTitle(content);
        String summary = extractSummary(content);
        Long articleId = articleService.createAiArticle(title, summary, content, defaultAdminUserId, 0);
        log.info("[AutoPublish] 文章生成成功，ID：{}，标题：{}", articleId, title);
        return articleId;
    }

    /**
     * 根据话题生成文章并返回完整内容（用于预览）
     * 使用 MCP 工具自动选择分类和标签
     * @param topic 话题
     * @return 完整文章数据（id, title, summary, content, status, categoryId, categoryName, tagIds, tagNames）
     */
    public Map<String, Object> generateArticleWithContent(String topic) {
        log.info("[AutoPublish] 生成文章（预览模式），话题：{}", topic);

        // 使用 MCP 工具生成文章并自动选择分类和标签
        Map<String, Object> mcpResult = aiService.generateArticleWithMcp(topic);
        String content = (String) mcpResult.get("content");
        Long categoryId = mcpResult.get("categoryId") != null ? ((Number) mcpResult.get("categoryId")).longValue() : null;
        List<Long> tagIds = mcpResult.get("tagIds") != null ?
            ((List<?>) mcpResult.get("tagIds")).stream().map(t -> ((Number) t).longValue()).toList() : null;

        // 检查是否自动发布
        boolean autoPublish = aiSettingsService.getBooleanValue(AiSettingsService.AI_AUTO_PUBLISH, false);
        int status = autoPublish ? 1 : 0;

        String title = extractTitle(content);
        String summary = extractSummary(content);
        Long articleId = articleService.createAiArticle(title, summary, content, defaultAdminUserId, status, categoryId, tagIds);
        log.info("[AutoPublish] 文章生成成功，ID：{}，标题：{}，分类：{}，标签：{}，状态：{}",
            articleId, title, mcpResult.get("categoryName"), mcpResult.get("tagNames"), autoPublish ? "已发布" : "草稿");

        Map<String, Object> result = new LinkedHashMap<>();
        result.put("articleId", articleId);
        result.put("title", title);
        result.put("summary", summary);
        result.put("content", content);
        result.put("topic", topic);
        result.put("status", status);
        result.put("categoryId", categoryId);
        result.put("categoryName", mcpResult.get("categoryName"));
        result.put("tagIds", tagIds);
        result.put("tagNames", mcpResult.get("tagNames"));
        result.put("recommendReason", mcpResult.get("recommendReason"));
        result.put("autoPublish", autoPublish);
        return result;
    }

    /**
     * 总结今日热点：将所有热点新闻汇总成一篇综述长文
     * 使用 MCP 工具自动选择分类和标签
     * @return 直接返回文章数据
     */
    public Map<String, Object> generateSummaryArticle() {
        log.info("[AutoPublish] 开始生成热点综述文章");
        List<String> allTopics = hotTopicService.getAllHotTopics();
        if (allTopics.isEmpty()) {
            throw new BusinessException("未能获取到热点话题，请稍后重试");
        }
        // 限制最多 50 个话题
        List<String> selected = allTopics.size() > 50
                ? allTopics.subList(0, 50)
                : allTopics;
        String topicsText = String.join("\n", selected);

        String prompt = String.format(
                "请阅读以下今日全网热点新闻列表，将它们整合成一篇内容丰富、观点鲜明的综述文章：\n\n%s\n\n" +
                "写作要求：\n" +
                "1. 起一个吸引人的标题\n" +
                "2. 分章节撰写，每章节聚焦一类话题（科技/社会/国际/娱乐等）\n" +
                "3. 分析话题背后的原因、趋势和影响\n" +
                "4. 语言流畅，逻辑清晰，适合博客读者\n" +
                "5. 字数 1500-2500 字\n" +
                "6. 直接输出文章正文，不需要任何说明",
                topicsText
        );

        // 使用 MCP 工具生成文章并自动选择分类和标签
        Map<String, Object> mcpResult = aiService.generateArticleWithMcp(prompt);
        String content = (String) mcpResult.get("content");
        Long categoryId = mcpResult.get("categoryId") != null ? ((Number) mcpResult.get("categoryId")).longValue() : null;
        List<Long> tagIds = mcpResult.get("tagIds") != null ?
            ((List<?>) mcpResult.get("tagIds")).stream().map(t -> ((Number) t).longValue()).toList() : null;

        // 检查是否自动发布
        boolean autoPublish = aiSettingsService.getBooleanValue(AiSettingsService.AI_AUTO_PUBLISH, false);
        int status = autoPublish ? 1 : 0;

        String title = extractTitle(content);
        String summary = extractSummary(content);
        Long articleId = articleService.createAiArticle(title, summary, content, defaultAdminUserId, status, categoryId, tagIds);
        log.info("[AutoPublish] 综述文章生成成功，ID：{}，标题：{}，分类：{}，状态：{}",
            articleId, title, mcpResult.get("categoryName"), autoPublish ? "已发布" : "草稿");

        Map<String, Object> result = new LinkedHashMap<>();
        result.put("articleId", articleId);
        result.put("title", title);
        result.put("summary", summary);
        result.put("content", content);
        result.put("topic", "今日热点总结");
        result.put("status", status);
        result.put("categoryId", categoryId);
        result.put("categoryName", mcpResult.get("categoryName"));
        result.put("tagIds", tagIds);
        result.put("tagNames", mcpResult.get("tagNames"));
        result.put("recommendReason", mcpResult.get("recommendReason"));
        result.put("autoPublish", autoPublish);
        return result;
    }

    /**
     * 按方向生成文章（从指定平台获取热点）
     * @param direction 方向（如 weibo, zhihu, baidu 等）
     * @return 完整文章数据
     */
    public Map<String, Object> generateByDirection(String direction) {
        log.info("[AutoPublish] 按方向生成文章，方向：{}", direction);
        List<String> topics = hotTopicService.getTopicsByPlatform(direction);
        if (topics.isEmpty()) {
            throw new BusinessException("该方向暂无热点话题：" + direction);
        }
        String selectedTopic = topics.get(0);
        return generateArticleWithContent(selectedTopic);
    }

    /**
     * 批量生成文章（每个话题一篇）
     * @param topics 话题列表
     * @param maxCount 最大生成数量
     * @return 生成的草稿文章列表
     */
    public List<Map<String, Object>> batchGenerateArticles(List<String> topics, int maxCount) {
        if (topics == null || topics.isEmpty()) {
            throw new BusinessException("话题列表为空");
        }
        List<String> selected = topics.size() > maxCount ? topics.subList(0, maxCount) : topics;
        log.info("[AutoPublish] 批量生成文章，话题数：{}，上限：{}", selected.size(), maxCount);

        // 并行生成（线程池大小 5，避免 AI 接口过载）
        ExecutorService executor = Executors.newFixedThreadPool(5);
        List<Future<Map<String, Object>>> futures = new ArrayList<>();
        for (String topic : selected) {
            futures.add(executor.submit(() -> {
                try {
                    return generateArticleWithContent(topic);
                } catch (Exception e) {
                    log.warn("[AutoPublish] 话题生成失败：{}，错误：{}", topic, e.getMessage());
                    return Map.of(
                            "topic", topic,
                            "articleId", -1L,
                            "title", "[生成失败] " + topic,
                            "summary", "",
                            "content", "",
                            "status", -1,
                            "error", e.getMessage()
                    );
                }
            }));
        }

        List<Map<String, Object>> results = new ArrayList<>();
        for (Future<Map<String, Object>> f : futures) {
            try {
                results.add(f.get(90, TimeUnit.SECONDS));
            } catch (Exception e) {
                log.warn("[AutoPublish] 批量任务获取结果失败：{}", e.getMessage());
            }
        }
        executor.shutdown();
        log.info("[AutoPublish] 批量生成完成，成功 {} 篇", results.stream().filter(r -> (long) r.get("articleId") > 0).count());
        return results;
    }

    /**
     * 分析本地项目并生成文章
     * 使用 MCP 工具自动选择分类和标签
     * @param files 文件列表 [{name, path, content}, ...]
     * @param prompt 用户输入的分析要求
     * @return 完整文章数据
     */
    public Map<String, Object> analyzeLocalProject(List<Map<String, String>> files, String prompt) {
        log.info("[AutoPublish] 开始分析本地项目，文件数：{}，要求：{}", files.size(), prompt);

        // 构建项目文件摘要
        StringBuilder fileSummary = new StringBuilder();
        for (Map<String, String> file : files) {
            String name = file.getOrDefault("name", "unknown");
            String path = file.getOrDefault("path", "");
            String content = file.getOrDefault("content", "");

            // 截断每个文件内容，最多保留 3000 字符
            String truncatedContent = content.length() > 3000
                    ? content.substring(0, 3000) + "\n... (内容已截断) ..."
                    : content;

            fileSummary.append("=== 文件: ").append(path.isEmpty() ? name : path).append(" ===\n");
            fileSummary.append(truncatedContent).append("\n\n");
        }

        // 使用 MCP 工具生成文章并自动选择分类和标签
        Map<String, Object> mcpResult = aiService.analyzeProjectWithMcp(fileSummary.toString(), prompt);
        String content = (String) mcpResult.get("content");
        Long categoryId = mcpResult.get("categoryId") != null ? ((Number) mcpResult.get("categoryId")).longValue() : null;
        List<Long> tagIds = mcpResult.get("tagIds") != null ?
            ((List<?>) mcpResult.get("tagIds")).stream().map(t -> ((Number) t).longValue()).toList() : null;

        // 检查是否自动发布
        boolean autoPublish = aiSettingsService.getBooleanValue(AiSettingsService.AI_AUTO_PUBLISH, false);
        int status = autoPublish ? 1 : 0;

        String title = extractTitle(content);
        String summary = extractSummary(content);
        Long articleId = articleService.createAiArticle(title, summary, content, defaultAdminUserId, status, categoryId, tagIds);
        log.info("[AutoPublish] 项目分析文章生成成功，ID：{}，标题：{}，分类：{}，状态：{}",
            articleId, title, mcpResult.get("categoryName"), autoPublish ? "已发布" : "草稿");

        Map<String, Object> result = new LinkedHashMap<>();
        result.put("articleId", articleId);
        result.put("title", title);
        result.put("summary", summary);
        result.put("content", content);
        result.put("topic", "本地项目分析");
        result.put("status", status);
        result.put("categoryId", categoryId);
        result.put("categoryName", mcpResult.get("categoryName"));
        result.put("tagIds", tagIds);
        result.put("tagNames", mcpResult.get("tagNames"));
        result.put("recommendReason", mcpResult.get("recommendReason"));
        result.put("autoPublish", autoPublish);
        return result;
    }

    // ========== 定时任务工作流 ==========

    /**
     * 执行完整定时发布工作流
     * 获取热点 → 选择话题 → AI 生成 → 保存草稿或发布
     * @return 执行结果（文章列表、状态）
     */
    public Map<String, Object> executeWorkflow() {
        log.info("[AutoPublish] 开始执行自动发文工作流");

        // 检查定时任务是否启用
        boolean schedulerEnabled = aiSettingsService.getBooleanValue(AiSettingsService.SCHEDULER_ENABLED, true);
        if (!schedulerEnabled) {
            throw new BusinessException("定时任务未启用");
        }

        // 1. AI 获取热点话题
        List<String> allTopics = hotTopicService.getAllHotTopics();
        if (allTopics.isEmpty()) {
            throw new BusinessException("未能获取到热点话题");
        }
        log.info("[AutoPublish] 获取到 {} 条热点话题", allTopics.size());

        // 2. 选择最相关话题
        List<String> techTopics = hotTopicService.selectBestTopic(allTopics);
        if (techTopics.isEmpty()) {
            techTopics = allTopics.subList(0, Math.min(1, allTopics.size()));
        }
        String selectedTopic = techTopics.get(0);
        log.info("[AutoPublish] 选择话题：{}", selectedTopic);

        // 3. AI 生成文章（使用 MCP 自动选择分类和标签）
        Map<String, Object> mcpResult = aiService.generateArticleWithMcp(selectedTopic);
        String content = (String) mcpResult.get("content");
        if (content == null || content.isBlank() || content.startsWith("[AI_ERROR]") || content.startsWith("[PARSE_ERROR]")) {
            throw new BusinessException("AI 生成内容失败：" + content);
        }

        Long categoryId = mcpResult.get("categoryId") != null ? ((Number) mcpResult.get("categoryId")).longValue() : null;
        List<Long> tagIds = mcpResult.get("tagIds") != null ?
            ((List<?>) mcpResult.get("tagIds")).stream().map(t -> ((Number) t).longValue()).toList() : null;

        // 检查是否自动发布
        boolean autoPublish = aiSettingsService.getBooleanValue(AiSettingsService.SCHEDULER_AUTO_PUBLISH, false);
        int status = autoPublish ? 1 : 0;

        // 4. 保存文章
        String title = extractTitle(content);
        String summary = extractSummary(content);
        Long articleId = articleService.createAiArticle(title, summary, content, defaultAdminUserId, status, categoryId, tagIds);

        // 5. 返回结果
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("articleId", articleId);
        result.put("title", title);
        result.put("topic", selectedTopic);
        result.put("status", status);
        result.put("statusText", status == 1 ? "已发布" : "草稿");
        result.put("autoPublish", autoPublish);
        result.put("categoryId", categoryId);
        result.put("categoryName", mcpResult.get("categoryName"));
        result.put("tagIds", tagIds);
        result.put("tagNames", mcpResult.get("tagNames"));
        result.put("message", String.format("文章「%s」已生成，状态：%s", title, status == 1 ? "已发布" : "草稿"));

        log.info("[AutoPublish] 工作流执行完成，文章ID：{}，状态：{}", articleId, status == 1 ? "已发布" : "草稿");
        return result;
    }

    private String extractTitle(String content) {
        String[] lines = content.split("\n");
        for (String line : lines) {
            String trimmed = line.trim();
            // 匹配 ## 标题 或 # 标题
            if (trimmed.startsWith("## ")) {
                return trimmed.substring(3).trim();
            }
            if (trimmed.startsWith("# ") && !trimmed.startsWith("## ")) {
                return trimmed.substring(2).trim();
            }
        }
        // 没有标题则取前80字
        if (content.length() > 80) {
            return content.substring(0, 80).replaceAll("[\\n\\r]+", " ").trim();
        }
        return content.trim();
    }

    private String extractSummary(String content) {
        String text = content.replaceAll("(?m)^#+\\s*.*$", "")
                .replaceAll("```[\\s\\S]*?```", "")
                .replaceAll("\\[.*?\\]\\(.*?\\)", "$1")
                .replaceAll("[\\n\\r]+", " ")
                .trim();
        if (text.length() > SUMMARY_LENGTH) {
            return text.substring(0, SUMMARY_LENGTH) + "...";
        }
        return text;
    }
}
