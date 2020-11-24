package com.wine.to.up.winelab.parser.service.services;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.read.ListAppender;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.slf4j.LoggerFactory;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;

class UpdateServiceTest {
    ParserService mockedParser;
    KafkaService mockedKafka;
    UpdateService updateService;
    ListAppender<ILoggingEvent> listAppender;

    @BeforeEach
    void init() {
        mockedParser = Mockito.mock(ParserService.class);
        mockedKafka = Mockito.mock(KafkaService.class);
        updateService = new UpdateService();
        ReflectionTestUtils.setField(updateService, "kafkaService", mockedKafka);
        ReflectionTestUtils.setField(updateService, "parserService", mockedParser);
        Logger logger = (Logger) LoggerFactory.getLogger(UpdateService.class);
        listAppender = new ListAppender<>();
        listAppender.start();
        logger.addAppender(listAppender);
    }

    @Test
    void testUpdateDoesntThrow()    {
        Assertions.assertDoesNotThrow(updateService::updateCatalog);
        List<ILoggingEvent> logsList = listAppender.list;
        Assertions.assertFalse(logsList.stream().anyMatch(it -> it.getLevel() == Level.ERROR));
    }

}
