package com.wine.to.up.winelab.parser.service.controller;

import com.wine.to.up.winelab.parser.service.domain.entity.Message;
import com.wine.to.up.winelab.parser.service.dto.Wine;
import com.wine.to.up.winelab.parser.service.repository.MessageRepository;
import com.wine.to.up.winelab.parser.service.services.Parser;
import io.swagger.models.auth.In;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.DataNode;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/parser")
@Slf4j
public class ParserController {

    @Autowired
    private MessageRepository messageRepository;

    @GetMapping
    public void parseProductPage(@RequestParam int productID) {
        Parser parser = Parser.getInstance();
        Wine wine = parser.parseProduct(productID);
        log.info(wine.toString());
    }

    @GetMapping("/home")
    public void parseHomePage() {
        Parser parser = Parser.getInstance();
        List<Integer> ids = parser.parseHome();
        log.info(ids.toString());
    }

    @GetMapping("/catalog")
    public void parseCatalog() {
        Parser parser = Parser.getInstance();
        List<Integer> ids = parser.parseCatalog();
        log.info(ids.toString());
    }
}