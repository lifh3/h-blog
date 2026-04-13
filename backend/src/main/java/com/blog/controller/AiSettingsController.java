package com.blog.controller;

import com.blog.common.Result;
import com.blog.service.AiSettingsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/admin/ai-settings")
public class AiSettingsController {

    @Autowired
    private AiSettingsService aiSettingsService;

    /**
     * 获取所有 AI 设置
     */
    @GetMapping
    public Result<Map<String, Object>> getAllSettings() {
        Map<String, Object> aiPublishSettings = aiSettingsService.getAiPublishSettings();
        Map<String, Object> schedulerSettings = aiSettingsService.getSchedulerSettings();
        return Result.success(Map.of(
            "aiPublish", aiPublishSettings,
            "scheduler", schedulerSettings
        ));
    }

    /**
     * 保存 AI 发布设置
     */
    @PutMapping("/ai-publish")
    public Result<Void> saveAiPublishSettings(@RequestBody Map<String, Object> settings) {
        boolean autoPublish = Boolean.TRUE.equals(settings.get("autoPublish"));
        Long defaultCategoryId = null;
        if (settings.get("defaultCategoryId") != null) {
            defaultCategoryId = Long.valueOf(settings.get("defaultCategoryId").toString());
        }
        aiSettingsService.saveAiPublishSettings(autoPublish, defaultCategoryId);
        return Result.success();
    }

    /**
     * 保存定时任务设置
     */
    @PutMapping("/scheduler")
    public Result<Void> saveSchedulerSettings(@RequestBody Map<String, Object> settings) {
        boolean enabled = Boolean.TRUE.equals(settings.get("enabled"));
        String cron = settings.get("cron") != null ? settings.get("cron").toString() : "0 0 9 * * ?";
        boolean autoPublish = Boolean.TRUE.equals(settings.get("autoPublish"));
        Long categoryId = null;
        if (settings.get("categoryId") != null && !"".equals(settings.get("categoryId").toString())) {
            categoryId = Long.valueOf(settings.get("categoryId").toString());
        }
        aiSettingsService.saveSchedulerSettings(enabled, cron, autoPublish, categoryId);
        return Result.success();
    }
}
