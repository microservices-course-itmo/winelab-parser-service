package com.wine.to.up.winelab.parser.service.job;

import com.wine.to.up.winelab.parser.service.services.ParserService;
import com.wine.to.up.winelab.parser.service.services.UpdateService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.stereotype.Component;


@Slf4j
@Component
@Configuration
public class ParseJob {
    private final ParserService parserService;
    private final UpdateService updateService;
    private final ThreadPoolTaskScheduler taskScheduler;

    public ParseJob(ParserService parserService, UpdateService updateService, ThreadPoolTaskScheduler taskScheduler) {
        this.parserService = parserService;
        this.updateService = updateService;
        this.taskScheduler = taskScheduler;
    }

    private final int SECONDS_IN_DAY = 60 * 60 * 24;
    private final int HOURS_IN_DAY = 24;

    @Scheduled(fixedRate = SECONDS_IN_DAY)
    public void setPeriodicCatalogUpdateJob() {
        int winePageCount = parserService.getCatalogPageCount("wine");
        int sparklingPageCount = parserService.getCatalogPageCount("sparkling");
        long period = SECONDS_IN_DAY / (winePageCount + sparklingPageCount);
        taskScheduler.scheduleAtFixedRate(
                new SendPageToCatalogJob(parserService, updateService, winePageCount, sparklingPageCount),
                period
        );
        log.info("Created new job with period of {} seconds ", period);
    }
}
