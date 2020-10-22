package com.wine.to.up.winelab.parser.service.controller;

import com.wine.to.up.commonlib.messaging.KafkaMessageSender;
import com.wine.to.up.parser.common.api.schema.ParserApi;
import com.wine.to.up.winelab.parser.service.dto.Wine;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * REST controller of the service
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/kafka")
@Validated
@Slf4j
public class KafkaController {

    /**
     * Service for sending messages
     */
    private KafkaMessageSender<ParserApi.Wine> kafkaSendMessageService;

    @Autowired
    public KafkaController(KafkaMessageSender<ParserApi.Wine> kafkaSendMessageService) {
        this.kafkaSendMessageService = kafkaSendMessageService;
    }

    /**
     * Sends messages into the topic "test".
     * In fact now this service listen to that topic too. That means that it causes sending and reading messages
     */
    @PostMapping(value = "/send")
    public void sendWineWithName(@RequestBody String name) {
        Wine wine = new Wine();
        wine.setName(name);
        kafkaSendMessageService.sendMessage(wine.toParserWine());
    }
}
