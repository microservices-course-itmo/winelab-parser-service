package com.wine.to.up.winelab.parser.service.job;

import com.wine.to.up.winelab.parser.service.dto.Wine;
import com.wine.to.up.winelab.parser.service.services.ParserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;


@Slf4j
@Component
public class ParseJob {
    private final ParserService parserService;

    @Autowired
    public ParseJob(ParserService parserService) {
        this.parserService = parserService;
    }

    @Scheduled(cron = "${job.cron.parse}")
    public void parseCatalogs() {
        var dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        log.info("Catalogs parsing starter at {}", dateFormat.format(new Date()));

        try {
            Map<Integer, Wine> wines = parserService.parseCatalogs();
            log.info("Catalogs parsing finished successful at {}. {} objects parsed",
                    dateFormat.format(new Date()), wines.size());
        } catch (Exception ex) {
            log.error("Catalogs parsing failed: {}", ex.getMessage());
        }


    }
}
