package com.wine.to.up.winelab.parser.service.controller;

import com.wine.to.up.winelab.parser.service.components.WineLabParserMetricsCollector;
import com.wine.to.up.winelab.parser.service.dto.WineToCsvConverter;
import com.wine.to.up.winelab.parser.service.services.ParserService;
import com.wine.to.up.winelab.parser.service.services.StorageService;
import com.wine.to.up.winelab.parser.service.services.UpdateService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.stubbing.Answer;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.util.NestedServletException;

import java.util.Map;

import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

@ExtendWith(MockitoExtension.class)
class ControllerTest {
    private MockMvc mockMvc;

    private ParserService parserService;
    private WineLabParserMetricsCollector metricsCollector;
    private WineToCsvConverter converter;
    private ParserController parserController;
    private StorageService storageService;
    private UpdateService updateService;

    @BeforeEach
    public void init() {
        parserService = Mockito.mock(ParserService.class);
        converter = Mockito.mock(WineToCsvConverter.class);
        metricsCollector = Mockito.mock(WineLabParserMetricsCollector.class);
        storageService = Mockito.mock(StorageService.class);
        updateService = Mockito.mock(UpdateService.class);
        parserController = new ParserController(parserService, converter, metricsCollector, storageService, updateService);
        mockMvc = MockMvcBuilders.standaloneSetup(parserController).build();
    }

    @Test
    void parseCatalog_throwArithmetic() throws Exception {
        try {
            mockMvc.perform(get("/parser/catalogs"));
        } catch (NestedServletException e) {
            Assertions.assertEquals(ArithmeticException.class, e.getCause().getClass());
        }
    }

    @Test
    void parseWine_throwNullPointer() throws Exception {
        try {
            mockMvc.perform(get("/parser/wine/1009581"));
        } catch (NestedServletException e) {
            Assertions.assertEquals(NullPointerException.class, e.getCause().getClass());
        }
    }

    @Test
    void parseCatalogPage_doesntThrow() throws Exception {
        Mockito.when(parserService.parseCatalogPage(anyString(), anyInt())).then(new Answer<Object>() {
            @Override
            public Object answer(InvocationOnMock invocationOnMock) throws Throwable {
                Thread.sleep(10);
                return Map.of();
            }
        });
        Assertions.assertDoesNotThrow(() -> mockMvc.perform(get("/parser/catalogs/wine/1")));
    }

}
