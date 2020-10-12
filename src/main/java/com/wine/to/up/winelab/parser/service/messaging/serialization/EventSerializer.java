package com.wine.to.up.winelab.parser.service.messaging.serialization;

import com.wine.to.up.demo.service.api.message.KafkaMessageSentEventOuterClass.KafkaMessageSentEvent;
import com.wine.to.up.winelab.parser.service.dto.Wine;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.common.serialization.Serializer;
import org.springframework.util.SerializationUtils;

/**
 * Serializer for {@link KafkaMessageSentEvent}
 */
@Slf4j
public class EventSerializer implements Serializer<Wine> {
    /**
     * {@inheritDoc}
     */
    @Override
    public byte[] serialize(String topic, Wine data) {
        log.info("Serializing message from topic: {}. {}", topic, data);
        return SerializationUtils.serialize(data);
    }
}
