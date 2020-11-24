package com.wine.to.up.winelab.parser.service.job;

import com.wine.to.up.winelab.parser.service.components.WineLabParserMetricsCollector;
import com.wine.to.up.winelab.parser.service.services.ParserService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.test.util.ReflectionTestUtils;

import java.io.IOException;
import java.util.Map;

class ParseJobTest {
    ParserService mockedParserService;
    ParserService parserService;
    private final WineLabParserMetricsCollector metricsCollector;

    public ParseJobTest(WineLabParserMetricsCollector metricsCollector) {
        this.metricsCollector = metricsCollector;
    }

    @BeforeEach
    public void init() {
        parserService = new ParserService(metricsCollector);
        mockedParserService = Mockito.mock(ParserService.class);
        ReflectionTestUtils.setField(parserService, "siteURL", "www.winelab.ru");
        ReflectionTestUtils.setField(parserService, "protocol", "https://");
        ReflectionTestUtils.setField(parserService, "cookies", Map.of("currentPos", "S734", "currentRegion", "RU-SPE"));
        ReflectionTestUtils.setField(parserService, "catalogs", new String[]{"vino", "shampanskie-i-igristye-vina"});
        ReflectionTestUtils.setField(parserService, "filterSelector", "div.filter_block__container.js-facet.js-facet-values div[data-code=%s] div.filter_button span");
        ReflectionTestUtils.setField(parserService, "colorSelector", "Color");
        ReflectionTestUtils.setField(parserService, "sugarSelector", "SugarAmount");
        ReflectionTestUtils.setField(parserService, "countrySelector", "countryfiltr");
        ReflectionTestUtils.setField(parserService, "grapeSelector", "Sort");
        ReflectionTestUtils.setField(parserService, "manufacturerSelector", "manufacture");
        ReflectionTestUtils.setField(parserService, "categorySelector", "category");
    }

    @Test
    void testParseJobDoesntThrow() {

        try {
            //TODO: assert doesnt throw by log analysis (check out UpdateServiceTest)
            Mockito.when(mockedParserService.parseCatalogs()).thenReturn(Map.of());
            ParseJob job = new ParseJob(mockedParserService);
            Assertions.assertDoesNotThrow(job::parseCatalogs);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}