package com.wine.to.up.winelab.parser.service.services;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.read.ListAppender;
import com.wine.to.up.winelab.parser.service.components.WineLabParserMetricsCollector;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.slf4j.LoggerFactory;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;
import java.util.Optional;

class UpdateServiceTest {
    ParserService mockedParser;
    KafkaService mockedKafka;
    WineLabParserMetricsCollector metricsCollector;
    UpdateService updateService;
    ListAppender<ILoggingEvent> listAppender;

    @BeforeEach
    public void init() {
        mockedParser = Mockito.mock(ParserService.class);
        mockedKafka = Mockito.mock(KafkaService.class);
        metricsCollector = Mockito.mock(WineLabParserMetricsCollector.class);
        updateService = new UpdateService();
        ReflectionTestUtils.setField(updateService, "kafkaService", mockedKafka);
        ReflectionTestUtils.setField(updateService, "parserService", mockedParser);
        ReflectionTestUtils.setField(updateService, "metricsCollector", metricsCollector);
        Logger logger = (Logger) LoggerFactory.getLogger(UpdateService.class);
        listAppender = new ListAppender<>();
        listAppender.start();
        logger.addAppender(listAppender);
    }

    @Test
    void testUpdateDoesntThrow() {
        Assertions.assertDoesNotThrow(() -> updateService.updateCatalog(Optional.empty()));
        List<ILoggingEvent> logsList = listAppender.list;
        Assertions.assertFalse(logsList.stream().anyMatch(it -> it.getLevel() == Level.ERROR));
    }
}
