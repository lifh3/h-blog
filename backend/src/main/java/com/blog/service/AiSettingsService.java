package com.blog.service;

import com.blog.entity.AiSettings;
import com.blog.mapper.AiSettingsMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class AiSettingsService {

    @Autowired
    private AiSettingsMapper aiSettingsMapper;

    // AI 发布设置 Keys
    public static final String AI_AUTO_PUBLISH = "ai.auto_publish";
    public static final String AI_DEFAULT_CATEGORY_ID = "ai.default_category_id";

    // 定时任务设置 Keys
    public static final String SCHEDULER_ENABLED = "scheduler.enabled";
    public static final String SCHEDULER_CRON = "scheduler.cron";
    public static final String SCHEDULER_AUTO_PUBLISH = "scheduler.auto_publish";
    public static final String SCHEDULER_CATEGORY_ID = "scheduler.category_id";

    /**
     * 获取所有设置
     */
    public Map<String, String> getAllSettings() {
        List<AiSettings> settings = aiSettingsMapper.selectList(null);
        Map<String, String> result = new HashMap<>();
        for (AiSettings setting : settings) {
            result.put(setting.getSettingKey(), setting.getSettingValue());
        }
        return result;
    }

    /**
     * 获取单个设置值
     */
    public String getValue(String key) {
        return aiSettingsMapper.getValueByKey(key);
    }

    /**
     * 获取布尔值设置
     */
    public boolean getBooleanValue(String key, boolean defaultValue) {
        String value = getValue(key);
        if (value == null || value.isEmpty()) {
            return defaultValue;
        }
        return "true".equalsIgnoreCase(value) || "1".equals(value);
    }

    /**
     * 获取整数值设置
     */
    public Long getLongValue(String key, Long defaultValue) {
        String value = getValue(key);
        if (value == null || value.isEmpty()) {
            return defaultValue;
        }
        try {
            return Long.parseLong(value);
        } catch (NumberFormatException e) {
            return defaultValue;
        }
    }

    /**
     * 保存设置
     */
    public void saveSetting(String key, String value) {
        aiSettingsMapper.updateValueByKey(key, value);
    }

    /**
     * 保存布尔值设置
     */
    public void saveBooleanSetting(String key, boolean value) {
        saveSetting(key, String.valueOf(value));
    }

    /**
     * 获取 AI 发布相关设置
     */
    public Map<String, Object> getAiPublishSettings() {
        Map<String, Object> result = new HashMap<>();
        result.put("autoPublish", getBooleanValue(AI_AUTO_PUBLISH, false));
        result.put("defaultCategoryId", getLongValue(AI_DEFAULT_CATEGORY_ID, null));
        return result;
    }

    /**
     * 保存 AI 发布设置
     */
    public void saveAiPublishSettings(boolean autoPublish, Long defaultCategoryId) {
        saveBooleanSetting(AI_AUTO_PUBLISH, autoPublish);
        if (defaultCategoryId != null) {
            saveSetting(AI_DEFAULT_CATEGORY_ID, String.valueOf(defaultCategoryId));
        } else {
            saveSetting(AI_DEFAULT_CATEGORY_ID, "");
        }
    }

    /**
     * 获取定时任务相关设置
     */
    public Map<String, Object> getSchedulerSettings() {
        Map<String, Object> result = new HashMap<>();
        result.put("enabled", getBooleanValue(SCHEDULER_ENABLED, true));
        result.put("cron", getValue(SCHEDULER_CRON));
        result.put("autoPublish", getBooleanValue(SCHEDULER_AUTO_PUBLISH, false));
        result.put("categoryId", getLongValue(SCHEDULER_CATEGORY_ID, null));
        return result;
    }

    /**
     * 保存定时任务设置
     */
    public void saveSchedulerSettings(boolean enabled, String cron, boolean autoPublish, Long categoryId) {
        saveBooleanSetting(SCHEDULER_ENABLED, enabled);
        if (cron != null && !cron.isEmpty()) {
            saveSetting(SCHEDULER_CRON, cron);
        }
        saveBooleanSetting(SCHEDULER_AUTO_PUBLISH, autoPublish);
        if (categoryId != null) {
            saveSetting(SCHEDULER_CATEGORY_ID, String.valueOf(categoryId));
        } else {
            saveSetting(SCHEDULER_CATEGORY_ID, "");
        }
    }
}
