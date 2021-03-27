package com.wine.to.up.winelab.parser.service.job;

import com.wine.to.up.winelab.parser.service.components.WineLabParserMetricsCollector;
import com.wine.to.up.winelab.parser.service.dto.City;
import com.wine.to.up.winelab.parser.service.dto.Wine;
import com.wine.to.up.winelab.parser.service.services.ParserService;
import com.wine.to.up.winelab.parser.service.services.UpdateService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Slf4j
@Component
@Configuration
public class ParseJob {
    private final String WINE = "wine";
    private final String SPARKLING = "sparkling";

    @Value("${parse.delay.successful}")
    long parseDelayAfterSuccess; // in minutes;
    @Value("${parse.delay.failed}")
    long parseDelayAfterFailure; // in minutes;

    ParserService parserService;
    UpdateService updateService;
    WineLabParserMetricsCollector metricsCollector;

    int moscowWinePageCount;
    int moscowSparklingPageCount;
    int defaultWinePageCount;
    int defaultSparklingPageCount;

    int unsuccessfulStreak;
    int currentPageNumber;
    City currentCity;
    String currentCatalog;
    LocalDateTime timeToStartParsing;
    boolean isParsing; // needed for metrics purposes only

    public ParseJob(ParserService parserService, UpdateService updateService, WineLabParserMetricsCollector metricsCollector) {
        this.parserService = parserService;
        this.updateService = updateService;
        this.metricsCollector = metricsCollector;
        this.reset();
    }

    @Scheduled(fixedRateString = "${job.rate.update.page}")
    public void parsePage() {
        if (!ready()) {
            return;
        }
        if (!isParsing) {
            isParsing = true;
            metricsCollector.parsingStarted();
        }
        try {
            List<Wine> wines = parserService.getFromCatalogPage(currentPageNumber, currentCatalog, currentCity);
            if (wines.isEmpty()) {
                unsuccessfulStreak += 1;
            } else {
                updateService.sendToKafka(wines);
                log.info("Sent page {} of {} from {} to catalog service", currentPageNumber, currentCatalog, currentCity.toString());
                if (nextPage()) { // if all wines are parsed
                    onSuccess();
                }
            }
            unsuccessfulStreak = 0;
        } catch (Exception ex) {
            log.error("Catalog page {} parsing failed: {}", currentPageNumber, ex);
            unsuccessfulStreak += 1;
            if (unsuccessfulStreak >= 5) { // if page parsing failed 5 times in a row
                onFailure();
            }
        }
    }

    private boolean nextPage() {
        if (currentCatalog.equals(WINE)) {
            return nextPageWhenWine((currentCity == City.MOSCOW ? moscowWinePageCount : defaultWinePageCount));
        } else {
            return nextPageWhenSparkling((currentCity == City.MOSCOW ? moscowSparklingPageCount : defaultSparklingPageCount));
        }
    }

    private boolean nextPageWhenWine(int pageCount) {
        if (currentPageNumber < pageCount) {
            currentPageNumber++;
        } else {
            currentPageNumber = 1;
            currentCatalog = SPARKLING;
        }
        return false;
    }

    private boolean nextPageWhenSparkling(int pageCount) {
        if (currentPageNumber < pageCount) {
            currentPageNumber++;
        } else {
            if (currentCity.ordinal() + 1 == City.values().length) { // if last city on the list
                return true;
            }
            currentPageNumber = 1;
            currentCatalog = WINE;
            currentCity = City.values()[currentCity.ordinal() + 1];
        }
        return false;
    }

    private void reset() {
        this.moscowWinePageCount = parserService.getCatalogPageCount(WINE, City.MOSCOW);
        this.moscowSparklingPageCount = parserService.getCatalogPageCount(SPARKLING, City.MOSCOW);
        this.defaultWinePageCount = parserService.getCatalogPageCount(WINE, City.defaultCity());
        this.defaultSparklingPageCount = parserService.getCatalogPageCount(SPARKLING, City.defaultCity());

        this.unsuccessfulStreak = 0;
        this.currentPageNumber = 1;
        this.currentCity = City.values()[0];
        this.currentCatalog = WINE;
        this.isParsing = false;
    }

    private boolean ready() {
        if (timeToStartParsing == null) {
            return true;
        }
        if (LocalDateTime.now().isBefore(timeToStartParsing)) {
            return false;
        }
        return true;
    }

    private void onFailure() {
        log.error("Parsing process failed");
        metricsCollector.parsingCompleteFailed();
        reset();
        this.timeToStartParsing = LocalDateTime.now().plus(parseDelayAfterFailure, ChronoUnit.MINUTES);
    }

    private void onSuccess() {
        log.info("Parsing process succeeded");
        metricsCollector.parsingCompleteSuccessful();
        reset();
        this.timeToStartParsing = LocalDateTime.now().plus(parseDelayAfterSuccess, ChronoUnit.MINUTES);
    }

}
