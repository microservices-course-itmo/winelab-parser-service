package com.wine.to.up.winelab.parser.service.controller;

import com.wine.to.up.winelab.parser.service.dto.Wine;
import com.wine.to.up.winelab.parser.service.job.UpdateWineLabJob;
import com.wine.to.up.winelab.parser.service.services.ParserService;
import com.wine.to.up.winelab.parser.service.services.UpdateService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * The controller for parser REST endpoints
 *
 * @author Somov Artyom
 */
@RestController
@RequestMapping("/parser")
@Slf4j
public class ParserController {
    @Autowired
    private final ParserService parserService;

    @Autowired
    private final UpdateService updateService;

    @Autowired
    private final UpdateWineLabJob job;

    public ParserController(ParserService parserService, UpdateService updateService, UpdateWineLabJob job) {
        this.parserService = parserService;
        this.updateService = updateService;
        this.job = job;
    }

    /**
     * Endpoint for parsing one specific wine by id given
     *
     * @param productID a product id on winelab.ru of wine to be parsed
     */
    @GetMapping("/wine/{id}")
    public void parseWine(@PathVariable(value = "id") int productID) {
        try {
            Wine wine = parserService.parseProduct(productID);
            log.info(wine.toString());
        } catch (IOException ex) {
            log.error("Error while parsing wine {} : ", productID, ex);
        }
    }

    /**
     * Endpoint for parsing all the wine-related catalogs
     */
    @GetMapping("/catalogs")
    public void parseCatalogs() {
        log.info("Parsing started!");
        long begin = System.currentTimeMillis();
        try {
            Map<Integer, Wine> wines = parserService.parseCatalogs();
            for (Wine wine : wines.values()) {
                log.info(wine.toString());
            }
            long end = System.currentTimeMillis();
            long timeElapsedTotal = end - begin;
            log.info("Time elapsed total: {} ", String.format("%d min %d sec",
                    TimeUnit.MILLISECONDS.toMinutes(timeElapsedTotal),
                    TimeUnit.MILLISECONDS.toSeconds(timeElapsedTotal) -
                            TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(timeElapsedTotal))
            ));

            long quantity = TimeUnit.MINUTES.toMillis(1) * (wines.size()) / timeElapsedTotal;
            log.info("Wines parsed quantity every minute {} ", quantity);
            log.info("Parsing done! Total {} wines parsed", wines.size());
        } catch (IOException ex) {
            log.error("Error while parsing catalogs : ", ex);
        }
    }

    /**
     * Endpoint for parsing single page of catalog
     *
     * @param catalog catalog to be parsed (either "wine" for casual wine or "sparkling" for sparkling wine)
     * @param page    page of the catalog to be parsed
     * @return ResponseEntity
     */
    @GetMapping("/catalogs/{catalog}/{page}")
    public ResponseEntity<Object> parseCatalogPage(
            @PathVariable(value = "catalog") String catalog,
            @PathVariable(value = "page") int page) {
        log.info("Parsing started!");
        long begin = System.currentTimeMillis();
        try {
            Map<Integer, Wine> wines = parserService.parseCatalogPage(catalog, page);
            for (Wine wine : wines.values()) {
                log.info(wine.toString());
            }
            long end = System.currentTimeMillis();
            long timeElapsedTotal = end - begin;
            log.info("Time elapsed total: {} ", String.format("%d min %d sec",
                    TimeUnit.MILLISECONDS.toMinutes(timeElapsedTotal),
                    TimeUnit.MILLISECONDS.toSeconds(timeElapsedTotal) -
                            TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(timeElapsedTotal))
            ));

            long quantity = TimeUnit.MINUTES.toMillis(1) * (wines.size()) / timeElapsedTotal;
            log.info("Wines parsed quantity every minute {} ", quantity);
            log.info("Parsing done! Total {} wines parsed", wines.size());
            var response_data = new ArrayList<String>();
            for (var wine : wines.values()) {
                response_data.add(wine.toString());
            }
            return ResponseEntity.ok(response_data);
        } catch (IOException ex) {
            log.error("Error while parsing {} page {} : ", catalog, page, ex);
            return new ResponseEntity<>(ex.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Endpoint for parsing all the wine-related catalogs and sending the result to kafka
     */
    @GetMapping("/update")
    public void updateCatalogs() {
        job.runJob();
    }
}