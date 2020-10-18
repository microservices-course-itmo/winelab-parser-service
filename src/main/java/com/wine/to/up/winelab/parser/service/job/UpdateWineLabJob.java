package com.wine.to.up.winelab.parser.service.job;

import com.wine.to.up.winelab.parser.service.services.UpdateService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
@Slf4j
public class UpdateWineLabJob {

    @Autowired
    private UpdateService updateService;

    /**
     * Каждый день обновляет список вин
     */
    @Scheduled(fixedRate = 24*60*60*1000, initialDelay = 24*60*60*1000)
    public void runJob() {
        long startDate = new Date().getTime();
        log.info("start UpdateWineLabJob run job method at " + startDate);
        updateService.updateCatalog();
        log.info("end UpdateWineLabJob run job method at " + new Date().getTime() + " duration = " + (new Date().getTime() - startDate));
    }

}