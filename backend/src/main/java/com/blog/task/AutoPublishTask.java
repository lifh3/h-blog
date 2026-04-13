package com.blog.task;

import com.blog.config.AiConfig.SchedulerProperties;
import com.blog.service.AutoPublishService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class AutoPublishTask {

    private static final Logger log = LoggerFactory.getLogger(AutoPublishTask.class);

    @Autowired
    private AutoPublishService autoPublishService;

    @Autowired
    private SchedulerProperties schedulerProperties;

    /**
     * 定时执行自动发文工作流
     * cron 表达式由 config.yaml 中的 scheduler.cron 配置
     */
    @Scheduled(cron = "${scheduler.cron:0 0 9 * * ?}")
    public void runScheduledPublish() {
        if (!schedulerProperties.isEnabled()) {
            log.info("[AutoPublish] 定时任务已禁用，跳过执行");
            return;
        }
        log.info("[AutoPublish] 定时任务触发，开始执行工作流");
        try {
            autoPublishService.executeWorkflow();
            log.info("[AutoPublish] 定时任务执行完成");
        } catch (Exception e) {
            log.error("[AutoPublish] 定时任务执行失败：{}", e.getMessage(), e);
        }
    }
}
