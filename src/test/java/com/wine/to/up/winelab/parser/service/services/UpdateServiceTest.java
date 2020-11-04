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

import java.io.IOException;
import java.util.List;

public class UpdateServiceTest {
    ParserService mockedParser;
    KafkaService mockedKafka;
    UpdateService updateService;
    ListAppender<ILoggingEvent> listAppender;

    @BeforeEach
    public void init() {
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
    public void testUpdateDoesntThrow() throws IOException {
        Assertions.assertDoesNotThrow(updateService::updateCatalog);
        List<ILoggingEvent> logsList = listAppender.list;
        Assertions.assertFalse(logsList.stream().anyMatch(it -> it.getLevel() == Level.ERROR));
    }

    @Test
    public void testUpdateThrows() throws IOException {
        Mockito.when(mockedParser.parseCatalogs()).thenThrow(new IOException());
        Assertions.assertDoesNotThrow(updateService::updateCatalog);
        List<ILoggingEvent> logsList = listAppender.list;
        Assertions.assertTrue(logsList.stream().anyMatch(it -> it.getLevel() == Level.ERROR));
    }

}
