package com.wine.to.up.winelab.parser.service.job;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.read.ListAppender;
import com.wine.to.up.commonlib.logging.EventLogger;
import com.wine.to.up.parser.common.api.schema.ParserApi;
import com.wine.to.up.winelab.parser.service.components.WineLabParserMetricsCollector;
import com.wine.to.up.winelab.parser.service.repositories.WineRepository;
import com.wine.to.up.winelab.parser.service.services.ParserService;
import com.wine.to.up.winelab.parser.service.services.UpdateService;
import com.wine.to.up.winelab.parser.service.testutils.TestUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.slf4j.LoggerFactory;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;
import java.util.Map;

class ParseJobTest {
    ParserService mockedParserService;
    ParserService parserService;
    UpdateService mockedUpdateService;
    WineLabParserMetricsCollector metricsCollector;
    WineRepository repository;
    ListAppender<ILoggingEvent> listAppender;

    @BeforeEach
    public void init() {
        metricsCollector = Mockito.mock(WineLabParserMetricsCollector.class);
        repository = Mockito.mock(WineRepository.class);
        parserService = new ParserService(metricsCollector, repository);
        mockedParserService = Mockito.mock(ParserService.class);
        mockedUpdateService = Mockito.mock(UpdateService.class);
        TestUtils.setParserServiceFields(parserService);
        Logger logger = (Logger) LoggerFactory.getLogger(UpdateService.class);
        listAppender = new ListAppender<>();
        listAppender.start();
        logger.addAppender(listAppender);
    }

    @Test
    void testParseJobDoesntThrow() {
        Mockito.when(mockedParserService.parseCatalogs()).thenReturn(Map.of());
        ParseJob job = new ParseJob(mockedParserService, mockedUpdateService, metricsCollector);
        Assertions.assertDoesNotThrow(job::setPeriodicCatalogUpdateJob);
        List<ILoggingEvent> logsList = listAppender.list;
        Assertions.assertFalse(logsList.stream().anyMatch(it -> it.getLevel() == Level.ERROR));
    }
}