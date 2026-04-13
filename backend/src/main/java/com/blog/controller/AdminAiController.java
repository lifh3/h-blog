package com.blog.controller;

import com.blog.common.AuthContext;
import com.blog.common.Result;
import com.blog.config.AiConfig.SchedulerProperties;
import com.blog.service.AiSettingsService;
import com.blog.service.AutoPublishService;
import com.blog.service.HotTopicService;
import com.blog.service.McpToolService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.support.CronTrigger;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admin/ai")
public class AdminAiController {

    @Autowired
    private HotTopicService hotTopicService;

    @Autowired
    private AutoPublishService autoPublishService;

    @Autowired
    private SchedulerProperties schedulerProperties;

    @Autowired
    private McpToolService mcpToolService;

    @Autowired
    private AiSettingsService aiSettingsService;

    // ========== AI 文章功能 ==========

    /**
     * 获取当前热点话题（AI 搜索发现）
     */
    @GetMapping("/topics")
    public Result<List<String>> getHotTopics() {
        List<String> topics = hotTopicService.getAllHotTopics();
        return Result.success(topics);
    }

    /**
     * 生成文章（AI 写作）
     * body: { topic: "话题" }
     * 返回生成的文章ID和状态
     */
    @PostMapping("/generate")
    public Result<Map<String, Object>> generateArticle(@RequestBody Map<String, Object> request) {
        // 防御：topic 可能是 String 也可能是 Array
        Object topicRaw = request.get("topic");
        String topic;
        if (topicRaw instanceof String) {
            topic = (String) topicRaw;
        } else if (topicRaw instanceof List) {
            List<?> list = (List<?>) topicRaw;
            topic = list.isEmpty() ? null : String.valueOf(list.get(0));
        } else {
            topic = topicRaw == null ? null : String.valueOf(topicRaw);
        }
        if (topic == null || topic.isBlank()) {
            return Result.error("话题不能为空");
        }
        Map<String, Object> result = autoPublishService.generateArticleWithContent(topic);
        return Result.success(result);
    }

    /**
     * 总结今日热点（综述文章）
     */
    @PostMapping("/summary")
    public Result<Map<String, Object>> generateSummary() {
        Map<String, Object> result = autoPublishService.generateSummaryArticle();
        return Result.success(result);
    }

    /**
     * 按方向生成文章
     * body: { direction: "weibo" | "zhihu" | "baidu" | ... }
     */
    @PostMapping("/generate-by-direction")
    public Result<Map<String, Object>> generateByDirection(@RequestBody Map<String, String> request) {
        String direction = request.get("direction");
        if (direction == null || direction.isBlank()) {
            return Result.error("方向不能为空");
        }
        Map<String, Object> result = autoPublishService.generateByDirection(direction);
        return Result.success(result);
    }

    /**
     * 批量生成文章（为每个热点生成一篇）
     * body: { topics: ["话题1", "话题2", ...], maxCount: 50 }
     */
    @PostMapping("/batch-generate")
    public Result<List<Map<String, Object>>> batchGenerate(@RequestBody Map<String, Object> request) {
        Object topicsRaw = request.get("topics");
        List<String> topics;
        if (topicsRaw instanceof List) {
            topics = ((List<?>) topicsRaw).stream().map(String::valueOf).toList();
        } else {
            return Result.error("topics 必须是数组");
        }
        int maxCount = request.containsKey("maxCount") ? ((Number) request.get("maxCount")).intValue() : 50;
        List<Map<String, Object>> results = autoPublishService.batchGenerateArticles(topics, maxCount);
        return Result.success(results);
    }

    /**
     * 获取可用平台列表
     */
    @GetMapping("/platforms")
    public Result<List<Map<String, String>>> getPlatforms() {
        return Result.success(hotTopicService.getAvailablePlatforms());
    }

    /**
     * 按方向获取热点话题列表
     */
    @GetMapping("/direction-topics")
    public Result<List<String>> getDirectionTopics(@RequestParam String direction) {
        List<String> topics = hotTopicService.getTopicsByPlatform(direction);
        return Result.success(topics);
    }

    /**
     * 分析本地项目并生成文章
     * body: { files: [{name, path, content}, ...], prompt: "分析要求" }
     */
    @PostMapping("/analyze-local")
    public Result<Map<String, Object>> analyzeLocalProject(@RequestBody Map<String, Object> request) {
        Object filesRaw = request.get("files");
        String prompt = request.get("prompt") != null ? String.valueOf(request.get("prompt")) : "";

        if (!(filesRaw instanceof List)) {
            return Result.error("files 必须是数组");
        }

        @SuppressWarnings("unchecked")
        List<Map<String, String>> files = (List<Map<String, String>>) filesRaw;
        if (files.isEmpty()) {
            return Result.error("文件列表不能为空");
        }

        Map<String, Object> result = autoPublishService.analyzeLocalProject(files, prompt);
        return Result.success(result);
    }

    // ========== 定时任务功能 ==========

    /**
     * 手动触发一次定时发布工作流
     */
    @PostMapping("/publish-now")
    public Result<Map<String, Object>> publishNow() {
        Map<String, Object> result = autoPublishService.executeWorkflow();
        return Result.success(result);
    }

    /**
     * 获取定时任务配置（从数据库读取）
     */
    @GetMapping("/schedule-config")
    public Result<Map<String, Object>> getScheduleConfig() {
        return Result.success(aiSettingsService.getSchedulerSettings());
    }

    /**
     * 更新定时任务配置（保存到数据库）
     */
    @PutMapping("/schedule-config")
    public Result<Void> updateScheduleConfig(@RequestBody Map<String, Object> config) {
        boolean enabled = Boolean.TRUE.equals(config.get("enabled"));
        boolean autoPublish = Boolean.TRUE.equals(config.get("autoPublish"));
        String cron = config.get("cron") != null ? config.get("cron").toString() : "0 0 9 * * ?";
        Long categoryId = null;
        if (config.get("categoryId") != null && !"".equals(config.get("categoryId").toString())) {
            categoryId = Long.valueOf(config.get("categoryId").toString());
        }

        // 验证 cron 表达式
        try {
            new CronTrigger(cron);
        } catch (IllegalArgumentException e) {
            return Result.error("无效的 cron 表达式：" + cron);
        }

        aiSettingsService.saveSchedulerSettings(enabled, cron, autoPublish, categoryId);
        return Result.success();
    }

    /**
     * 获取 AI 可用的分类和标签列表
     */
    @GetMapping("/available-tags-categories")
    public Result<Map<String, Object>> getAvailableTagsAndCategories() {
        Map<String, Object> categories = mcpToolService.getCategories();
        Map<String, Object> tags = mcpToolService.getTags();
        return Result.success(Map.of(
            "categories", categories,
            "tags", tags
        ));
    }
}
