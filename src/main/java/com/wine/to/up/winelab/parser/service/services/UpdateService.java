package com.wine.to.up.winelab.parser.service.services;

import com.wine.to.up.winelab.parser.service.dto.Wine;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Map;

@Service
@Slf4j
public class UpdateService {
    @Autowired
    private KafkaService kafkaService;
    @Autowired
    private ParserService parserService;

    public void updateCatalog() {
        try {
            Map<Integer, Wine> wines = parserService.parseCatalogs();
            wines.forEach((id, wine) ->
                    kafkaService.sendWine(wine)
            );
        } catch (IOException e) {
            log.error("Update catalog error, {}", e.toString());
        }
    }
}