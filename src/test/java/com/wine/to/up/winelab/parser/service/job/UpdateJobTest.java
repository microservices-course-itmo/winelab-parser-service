package com.wine.to.up.winelab.parser.service.job;

import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.read.ListAppender;
import com.wine.to.up.winelab.parser.service.services.KafkaService;
import com.wine.to.up.winelab.parser.service.services.ParserService;
import com.wine.to.up.winelab.parser.service.services.UpdateService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.slf4j.LoggerFactory;
import org.springframework.test.util.ReflectionTestUtils;


public class UpdateJobTest {
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
    public void testUpdateJobDoesntThrow() {

        UpdateWineLabJob job = new UpdateWineLabJob();
        ReflectionTestUtils.setField(job, "updateService", updateService);
        Assertions.assertDoesNotThrow(job::runJob);
    }
}
