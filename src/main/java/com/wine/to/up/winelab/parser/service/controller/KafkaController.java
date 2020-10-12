package com.wine.to.up.winelab.parser.service.controller;

import com.google.protobuf.ByteString;
import com.wine.to.up.commonlib.messaging.KafkaMessageSender;
import com.wine.to.up.demo.service.api.dto.DemoServiceMessage;
import com.wine.to.up.demo.service.api.message.KafkaMessageHeaderOuterClass;
import com.wine.to.up.demo.service.api.message.KafkaMessageSentEventOuterClass.KafkaMessageSentEvent;
import com.wine.to.up.winelab.parser.service.dto.Wine;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collections;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Stream;

import static java.util.stream.Collectors.toList;

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
    private KafkaMessageSender<Wine> kafkaSendMessageService;

    @Autowired
    public KafkaController(KafkaMessageSender<Wine> kafkaSendMessageService) {
        this.kafkaSendMessageService = kafkaSendMessageService;
    }

    /**
     * Sends messages into the topic "test".
     * In fact now this service listen to that topic too. That means that it causes sending and reading messages
     */
    @PostMapping(value = "/send")
    public void sendMessage(@RequestBody String name) {
        Wine wine = new Wine();
        wine.setName(name);
        sendMessageWithHeaders(wine);
    }

    /**
     * See {@link #sendMessage(String)}
     * Sends message with headers
     */
    @PostMapping(value = "/send/headers")
    public void sendMessageWithHeaders(@RequestBody Wine wine) {

        kafkaSendMessageService.sendMessage(wine);
    }
}
