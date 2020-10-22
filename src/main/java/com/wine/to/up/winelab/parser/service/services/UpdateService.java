package com.wine.to.up.winelab.parser.service.services;

import com.wine.to.up.winelab.parser.service.dto.Wine;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;

@Service
@Slf4j
public class UpdateService {
    @Autowired
    private KafkaService kafkaService;
    @Autowired
    private ParserService parserService;

    public void updateCatalog() {
        try {
            List<Integer> ids = parserService.parseCatalogs();
            ids.forEach(id -> {
                try {
                    Wine wine = parserService.parseProduct(id);
                    kafkaService.sendWine(wine);
                } catch (IOException e) {
                    log.error("Parse product with id = {} error, {}", id, e.toString());

                }
            });
        } catch(IOException e) {
            log.error("Update catalog error, {}",e.toString());
        }
    }
}
