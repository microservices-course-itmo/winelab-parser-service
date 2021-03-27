package com.wine.to.up.winelab.parser.service.services;

import com.wine.to.up.parser.common.api.schema.ParserApi;
import com.wine.to.up.winelab.parser.service.components.WineLabParserMetricsCollector;
import com.wine.to.up.winelab.parser.service.dto.Wine;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static java.lang.Integer.min;

@Service
@Slf4j
@Configuration
public class UpdateService {
    @Autowired
    private KafkaService kafkaService;
    @Autowired
    private ParserService parserService;
    @Autowired
    private WineLabParserMetricsCollector metricsCollector;

    @Value("${parser.address}")
    private String siteURL;
    private String parserName;

    public void updateCatalog() {
        Map<Integer, Wine> wines = parserService.parseCatalogs();
        sendToKafka(new ArrayList<>(wines.values()));
    }

    public void sendToKafka(List<Wine> wines) {
        final int CHUNK_WINE_COUNT = 100;
        List<ParserApi.Wine> apiWines = wines
                .stream()
                .map(Wine::toParserWine)
                .collect(Collectors.toList());
        for (int start = 0; start < wines.size(); start += CHUNK_WINE_COUNT) {
            int end = min(start + CHUNK_WINE_COUNT, wines.size());
            ParserApi.WineParsedEvent.Builder eventBuilder = ParserApi.WineParsedEvent.newBuilder()
                    .addAllWines(apiWines.subList(start, end));
            if (siteURL != null) {
                eventBuilder.setShopLink(siteURL);
            }
            if (parserName != null) {
                eventBuilder.setParserName(parserName);
            }
            ParserApi.WineParsedEvent event = eventBuilder.build();
            kafkaService.sendWineParsedEvent(event);
        }
    }
}