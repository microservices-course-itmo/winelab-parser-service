package com.wine.to.up.winelab.parser.service.services;

import com.wine.to.up.commonlib.messaging.KafkaMessageSender;
import com.wine.to.up.parser.common.api.schema.ParserApi;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class KafkaService {

    private KafkaMessageSender<ParserApi.WineParsedEvent> kafkaSendMessageService;

    @Autowired
    public KafkaService(KafkaMessageSender<ParserApi.WineParsedEvent> kafkaSendMessageService) {
        this.kafkaSendMessageService = kafkaSendMessageService;
    }

    void sendWineParsedEvent(ParserApi.WineParsedEvent event) {
        kafkaSendMessageService.sendMessage(event);
    }

}