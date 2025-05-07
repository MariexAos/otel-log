package com.example.demo.controller;

import lombok.RequiredArgsConstructor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.service.LogDemoService;

@RestController
@RequestMapping("/api/logs")
@RequiredArgsConstructor
public class LogDemoController {

    // 获取Logger实例
    private static final Logger logger = LogManager.getLogger(LogDemoController.class);
    
    private final LogDemoService logDemoService;

    @GetMapping("/test")
    public String testLogs(@RequestParam(defaultValue = "测试") String message) {
        // 打印不同级别的日志
        logger.trace("这是一条TRACE级别的日志: {}", message);
        logger.debug("这是一条DEBUG级别的日志: {}", message);
        logger.info("这是一条INFO级别的日志: {}", message);
        logger.warn("这是一条WARN级别的日志: {}", message);
        logger.error("这是一条ERROR级别的日志: {}", message);
        logger.info(String.format("资金回帐: %s", message));
        
        return "日志已打印，请查看控制台或日志文件";
    }
    
    @GetMapping("/task")
    public String executeTask(@RequestParam(defaultValue = "测试任务") String taskName) {
        logger.info("接收到执行任务请求: {}", taskName);
        logDemoService.performTask(taskName);
        return "任务已执行，请查看控制台或日志文件";
    }
} 