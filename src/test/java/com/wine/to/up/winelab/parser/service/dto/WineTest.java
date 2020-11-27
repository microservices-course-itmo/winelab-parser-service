package com.wine.to.up.winelab.parser.service.dto;
import com.wine.to.up.winelab.parser.service.components.WineLabParserMetricsCollector;
import com.wine.to.up.parser.common.api.schema.ParserApi;
import com.wine.to.up.winelab.parser.service.services.ParserService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Map;

class WineTest {
    ParserService parserService;
    ParserService mockedParserService;
    private final WineLabParserMetricsCollector metricsCollector;

   public WineTest(WineLabParserMetricsCollector metricsCollector) {
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
    void testMappedValuesNotNull() {
        try {
            Wine wine = parserService.parseProduct(1014769);
            ParserApi.Wine apiWine = wine.toParserWine();

            Assertions.assertNotNull(apiWine.getName());           //test the fields are not null/null (depends on the field)
            Assertions.assertNotNull(apiWine.getLink());
            Assertions.assertNotNull(apiWine.getImage());
            Assertions.assertNotNull(apiWine.getManufacturer());
            Assertions.assertNotNull(apiWine.getBrand());
            Assertions.assertNotNull(apiWine.getCountry());
            Assertions.assertNotNull(apiWine.getColor());
            Assertions.assertNotNull(apiWine.getSugar());
            Assertions.assertNotNull(apiWine.getRegion(0));
            Assertions.assertNotNull(apiWine.getGrapeSort(0));
            Assertions.assertNotNull(apiWine.getGastronomy());
            Assertions.assertNotNull(apiWine.getDescription());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    void testParsedValuesEqualExpected() {
        try {
            Wine wine = parserService.parseProduct(1014769);
            ParserApi.Wine apiWine = wine.toParserWine();

            Assertions.assertEquals("Вино Saga Domaine Barons de Rothschild Bordeaux красное сухое 0,75 л", apiWine.getName());             //test the fields are being mapped correctly
            Assertions.assertEquals((float) 1243.0, apiWine.getOldPrice());
            Assertions.assertEquals("https://www.winelab.ru/product/1014769", apiWine.getLink());
            Assertions.assertEquals((float) 599.0, apiWine.getNewPrice());
            Assertions.assertEquals("https://www.winelab.ru/medias/1014769.png-300Wx300H?context=bWFzdGVyfGltYWdlc3wzMzQwMXxpbWFnZS9wbmd8aW1hZ2VzL2g4NC9oZWMvODgzMjY0NjkzODY1NC5wbmd8OTk3MDg5NjdlMTk4NzlhNWM2MWQ0YzBiZGNhZmFmNGM3ZDViYmU1NWJmMzgyNDUwNWY0ZmRiYjczODdmOTJhOA", apiWine.getImage());
            Assertions.assertEquals("Domaine Barons de Rothschild", apiWine.getManufacturer());
            Assertions.assertEquals("SAGA", apiWine.getBrand());
            Assertions.assertEquals("Франция", apiWine.getCountry());
            Assertions.assertEquals((float) 0.75, apiWine.getCapacity());
            Assertions.assertEquals(ParserApi.Wine.Color.RED, apiWine.getColor());
            Assertions.assertEquals(ParserApi.Wine.Sugar.DRY, apiWine.getSugar());
            Assertions.assertEquals("Каберне Совиньон", apiWine.getGrapeSort(0));
            Assertions.assertEquals("Сага Бордо Руж – это вино на каждый день. Оно включает в себя традиционные сорта винограда: Каберне Совиньон, Мерло, которые смешиваются в разных пропорциях (в зависимости от урожая). Доминирующий Каберне Совиньон придает вину классическую элегантность, тогда как стиль DBR (Lafite) способствует созданию мягкого, нежного вкуса. Сорт: 60% Каберне Совиньон, 40% Мерло Время выдержки в дубовых бочках : 40% вина, 9 месяцев в нержавеющих емкостях Цвет: Насыщенный пурпурный. Аромат: Нежный и выразительный, с тонами черных ягод (ежевики и черешни) на фоне ванильных и жареных ноток. Вкус: Сочное и мягкое на вкус, с обильной танинной структурой и продолжительным ягодным послевкусием, отмеченным лакричным привкусом.", apiWine.getDescription());
            Assertions.assertEquals("Вино прекрасно сочетается с блюдами из красного мяса. Декантирование Примерно за 1 час Температура подачи: 16-18 °C", apiWine.getGastronomy());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}