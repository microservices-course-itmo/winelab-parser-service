package com.wine.to.up.winelab.parser.service.messaging;

import com.wine.to.up.commonlib.messaging.KafkaMessageHandler;
import com.wine.to.up.demo.service.api.message.KafkaMessageSentEventOuterClass.KafkaMessageSentEvent;
import com.wine.to.up.winelab.parser.service.domain.entity.Message;
import com.wine.to.up.winelab.parser.service.dto.Wine;
import com.wine.to.up.winelab.parser.service.repository.MessageRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.concurrent.atomic.AtomicInteger;

@Component
@Slf4j
public class WineTopicKafkaMessageHandler implements KafkaMessageHandler<Wine> {
    private final AtomicInteger counter = new AtomicInteger(0);

    @Override
    public void handle(Wine wine) {
        counter.incrementAndGet();
        log.info("Wine received wine with name {}, number of messages: {}", wine.getClass().getName(), counter.get());
    }
}