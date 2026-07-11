package com.uniwork.modules.auth.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Slf4j
@Component
public class KeepAliveTask {

    @Value("${app.url:}")
    private String appUrl;

    private final RestTemplate restTemplate = new RestTemplate();

    // 600,000 ms = 10 minutes
    @Scheduled(fixedRate = 600000)
    public void pingSelf() {
        if (appUrl == null || appUrl.trim().isEmpty()) {
            log.debug("KeepAliveTask: app.url is not configured. Self-ping is disabled.");
            return;
        }

        try {
            String pingUrl = appUrl.trim();
            if (!pingUrl.endsWith("/")) {
                pingUrl += "/";
            }
            pingUrl += "auth/ping";

            log.info("KeepAliveTask: Sending self-ping to {}", pingUrl);
            String response = restTemplate.getForObject(pingUrl, String.class);
            log.info("KeepAliveTask: Ping response: {}", response);
        } catch (Exception e) {
            log.error("KeepAliveTask: Failed to ping self: {}", e.getMessage());
        }
    }
}
