package com.wine.to.up.winelab.parser.service.job;

import com.wine.to.up.winelab.parser.service.components.WineLabParserMetricsCollector;
import com.wine.to.up.winelab.parser.service.dto.City;
import com.wine.to.up.winelab.parser.service.services.ParserService;
import com.wine.to.up.winelab.parser.service.services.UpdateService;
import lombok.extern.slf4j.Slf4j;
import lombok.var;
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
    private static final String SPARKLING = "sparkling";

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
        int moscowWinePageCount = parserService.getCatalogPageCount("wine", City.MOSCOW);
        int moscowSparklingPageCount = parserService.getCatalogPageCount(SPARKLING, City.MOSCOW);
        int defaultWinePageCount = parserService.getCatalogPageCount("wine", City.defaultCity());
        int defaultSparklingPageCount = parserService.getCatalogPageCount(SPARKLING, City.defaultCity());
        long period = MILLISECONDS_IN_SECOND * SECONDS_IN_DAY /
                (moscowWinePageCount + moscowSparklingPageCount +
                        (City.values().length - 1) * (defaultWinePageCount + defaultSparklingPageCount) + 1);
        long periodInSecs = period / 1000;
        if (firstTask) {
            firstTask = false;
        } else {
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
                new SendPageToCatalogJob(parserService, updateService),
                period
        );
        log.info("Created new job with period of {} seconds ", periodInSecs);
    }
}
