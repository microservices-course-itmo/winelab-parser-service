package com.wine.to.up.winelab.parser.service.services;

import com.wine.to.up.commonlib.messaging.KafkaMessageSender;
import com.wine.to.up.parser.common.api.schema.ParserApi;
import com.wine.to.up.winelab.parser.service.components.WineLabParserMetricsCollector;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class KafkaService {

    private KafkaMessageSender<ParserApi.WineParsedEvent> kafkaSendMessageService;
    private WineLabParserMetricsCollector metricsCollector;

    @Autowired
    public KafkaService(KafkaMessageSender<ParserApi.WineParsedEvent> kafkaSendMessageService, WineLabParserMetricsCollector metricsCollector) {
        this.kafkaSendMessageService = kafkaSendMessageService;
        this.metricsCollector = metricsCollector;
    }

    void sendWineParsedEvent(ParserApi.WineParsedEvent event) {
        try {
            metricsCollector.countWinesPublishedToKafka(event.getWinesCount());
            kafkaSendMessageService.sendMessage(event);
            log.info("Wine {} successfully sent to kafka!", event.getLink());
        } catch(Exception exception) {
            log.error("Error while sending wine to kafka! {}", exception.toString());
        }
        
    }

}