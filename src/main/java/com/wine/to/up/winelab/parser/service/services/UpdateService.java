package com.wine.to.up.winelab.parser.service.services;

import com.wine.to.up.parser.common.api.schema.ParserApi;
import com.wine.to.up.winelab.parser.service.components.WineLabParserMetricsCollector;
import com.wine.to.up.winelab.parser.service.dto.Wine;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Service;

import java.util.List;
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
    @Autowired
    private StorageService storageService;

    @Value("${parser.address}")
    private String siteURL;

    public void sendAllToCatalog() {
        final int CHUNK_WINE_COUNT = 100;
        try {
            List<ParserApi.Wine> apiWines = storageService.getAll()
                    .stream()
                    .map(Wine::toParserWine)
                    .collect(Collectors.toList());
            for (int start = 0; start < apiWines.size(); start += CHUNK_WINE_COUNT) {
                int end = min(start + CHUNK_WINE_COUNT, apiWines.size());
                ParserApi.WineParsedEvent.Builder eventBuilder = ParserApi.WineParsedEvent.newBuilder()
                        .addAllWines(apiWines.subList(start, end));
                if (siteURL != null) {
                    eventBuilder.setShopLink(siteURL);
                }
                ParserApi.WineParsedEvent event = eventBuilder.build();
                kafkaService.sendWineParsedEvent(event);
            }
        }
        catch (NullPointerException ex){
            log.error("Couldn't send wines to catalog - storage service returned null", ex);
        }
    }

    public void sendNextChunkToCatalog() {
        try {
            List<ParserApi.Wine> apiWines = storageService.getNextChunk()
                    .stream()
                    .map(Wine::toParserWine)
                    .collect(Collectors.toList());
            ParserApi.WineParsedEvent.Builder eventBuilder = ParserApi.WineParsedEvent.newBuilder()
                    .addAllWines(apiWines);
            if (siteURL != null) {
                eventBuilder.setShopLink(siteURL);
            }
            ParserApi.WineParsedEvent event = eventBuilder.build();
            kafkaService.sendWineParsedEvent(event);
        }
        catch (NullPointerException ex){
            log.error("Couldn't send wines to catalog - storage service returned null", ex);
        }
    }
}