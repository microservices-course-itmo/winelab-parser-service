package com.wine.to.up.winelab.parser.service.job;

import com.wine.to.up.winelab.parser.service.services.ParserService;
import com.wine.to.up.winelab.parser.service.services.UpdateService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;


@Slf4j
@Component
@Configuration
public class ParseJob {
    private final ParserService parserService;
    private final UpdateService updateService;

    public ParseJob(ParserService parserService, UpdateService updateService) {
        this.parserService = parserService;
        this.updateService = updateService;
    }

    private final int SECONDS_IN_DAY = 60 * 60 * 24;
    private final int HOURS_IN_DAY = 24;

    private int currentPageNumber;
    private int winePageCount;
    private int sparklingPageCount;

    @Scheduled(fixedRate = SECONDS_IN_DAY)
    public void setPeriodicCatalogUpdateJob() {
        winePageCount = parserService.getCatalogPageCount("wine");
        sparklingPageCount = parserService.getCatalogPageCount("sparkling");
        currentPageNumber = 1;
        // TODO останавливать старый taskScheduler и вызывать новый taskScheduler
        //  от new SendPageToCatalogJob(parserService, updateService, winePageCount, sparklingPageCount)
        //  с fixedRate = SECONDS_IN_DAY / (winePageCount + sparklingPageCount)
    }
}
