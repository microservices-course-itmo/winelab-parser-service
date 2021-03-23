package com.wine.to.up.winelab.parser.service.dto;

import com.wine.to.up.commonlib.logging.EventLogger;
import com.wine.to.up.parser.common.api.schema.ParserApi;
import com.wine.to.up.winelab.parser.service.components.WineLabParserMetricsCollector;
import com.wine.to.up.winelab.parser.service.repositories.WineRepository;
import com.wine.to.up.winelab.parser.service.services.ParserService;
import com.wine.to.up.winelab.parser.service.testutils.TestUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.test.util.ReflectionTestUtils;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Map;

public class CsvConverterTest {
    ParserService parserService;
    ParserService mockedParserService;
    WineLabParserMetricsCollector metricsCollector;
    WineRepository repository;
    private static final String csvExpected = "\"NAME\",\"LINK\",\"OLD_PRICE\",\"NEW_PRICE\",\"IMAGE\",\"MANUFACTURER\",\"BRAND\",\"COUNTRY\",\"REGION\",\"VOLUME\",\"ALCOHOL_CONTENT\",\"SPARKLING\",\"COLOR\",\"SUGAR\",\"GRAPE_SORT\",\"DESCRIPTION\",\"GASTRONOMY\"\n" +
            "\"Вино Saga Domaine Barons de Rothschild Bordeaux красное сухое 0,75 л\",\"https://winelab.ru/product/1014769\",\"1243\",\"599.0\",\"https://winelab.ru/medias/1014769.png-300Wx300H?context=bWFzdGVyfGltYWdlc3wzMzQwMXxpbWFnZS9wbmd8aW1hZ2VzL2g4NC9oZWMvODgzMjY0NjkzODY1NC5wbmd8OTk3MDg5NjdlMTk4NzlhNWM2MWQ0YzBiZGNhZmFmNGM3ZDViYmU1NWJmMzgyNDUwNWY0ZmRiYjczODdmOTJhOA\",\"Domaine Barons de Rothschild\",\"SAGA\",\"Франция\",\"Бордо\",\"0.75\",\"13\",\"false\",\"RED\",\"DRY\",\"Каберне Совиньон\",\"Сага Бордо Руж – это вино на каждый день. Оно включает в себя традиционные сорта винограда: Каберне Совиньон, Мерло, которые смешиваются в разных пропорциях (в зависимости от урожая). Доминирующий Каберне Совиньон придает вину классическую элегантность, тогда как стиль DBR (Lafite) способствует созданию мягкого, нежного вкуса. Сорт: 60% Каберне Совиньон, 40% Мерло Время выдержки в дубовых бочках : 40% вина, 9 месяцев в нержавеющих емкостях Цвет: Насыщенный пурпурный. Аромат: Нежный и выразительный, с тонами черных ягод (ежевики и черешни) на фоне ванильных и жареных ноток. Вкус: Сочное и мягкое на вкус, с обильной танинной структурой и продолжительным ягодным послевкусием, отмеченным лакричным привкусом.\",\"Вино прекрасно сочетается с блюдами из красного мяса. Декантирование Примерно за 1 час Температура подачи: 16-18 °C\"\n";

    @BeforeEach
    public void init() {
        metricsCollector = Mockito.mock(WineLabParserMetricsCollector.class);
        repository = Mockito.mock(WineRepository.class);
        parserService = new ParserService(metricsCollector, repository);
        mockedParserService = Mockito.mock(ParserService.class);
        TestUtils.setParserServiceFields(parserService);
    }

    @Test
    void testCsvConverter() {
        Wine wine = parserService.parseProduct(1014769);
        WineToCsvConverter converter = new WineToCsvConverter();
        String message = converter.convert(Arrays.asList(wine));
        Assertions.assertEquals(csvExpected, message);
    }

    private class ParserServiceStub extends ParserService {
        public ParserServiceStub(WineLabParserMetricsCollector metricsCollector) {
            super(metricsCollector, repository);
        }
        @Override
        protected Document getDocument(String url) throws IOException {
            StringBuffer localURLBuffer = new StringBuffer(url
                    .replaceFirst("https://winelab.ru/", "src/test/resources/pages/")
                    .replaceAll("\\?*", "")
                    .replaceAll(";", ""))
                    .append(".html");
            String localURL = localURLBuffer.toString();
            File localPage = new File(localURL);
            System.out.println();
            return Jsoup.parse(localPage, "UTF-8");
        }
    }
}
