package com.wine.to.up.winelab.parser.service.job;

import com.wine.to.up.winelab.parser.service.dto.Wine;
import com.wine.to.up.winelab.parser.service.services.ParserService;
import com.wine.to.up.winelab.parser.service.services.UpdateService;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

@Slf4j
public class SendPageToCatalogJob implements Runnable {
    ParserService parserService;
    UpdateService updateService;

    private int currentPageNumber;
    private int winePageCount;
    private int sparklingPageCount;

    SendPageToCatalogJob(ParserService parserService, UpdateService updateService, int winePageCount, int sparklingPageCount) {
        this.parserService = parserService;
        this.updateService = updateService;
        this.currentPageNumber = 1;
        this.winePageCount = winePageCount;
        this.sparklingPageCount = sparklingPageCount;
    }

    public void run() {
        try {
            List<Wine> wines = parserService.getFromCatalogPage(currentPageNumber, winePageCount, sparklingPageCount);
            updateService.sendToKafka(wines);
            if(currentPageNumber <= winePageCount) {
                log.info("Sent page {} of wines to catalog service", currentPageNumber);
            }
            else {
                log.info("Sent page {} of sparkling wines to catalog service", currentPageNumber - winePageCount);
            }
            currentPageNumber++;
        } catch (Exception ex) {
            log.error("Catalog page {} parsing failed: {}", currentPageNumber, ex);
        }
    }
}
