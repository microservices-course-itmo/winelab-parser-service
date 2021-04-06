package com.wine.to.up.winelab.parser.service.job;

import com.wine.to.up.commonlib.annotations.InjectEventLogger;
import com.wine.to.up.commonlib.logging.EventLogger;
import com.wine.to.up.winelab.parser.service.components.WineLabParserMetricsCollector;
import com.wine.to.up.winelab.parser.service.dto.City;
import com.wine.to.up.winelab.parser.service.dto.Wine;
import com.wine.to.up.winelab.parser.service.logging.WineLabParserNotableEvents;
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
    @InjectEventLogger
    private EventLogger eventLogger;
    @Value("${parse.delay.successful}")
    long parseDelayAfterSuccess; // in minutes;
    @Value("${parse.delay.failed}")
    long parseDelayAfterFailure; // in minutes;

    ParserService parserService;
    UpdateService updateService;
    WineLabParserMetricsCollector metricsCollector;

    private int currentCatalogPageCount;

    private int unsuccessfulStreak;
    private int unsuccessfulTotal;
    private int currentPageNumber;
    private City currentCity;
    private String currentCatalog;
    private LocalDateTime timeToStartParsing;
    private boolean isParsing; // needed for metrics purposes only

    public ParseJob(ParserService parserService, UpdateService updateService, WineLabParserMetricsCollector metricsCollector) {
        this.parserService = parserService;
        this.updateService = updateService;
        this.metricsCollector = metricsCollector;
        this.reset();
    }

    @Scheduled(fixedDelay = 1000)
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
                unsuccessfulStreak++;
                unsuccessfulTotal++;
                log.warn("Failed to parse page {} of {}s catalog for {}. Fails in a row: {}. Total fails: {}",
                        currentPageNumber, currentCatalog, currentCity, unsuccessfulStreak, unsuccessfulTotal);
                if (unsuccessfulTotal >= 20) { // if page parsing failed 20 times total
                    onFailure();
                }
                if (unsuccessfulStreak >= 3) { // couldn't get catalog page 3 times in a row
                    unsuccessfulStreak = 0;
                    if(nextPage()) {
                        onSuccess();
                    }
                }
            } else {
                unsuccessfulStreak = 0;
                updateService.sendToKafka(wines);
                log.info("Sent page {} of {} from {} to catalog service", currentPageNumber, currentCatalog, currentCity.toString());
                if (nextPage()) { // if all wines are parsed
                    onSuccess();
                }
            }
        } catch (Exception ex) {
            log.error("Catalog page {} parsing failed: {}", currentPageNumber, ex);
            eventLogger.warn(WineLabParserNotableEvents.W_WINE_PAGE_PARSING_FAILED);
            unsuccessfulStreak++;
            unsuccessfulTotal++;
            log.warn("Fails in a row: {}", unsuccessfulStreak);
            if (unsuccessfulStreak >= 20) {
                onFailure();
            }
        }
    }

    private boolean nextPage() {
        if (currentCatalog.equals(WINE)) {
            return nextPageWhenWine();
        } else {
            return nextPageWhenSparkling();
        }
    }

    private boolean nextPageWhenWine() {
        if (currentPageNumber < currentCatalogPageCount) {
            currentPageNumber++;
        } else {
            currentPageNumber = 1;
            currentCatalog = SPARKLING;
            currentCatalogPageCount = parserService.getCatalogPageCount(currentCatalog, currentCity);
        }
        return false;
    }

    private boolean nextPageWhenSparkling() {
        if (currentPageNumber < currentCatalogPageCount) {
            currentPageNumber++;
        } else {
            if (currentCity.ordinal() + 1 == City.values().length) { // if last city on the list
                return true;
            }
            currentPageNumber = 1;
            currentCatalog = WINE;
            currentCity = City.values()[currentCity.ordinal() + 1];
            currentCatalogPageCount = parserService.getCatalogPageCount(currentCatalog, currentCity);
        }
        return false;
    }

    private void reset() {
        this.unsuccessfulStreak = 0;
        this.unsuccessfulTotal = 0;
        this.currentPageNumber = 1;
        this.currentCity = City.values()[0];
        this.currentCatalog = WINE;
        this.currentCatalogPageCount = parserService.getCatalogPageCount(currentCatalog, currentCity);
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
