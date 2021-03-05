package com.wine.to.up.winelab.parser.service.controller;

import com.wine.to.up.winelab.parser.service.dto.Wine;
import com.wine.to.up.winelab.parser.service.dto.WineLocalInfo;
import com.wine.to.up.winelab.parser.service.dto.WineToCsvConverter;
import com.wine.to.up.winelab.parser.service.job.UpdateWineLabJob;
import com.wine.to.up.winelab.parser.service.services.ParserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * The controller for parser REST endpoints
 *
 * @author Somov Artyom
 */
@RestController
@RequestMapping("/parser")
@Slf4j
@Configuration
public class ParserController {
    private final ParserService parserService;
    private final UpdateWineLabJob job;
    private final WineToCsvConverter converter;

    public ParserController(ParserService parserService, UpdateWineLabJob job, WineToCsvConverter converter) {
        this.parserService = parserService;
        this.job = job;
        this.converter = converter;
    }

    /**
     * Endpoint for parsing one specific wine by id given
     *
     * @param productID a product id on winelab.ru of wine to be parsed
     */
    @GetMapping("/wine/{id}")
    public ResponseEntity<Object> parseWine(@PathVariable(value = "id") int productID) {
        Wine wine = parserService.parseProduct(productID);
        if (wine != null) {
            log.debug(wine.toString());
            return ResponseEntity.ok(wine);
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    /**
     * Endpoint for parsing all the wine-related catalogs
     *
     * @return ResponseEntity
     */
    @GetMapping("/catalogs")
    public ResponseEntity<Object> parseCatalogs() {
        log.info("Parsing started!");
        long begin = System.currentTimeMillis();

        Map<Integer, Wine> wines = parserService.parseCatalogs();
        long end = System.currentTimeMillis();
        long timeElapsedTotal = end - begin;
        log.info("Time elapsed total: {} ", String.format("%d min %d sec",
                TimeUnit.MILLISECONDS.toMinutes(timeElapsedTotal),
                TimeUnit.MILLISECONDS.toSeconds(timeElapsedTotal) -
                        TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(timeElapsedTotal))
        ));
        if (wines.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        List<String> responseData = wines.values()
                .stream()
                .map(Wine::toString)
                .collect(Collectors.toList());
        return ResponseEntity.ok(responseData);
    }

    /**
     * Endpoint for parsing single page of catalog
     *
     * @param catalog catalog to be parsed
     *                (either "wine" for casual wine or "sparkling" for sparkling wine)
     * @param page    page of the catalog to be parsed
     * @return ResponseEntity
     */
    @GetMapping("/catalogs/{catalog}/{page}")
    public ResponseEntity<Object> parseCatalogPage(
            @PathVariable(value = "catalog") String catalog,
            @PathVariable(value = "page") int page) {
        if (page < 1) {
            log.warn("Couldn't parse {}s' catalog page {} : Page number must be positive", catalog, page);
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        log.info("Parsing started!");
        long begin = System.currentTimeMillis();
        Map<Integer, Wine> wines = parserService.parseCatalogPage(catalog, page);
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
        List<String> responseData = wines.values()
                .stream()
                .map(Wine::toString)
                .collect(Collectors.toList());
        return ResponseEntity.ok(responseData);
    }

    /**
     * Endpoint for parsing all the wine-related catalogs and sending the result to kafka
     */
    @GetMapping("/update")
    public ResponseEntity<Object> updateCatalogs() {
        int count = job.runJob();
        return ResponseEntity.ok(String.format("Total %d wines sent", count));
    }

    /**
     * Endpoint for parsing catalog and returning it in CSV format
     */
    @GetMapping("/catalogs/csv")
    public ResponseEntity<Object> parseAsCsv() {
        log.info("Parsing as CSV started!");
        Map<Integer, Wine> wines = parserService.parseCatalogs();
        if (wines.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        String responseData = converter.convert(wines.values());
        return ResponseEntity.ok(responseData);
    }

    /**
     * Endpoint to retrieve all city-specific data (is wine in stock and what price it has)
     */
    @GetMapping("/wine/local/{id}")
    public ResponseEntity<List<WineLocalInfo>> parseWineLocalInfo(@PathVariable(value = "id") int productID) {
        List<WineLocalInfo> info = parserService.parseAllLocalInfo(productID);
        return ResponseEntity.ok(info);
    }
}