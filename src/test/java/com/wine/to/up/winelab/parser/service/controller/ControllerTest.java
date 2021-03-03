package com.wine.to.up.winelab.parser.service.controller;

import com.wine.to.up.winelab.parser.service.dto.WineToCsvConverter;
import com.wine.to.up.winelab.parser.service.job.UpdateWineLabJob;
import com.wine.to.up.winelab.parser.service.services.KafkaService;
import com.wine.to.up.winelab.parser.service.services.ParserService;
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
    MockMvc mockMvc;

    ParserService parserService;
    KafkaService kafkaService;
    UpdateWineLabJob updateWineLabJob;
    private  WineToCsvConverter converter;
    private ParserController parserController;

    @BeforeEach
    public void init() {
        parserService = Mockito.mock(ParserService.class);
        kafkaService = Mockito.mock(KafkaService.class);
        updateWineLabJob = Mockito.mock(UpdateWineLabJob.class);
        converter = Mockito.mock(WineToCsvConverter.class);
        parserController  = new ParserController(parserService, updateWineLabJob, converter);
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
