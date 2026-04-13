package com.blog.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties
public class AiConfig {

    @Bean
    @ConfigurationProperties(prefix = "ai")
    public AiProperties aiProperties() {
        return new AiProperties();
    }

    @Bean
    @ConfigurationProperties(prefix = "scheduler")
    public SchedulerProperties schedulerProperties() {
        return new SchedulerProperties();
    }

    public static class AiProperties {
        private String apiKey;
        private String baseUrl;
        private String model;

        public String getApiKey() { return apiKey; }
        public void setApiKey(String apiKey) { this.apiKey = apiKey; }
        public String getBaseUrl() { return baseUrl; }
        public void setBaseUrl(String baseUrl) { this.baseUrl = baseUrl; }
        public String getModel() { return model; }
        public void setModel(String model) { this.model = model; }
    }

    public static class SchedulerProperties {
        private boolean enabled = true;
        private String cron = "0 0 9 * * ?";
        private boolean autoPublish = false;

        public boolean isEnabled() { return enabled; }
        public void setEnabled(boolean enabled) { this.enabled = enabled; }
        public String getCron() { return cron; }
        public void setCron(String cron) { this.cron = cron; }
        public boolean isAutoPublish() { return autoPublish; }
        public void setAutoPublish(boolean autoPublish) { this.autoPublish = autoPublish; }
    }
}
