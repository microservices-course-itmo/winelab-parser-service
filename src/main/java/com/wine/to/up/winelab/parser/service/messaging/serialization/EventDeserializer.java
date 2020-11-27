package com.wine.to.up.winelab.parser.service.messaging.serialization;

import com.wine.to.up.winelab.parser.service.dto.Wine;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.common.serialization.Deserializer;
import org.springframework.util.SerializationUtils;

/**
 * Deserializer for {@link Wine}
 */
@Slf4j
public class EventDeserializer implements Deserializer<Wine> {
    /**
     * {@inheritDoc}
     */
    @Override
    public Wine deserialize(String topic, byte[] bytes) {
        log.info("Deserializing message from topic: {}.", topic);
        return (Wine) SerializationUtils.deserialize(bytes);
    }
}
