package com.example.demo.service;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;

@Service
public class LogDemoService {

    private static final Logger logger = LogManager.getLogger(LogDemoService.class);
    
    public void performTask(String taskName) {
        logger.info("开始执行任务: {}", taskName);
        
        try {
            // 模拟任务执行
            Thread.sleep(1000);
            logger.debug("任务执行中的调试信息");
            
            // 模拟可能出现的警告情况
            if (taskName.length() > 10) {
                logger.warn("任务名称过长: {}", taskName);
            }
            
        } catch (Exception e) {
            logger.error("任务执行失败: {}", e.getMessage(), e);
        }
        
        logger.info("任务执行完成: {}", taskName);
    }
} 