package com.wine.to.up.winelab.parser.service.messaging;

import com.wine.to.up.commonlib.messaging.KafkaMessageHandler;
import com.wine.to.up.parser.common.api.schema.ParserApi;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.concurrent.atomic.AtomicInteger;

@Component
@Slf4j
public class WineTopicKafkaMessageHandler implements KafkaMessageHandler<ParserApi.WineParsedEvent> {
    private final AtomicInteger counter = new AtomicInteger(0);

    @Override
    public void handle(ParserApi.WineParsedEvent event) {
        counter.incrementAndGet();
        log.info("Wine received wine with name {}, number of messages: {}", event.getClass().getName(), counter.get());
    }
}