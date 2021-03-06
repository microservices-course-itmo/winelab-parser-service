package com.wine.to.up.winelab.parser.service.services;

import com.wine.to.up.commonlib.logging.EventLogger;
import com.wine.to.up.parser.common.api.schema.ParserApi;
import com.wine.to.up.winelab.parser.service.components.WineLabParserMetricsCollector;
import com.wine.to.up.winelab.parser.service.dto.Wine;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.test.util.ReflectionTestUtils;

import java.io.File;
import java.io.IOException;
import java.util.Map;

public class ParserServiceLocalDocTest {

    ParserServiceTest a = new ParserServiceTest();

    @BeforeEach
    void callInit() {
        a.init();
    }

    @Test
    void testParsedValuesEqualExpectedLocal() {
        Wine wine = a.parserService.parseProduct(1014769);
        ParserApi.Wine apiWine = wine.toParserWine();

        Assertions.assertEquals("Вино Saga Domaine Barons de Rothschild Bordeaux красное сухое 0,75 л", apiWine.getName());             //test the fields are being mapped correctly
        Assertions.assertEquals(1243.0f, apiWine.getOldPrice());
        Assertions.assertEquals("https://winelab.ru/product/1014769", apiWine.getLink());
        Assertions.assertEquals(599.0f, apiWine.getNewPrice());
        Assertions.assertEquals("https://jmrkpxyvei.a.trbcdn.net/medias/1014769.png-300Wx300H?context=bWFzdGVyfGltYWdlc3wzMzQwMXxpbWFnZS9wbmd8aW1hZ2VzL2g4NC9oZWMvODgzMjY0NjkzODY1NC5wbmd8OTk3MDg5NjdlMTk4NzlhNWM2MWQ0YzBiZGNhZmFmNGM3ZDViYmU1NWJmMzgyNDUwNWY0ZmRiYjczODdmOTJhOA", apiWine.getImage());
        Assertions.assertEquals("Domaine Barons de Rothschild", apiWine.getManufacturer());
        Assertions.assertEquals("SAGA", apiWine.getBrand());
        Assertions.assertEquals("Франция", apiWine.getCountry());
        Assertions.assertEquals(0.75f, apiWine.getCapacity());
        Assertions.assertEquals(ParserApi.Wine.Color.RED, apiWine.getColor());
        Assertions.assertEquals(ParserApi.Wine.Sugar.DRY, apiWine.getSugar());
        Assertions.assertEquals("Каберне Совиньон", apiWine.getGrapeSort(0));
        Assertions.assertEquals("Сага Бордо Руж – это вино на каждый день. Оно включает в себя традиционные сорта винограда: Каберне Совиньон, Мерло, которые смешиваются в разных пропорциях (в зависимости от урожая). Доминирующий Каберне Совиньон придает вину классическую элегантность, тогда как стиль DBR (Lafite) способствует созданию мягкого, нежного вкуса. Сорт: 60% Каберне Совиньон, 40% Мерло Время выдержки в дубовых бочках : 40% вина, 9 месяцев в нержавеющих емкостях Цвет: Насыщенный пурпурный. Аромат: Нежный и выразительный, с тонами черных ягод (ежевики и черешни) на фоне ванильных и жареных ноток. Вкус: Сочное и мягкое на вкус, с обильной танинной структурой и продолжительным ягодным послевкусием, отмеченным лакричным привкусом.", apiWine.getDescription());
        Assertions.assertEquals("Вино прекрасно сочетается с блюдами из красного мяса. Декантирование Примерно за 1 час Температура подачи: 16-18 °C", apiWine.getGastronomy());
    }

    private class ParserServiceStub extends ParserService {
        public ParserServiceStub(WineLabParserMetricsCollector metricsCollector) {
            super(metricsCollector);
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
