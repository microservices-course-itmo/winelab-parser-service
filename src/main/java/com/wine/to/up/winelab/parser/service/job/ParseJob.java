package com.wine.to.up.winelab.parser.service.job;

import com.wine.to.up.winelab.parser.service.dto.Wine;
import com.wine.to.up.winelab.parser.service.services.ParserService;
import com.wine.to.up.winelab.parser.service.services.StorageService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Map;


@Slf4j
@Component
@Configuration
public class ParseJob {
    private final ParserService parserService;
    private final StorageService storageService;

    public ParseJob(ParserService parserService, StorageService storageService) {
        this.parserService = parserService;
        this.storageService = storageService;
    }

    @Scheduled(cron = "${job.cron.store}")
    public void parseAndStoreCatalogs() {
        var dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        log.info("Catalogs parsing starter at {}", dateFormat.format(new Date()));

        Map<Integer, Wine> wines = parserService.parseCatalogs();
        storageService.setWines(new ArrayList<>(wines.values()));

        log.info("Catalogs parsing finished successful at {}. {} objects parsed",
                dateFormat.format(new Date()), wines.size());
    }
}
