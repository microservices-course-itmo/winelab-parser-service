package com.wine.to.up.winelab.parser.service.messaging.serialization;

import com.wine.to.up.parser.common.api.schema.ParserApi;
import com.wine.to.up.winelab.parser.service.dto.Wine;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.common.serialization.Serializer;
import org.springframework.util.SerializationUtils;

/**
 * Serializer for {@link Wine}
 */
@Slf4j
public class EventSerializer implements Serializer<ParserApi.WineParsedEvent> {
    /**
     * {@inheritDoc}
     */
    @Override
    public byte[] serialize(String topic, ParserApi.WineParsedEvent data) {
        log.info("Serializing message from topic: {}. {}", topic, data);
        return SerializationUtils.serialize(data);
    }
}
