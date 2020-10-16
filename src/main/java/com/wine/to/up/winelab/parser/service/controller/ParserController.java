package com.wine.to.up.winelab.parser.service.controller;

import com.wine.to.up.winelab.parser.service.dto.Wine;
import com.wine.to.up.winelab.parser.service.repository.MessageRepository;
import com.wine.to.up.winelab.parser.service.services.ParserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.Map;

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

    @GetMapping("/wine")
    public void parseWine(@RequestParam int productID) {
        try {
            Wine wine = parserService.parseProduct(productID, null, null);
            log.info(wine.toString());
        } catch (IOException ex) {
            log.error(ex.getMessage());
        }
    }

    @GetMapping
    public void parseCatalogs() {
        log.info("Parsing started!");
        try {
            Map<Integer, Wine> wines = parserService.parseCatalogs();
            for (Map.Entry<Integer, Wine> entry : wines.entrySet()) {
                log.info(entry.getValue().toString());
                /* TODO
                    Transalte to ApiWine
                    Message class (https://github.com/microservices-course-itmo/parser-common-api/blob/dev/src/main/java/com/wine/to/up/parser/common/api/schema/parser_api.proto)
                 */
            }
            log.info("Parsing done!");
        } catch (IOException ex) {
            log.error(ex.getMessage());
        }
    }
}