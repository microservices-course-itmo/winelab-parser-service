package com.wine.to.up.winelab.parser.service.controller;

import com.wine.to.up.winelab.parser.service.components.WineLabParserMetricsCollector;
import com.wine.to.up.winelab.parser.service.services.KafkaService;
import com.wine.to.up.winelab.parser.service.services.ParserService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.OverrideAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.util.NestedServletException;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

@ExtendWith(SpringExtension.class)
@WebMvcTest(ParserController.class)
@OverrideAutoConfiguration(enabled = true)
public class ControllerTest {
    @Autowired
    MockMvc mockMvc;
    @MockBean
    ParserService parserService;
    @MockBean
    KafkaService kafkaService;

    @MockBean
    private WineLabParserMetricsCollector metricsCollector;

    @Test
    public void parseCatalog_throwArithmetic() throws Exception {
        try {
            mockMvc.perform(get("/parser/catalogs"));
        } catch (NestedServletException e) {
            Assertions.assertEquals(ArithmeticException.class, e.getCause().getClass());
        }
    }

    @Test
    public void parseWine_throwNullPointer() throws Exception {
        try {
            mockMvc.perform(get("/parser/wine/1009581"));
        } catch (NestedServletException e) {
            Assertions.assertEquals(NullPointerException.class, e.getCause().getClass());
        }
    }
}
