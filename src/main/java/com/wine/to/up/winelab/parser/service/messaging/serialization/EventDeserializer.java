package com.wine.to.up.winelab.parser.service.messaging.serialization;

import com.google.protobuf.InvalidProtocolBufferException;
import com.wine.to.up.demo.service.api.message.KafkaMessageSentEventOuterClass.KafkaMessageSentEvent;
import com.wine.to.up.parser.common.api.schema.ParserApi;
import com.wine.to.up.winelab.parser.service.dto.Wine;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.common.serialization.Deserializer;
import org.springframework.util.SerializationUtils;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectInputStream;

/**
 * Deserializer for {@link KafkaMessageSentEvent}
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
