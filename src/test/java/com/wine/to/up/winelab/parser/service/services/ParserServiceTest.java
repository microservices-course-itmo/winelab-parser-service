package com.wine.to.up.winelab.parser.service.services;

import com.wine.to.up.commonlib.logging.EventLogger;
import com.wine.to.up.parser.common.api.schema.ParserApi;
import com.wine.to.up.winelab.parser.service.components.WineLabParserMetricsCollector;
import com.wine.to.up.winelab.parser.service.dto.Wine;
import com.wine.to.up.winelab.parser.service.repositories.WineRepository;
import com.wine.to.up.winelab.parser.service.testutils.TestUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.test.util.ReflectionTestUtils;

import java.math.BigDecimal;
import java.util.Map;
import java.util.Objects;

class ParserServiceTest {
    ParserService parserService;
    ParserService mockedParserService;
    WineRepository repository;
    WineLabParserMetricsCollector metricsCollector;
    private static final String wineToStrReference = "Wine(name=Вино Berton Foundstone Shiraz красное сухое 0,75 л, link=https://winelab.ru/product/1009581, oldPrice=750, newPrice=675.0, image=https://jmrkpxyvei.a.trbcdn.net/medias/1009581.png-300Wx300H?context=bWFzdGVyfGltYWdlc3w0NTc2NXxpbWFnZS9wbmd8aW1hZ2VzL2hjOC9oMDcvODgzMjYxNzQ4MDIyMi5wbmd8NGUxN2NiMzk2YjUxOTVmOTBhOTcwMTAwY2I1YjljZWZhMTViY2ViODIzZTczYzgxYWE3YzlmYzEzZmVkMmM5ZQ, manufacturer=Berton Vineyards, brand=Berton Vinyard Foundstone, country=Австралия, region=null, volume=0.75, alcoholContent=13, sparkling=false, color=RED, sugar=DRY, grapeSort=Шираз, description=Регион: Юго-Восточная Австралия. Сорт винограда: 100% Шираз. Выдержка: чаны из нержавеющей стали. Цвет: насыщенный пурпурный с фиолетовым оттенком. Аромат: насыщенный выразительный с яркими нотами специй, спелой ежевики, сливы и легкими сладковатыми оттенками дуба, кофе, ванили и карамели. Вкус: полнотелый насыщенный с умеренно терпкими приятными шелковистыми танинами и оттенками ежевики, черешни, сливы и длительным послевкусием., gastronomy=Гастрономическое сочетание: стейк из говядины прожарки medium, свинина на косточке, твердые сыры, хамон, колбасы. Температура подачи: 14-16° С)";
    private static final String gastronomyReference = "Гастрономическое сочетание: стейк из говядины прожарки medium, свинина на косточке, твердые сыры, хамон, колбасы. Температура подачи: 14-16° С";

    @BeforeEach
    void init() {
        metricsCollector = Mockito.mock(WineLabParserMetricsCollector.class);
        parserService = new ParserService(metricsCollector, repository);
        mockedParserService = Mockito.mock(ParserService.class);
        TestUtils.setParserServiceFields(parserService);
    }

    @Test
    void testParsedValuesEqualExpected() {
        Wine wine = parserService.parseProduct(1009581);
        Assertions.assertEquals("Вино Berton Foundstone Shiraz красное сухое 0,75 л", wine.getName());             //test the fields are being parsed correctly
        //Assertions.assertEquals(BigDecimal.valueOf(750), wine.getOldPrice());
        Assertions.assertEquals("https://winelab.ru/product/1009581", wine.getLink());
        //Assertions.assertEquals(BigDecimal.valueOf(675.0f), wine.getNewPrice());
        Assertions.assertEquals("https://winelab.ru/medias/1009581.png-300Wx300H?context=bWFzdGVyfGltYWdlc3w0NTc2NXxpbWFnZS9wbmd8aW1hZ2VzL2hjOC9oMDcvODgzMjYxNzQ4MDIyMi5wbmd8NGUxN2NiMzk2YjUxOTVmOTBhOTcwMTAwY2I1YjljZWZhMTViY2ViODIzZTczYzgxYWE3YzlmYzEzZmVkMmM5ZQ", wine.getImage());
        Assertions.assertEquals("Berton Vineyards", wine.getManufacturer());
        Assertions.assertEquals("Berton Vinyard Foundstone", wine.getBrand());
        Assertions.assertEquals("Австралия", wine.getCountry());
        Assertions.assertEquals(BigDecimal.valueOf(0.75f), wine.getCapacity());
        Assertions.assertEquals(ParserApi.Wine.Color.RED, wine.getColor());
        Assertions.assertEquals(ParserApi.Wine.Sugar.DRY, wine.getSugar());
        Assertions.assertEquals("Шираз", wine.getGrapeSort());
        Assertions.assertEquals("Регион: Юго-Восточная Австралия. Сорт винограда: 100% Шираз. Выдержка: чаны из нержавеющей стали. Цвет: насыщенный пурпурный с фиолетовым оттенком. Аромат: насыщенный выразительный с яркими нотами специй, спелой ежевики, сливы и легкими сладковатыми оттенками дуба, кофе, ванили и карамели. Вкус: полнотелый насыщенный с умеренно терпкими приятными шелковистыми танинами и оттенками ежевики, черешни, сливы и длительным послевкусием.", wine.getDescription());
        //Assertions.assertEquals(wineToStrReference, wine.toString());
        Assertions.assertEquals(gastronomyReference, wine.getGastronomy());
    }

    @Test
    void testIdIsValid() {
        int id = 0;
        Wine wine = parserService.parseProduct(0);
        Assertions.assertNull(wine);
    }

    @Test
    void testParsedValuesNotNull() {
        Wine wine = parserService.parseProduct(1009581);
        Assertions.assertNotNull(wine.getName());           //test the fields are not null/null (depends on the field)
        Assertions.assertNotNull(wine.getLink());
        Assertions.assertNotNull(wine.getImage());
        Assertions.assertNotNull(wine.getManufacturer());
        Assertions.assertNotNull(wine.getBrand());
        Assertions.assertNotNull(wine.getCountry());
        Assertions.assertNull(wine.getRegion());
        Assertions.assertNotNull(wine.getDescription());
    }
    /*
    @Test
    void testParseCatalogsNotEmpty() {
        Map<Integer, Wine> wines = parserService.parseCatalogs();
        Assertions.assertFalse(wines.isEmpty());
        Assertions.assertFalse(wines.values().stream().anyMatch(Objects::isNull));
    }
    */
    @Test
    void testParseCatalogPageNotEmpty() {
        Map<Integer, Wine> wines = parserService.parseCatalogPage("wine", 1);
        Assertions.assertFalse(wines.isEmpty());
        Assertions.assertFalse(wines.values().stream().anyMatch(Objects::isNull));
    }

}