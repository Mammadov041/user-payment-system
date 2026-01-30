package com.example.userpaymentservice.controller;

import com.example.userpaymentservice.config.AppProperties;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/config")
public class ConfigController {

    @Value("${app.env}")
    private String envFromValue;

    private final AppProperties appProperties;

    public ConfigController(AppProperties appProperties) {
        this.appProperties = appProperties;
    }

    @GetMapping
    public Map<String, Object> getConfig() {
        Map<String, Object> config = new HashMap<>();
        config.put("envFromValue", envFromValue);
        config.put("envFromConfigProps", appProperties.getEnv());
        config.put("appName", appProperties.getName());
        config.put("appVersion", appProperties.getVersion());
        config.put("loggingLevel", appProperties.getLogging().getLevel());
        config.put("detailedLogging", appProperties.getLogging().isDetailed());
        return config;
    }
}