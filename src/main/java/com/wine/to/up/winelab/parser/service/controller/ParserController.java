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
import java.util.List;

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

    @GetMapping
    public void parseProductPage(@RequestParam int productID) {
        try {
            Wine wine = parserService.parseProduct(productID);
            log.info(wine.toString());
        } catch (IOException ex) {
            log.error(ex.getMessage());
        }
    }

    @GetMapping("/home")
    public void parseHomePage() {
        try {
            List<Integer> ids = parserService.parseHome();
            log.info(ids.toString());
        } catch (IOException ex) {
            log.error(ex.getMessage());
        }
    }

    @GetMapping("/catalog")
    public void parseCatalogs() {
        try {
            List<Integer> ids = parserService.parseCatalogs();
            log.info(ids.toString());
        } catch (IOException ex) {
            log.error(ex.getMessage());
        }
    }
}