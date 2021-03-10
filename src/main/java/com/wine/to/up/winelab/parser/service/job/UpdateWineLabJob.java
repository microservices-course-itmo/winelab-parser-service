package com.wine.to.up.winelab.parser.service.job;

import com.wine.to.up.winelab.parser.service.services.UpdateService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@Configuration
public class UpdateWineLabJob {

    @Autowired
    private UpdateService updateService;

    /**
     * Каждый день обновляет список вин
     */
    @Scheduled(cron = "${job.cron.update}")
    public int runJob() {
        long startDate = System.currentTimeMillis();
        log.info("start UpdateWineLabJob run job method at {}", startDate);
        int count = updateService.updateCatalog();
        log.info("end UpdateWineLabJob run job method at {} duration = {} ", System.currentTimeMillis(), (System.currentTimeMillis() - startDate));
        return count;
    }

}