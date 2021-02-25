package com.wine.to.up.winelab.parser.service.job;

import com.wine.to.up.winelab.parser.service.services.StorageService;
import com.wine.to.up.winelab.parser.service.services.UpdateService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.text.SimpleDateFormat;
import java.util.Date;

@Component
@Slf4j
@Configuration
public class UpdateJob {

    @Autowired
    private UpdateService updateService;

    @Autowired
    private StorageService storageService;

    /**
     * Каждый час отсылает часть вин в каталог
     */
    @Scheduled(cron = "${job.cron.send}")
    public void parseCatalogs() {
        updateService.sendNextChunkToCatalog();
        var dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        log.info("Sent chunk to catalog at {}", dateFormat.format(new Date()));
    }

}