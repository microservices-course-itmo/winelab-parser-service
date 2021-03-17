package com.wine.to.up.winelab.parser.service.job;

import com.wine.to.up.winelab.parser.service.dto.City;
import com.wine.to.up.winelab.parser.service.dto.Wine;
import com.wine.to.up.winelab.parser.service.services.ParserService;
import com.wine.to.up.winelab.parser.service.services.UpdateService;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

@Slf4j
public class SendPageToCatalogJob implements Runnable {
    ParserService parserService;
    UpdateService updateService;

    int moscowWinePageCount;
    int moscowSparklingPageCount;
    int defaultWinePageCount;
    int defaultSparklingPageCount;

    int currentPageNumber;
    City currentCity;
    String currentCatalog;


    public SendPageToCatalogJob(ParserService parserService, UpdateService updateService) {
        this.parserService = parserService;
        this.updateService = updateService;

        this.moscowWinePageCount = parserService.getCatalogPageCount("wine", City.MOSCOW);
        this.moscowSparklingPageCount = parserService.getCatalogPageCount("sparkling", City.MOSCOW);
        this.defaultWinePageCount = parserService.getCatalogPageCount("wine", City.defaultCity());
        this.defaultSparklingPageCount = parserService.getCatalogPageCount("sparkling", City.defaultCity());

        this.currentPageNumber = 1;
        this.currentCity = City.values()[0];
        this.currentCatalog = "wine";
    }

    public void run() {
        try {
            List<Wine> wines = parserService.getFromCatalogPage(currentPageNumber, currentCatalog, currentCity);
            updateService.sendToKafka(wines);
            log.info("Sent page {} of {} from {} to catalog service", currentPageNumber, currentCatalog, currentCity.toString());
            nextPage();
        } catch (IndexOutOfBoundsException ex) {
            log.info("Parsing is finished.");
        } catch (Exception ex) {
            log.error("Catalog page {} parsing failed: {}", currentPageNumber, ex);
        }
    }

    private void nextPage() {
        if (currentCity == City.MOSCOW) {
            if (currentCatalog.equals("wine")) {
                if (currentPageNumber < moscowWinePageCount) {
                    currentPageNumber++;
                }
                else {
                    currentPageNumber = 1;
                    currentCatalog = "sparkling";
                }
            }
            else {
                if (currentPageNumber < moscowSparklingPageCount) {
                    currentPageNumber++;
                }
                else {
                    currentPageNumber = 1;
                    currentCatalog = "wine";
                    currentCity = City.values()[currentCity.ordinal() + 1];
                }
            }
        }
        else {
            if (currentCatalog.equals("wine")) {
                if (currentPageNumber < defaultWinePageCount) {
                    currentPageNumber++;
                }
                else {
                    currentPageNumber = 1;
                    currentCatalog = "sparkling";
                }
            }
            else {
                if (currentPageNumber < defaultSparklingPageCount) {
                    currentPageNumber++;
                }
                else {
                    currentPageNumber = 1;
                    currentCatalog = "wine";
                    currentCity = City.values()[currentCity.ordinal() + 1];
                }
            }
        }
    }

}
