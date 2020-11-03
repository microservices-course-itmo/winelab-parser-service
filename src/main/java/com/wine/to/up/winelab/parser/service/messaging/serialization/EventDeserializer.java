package com.wine.to.up.winelab.parser.service.messaging.serialization;

import com.wine.to.up.parser.common.api.schema.ParserApi;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.common.serialization.Deserializer;
import org.springframework.util.SerializationUtils;

/**
 * Deserializer for {@link ParserApi.Wine}
 */
@Slf4j
public class EventDeserializer implements Deserializer<ParserApi.Wine> {
    /**
     * {@inheritDoc}
     */
    @Override
    public ParserApi.Wine deserialize(String topic, byte[] bytes) {
        log.info("Deserializing message from topic: {}.", topic);
        return (ParserApi.Wine) SerializationUtils.deserialize(bytes);
    }
}
