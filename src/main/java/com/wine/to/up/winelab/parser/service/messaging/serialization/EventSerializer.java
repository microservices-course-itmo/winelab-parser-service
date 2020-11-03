package com.wine.to.up.winelab.parser.service.messaging.serialization;

import com.wine.to.up.parser.common.api.schema.ParserApi;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.common.serialization.Serializer;

/**
 * Serializer for {@link ParserApi.Wine}
 */
@Slf4j
public class EventSerializer implements Serializer<ParserApi.Wine> {
    /**
     * {@inheritDoc}
     */
    @Override
    public byte[] serialize(String topic, ParserApi.Wine data) {
        return data.toByteArray();
    }
}
