package com.wine.to.up.winelab.parser.service.controller;

import com.wine.to.up.winelab.parser.service.dto.ApiWine;
import com.wine.to.up.winelab.parser.service.dto.Wine;
import com.wine.to.up.winelab.parser.service.repository.MessageRepository;
import com.wine.to.up.winelab.parser.service.services.ParserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * The controller for parser REST endpoints
 *
 * @author Somov Artyom
 * */
@RestController
@RequestMapping("/parser")
@Slf4j
public class ParserController {

    private final MessageRepository messageRepository;
    private final ParserService parserService;

    @Autowired
    public ParserController(MessageRepository messageRepository, ParserService parserService) {
        this.messageRepository = messageRepository;
        this.parserService = parserService;
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
            log.error(ex.getMessage());
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
            log.info("Time elapsed total: {} ",String.format("%d min %d sec",
                    TimeUnit.MILLISECONDS.toMinutes(timeElapsedTotal),
                    TimeUnit.MILLISECONDS.toSeconds(timeElapsedTotal) -
                            TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(timeElapsedTotal))
            ));

            long quantity = (wines.size())/(TimeUnit.MILLISECONDS.toMinutes(timeElapsedTotal));
            log.info("Wines parsed quantity every minute {} ", quantity);
            log.info("Parsing done! Total {} wines parsed", wines.size());
        } catch (IOException ex) {
            log.error(ex.getMessage());
        }
    }
}