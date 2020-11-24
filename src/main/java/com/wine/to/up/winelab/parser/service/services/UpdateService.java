package com.wine.to.up.winelab.parser.service.services;

import com.wine.to.up.parser.common.api.schema.ParserApi;
import com.wine.to.up.winelab.parser.service.dto.Wine;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Slf4j
public class UpdateService {
    @Autowired
    private KafkaService kafkaService;
    @Autowired
    private ParserService parserService;

    @Value("${parser.siteURL}")
    private String siteURL;

    public void updateCatalog() {
        try {
            Map<Integer, Wine> wines = parserService.parseCatalogs();
            ParserApi.WineParsedEvent.Builder eventBuilder = ParserApi.WineParsedEvent.newBuilder()
                    .addAllWines(wines.values()
                            .stream()
                            .map(Wine::toParserWine)
                            .collect(Collectors.toList()));
            if (siteURL != null) {
                eventBuilder.setShopLink(siteURL);
            }
            kafkaService.sendWineParsedEvent(eventBuilder.build());
        } catch (IOException ex) {
            log.error("Update catalog error : ", ex);
        }
    }
}