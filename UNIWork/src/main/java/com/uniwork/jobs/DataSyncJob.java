package com.uniwork.jobs;

import lombok.extern.log4j.Log4j2;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
@Log4j2
public class DataSyncJob {

    @Scheduled(cron = "0 0 * * * *")
    public void syncProjectStatus() {
        log.info("Sync Project Status");
    }
}
