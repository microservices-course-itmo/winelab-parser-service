package com.wine.to.up.winelab.parser.service.job;

import com.wine.to.up.winelab.parser.service.components.WineLabParserMetricsCollector;
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
    private final WineLabParserMetricsCollector metricsCollector;

    public ParseJob(ParserService parserService, UpdateService updateService, WineLabParserMetricsCollector metricsCollector) {
        this.parserService = parserService;
        this.updateService = updateService;
        this.metricsCollector = metricsCollector;
    }

    private ThreadPoolTaskScheduler taskScheduler;
    private final int SECONDS_IN_DAY = 60 * 60 * 24;
    private final int MILLISECONDS_IN_SECOND = 1000;
    private boolean firstTask = true;

    @Scheduled(fixedRate = MILLISECONDS_IN_SECOND * SECONDS_IN_DAY)
    public void setPeriodicCatalogUpdateJob() {
        int winePageCount = parserService.getCatalogPageCount("wine");
        int sparklingPageCount = parserService.getCatalogPageCount("sparkling");
        long period = MILLISECONDS_IN_SECOND * SECONDS_IN_DAY / (winePageCount + sparklingPageCount);
        if (firstTask) {
            firstTask = false;
        }
        else {
            metricsCollector.parsingCompleteSuccessful();
            taskScheduler.destroy();
        }
        metricsCollector.parsingStarted();
        taskScheduler = new ThreadPoolTaskScheduler();
        taskScheduler.setPoolSize(4);
        taskScheduler.setThreadNamePrefix(
                "ThreadPoolTaskScheduler");
        taskScheduler.initialize();
        taskScheduler.scheduleAtFixedRate(
                new SendPageToCatalogJob(parserService, updateService, winePageCount, sparklingPageCount),
                period
        );
        log.info("Created new job with period of {} seconds ", period);
    }
}
