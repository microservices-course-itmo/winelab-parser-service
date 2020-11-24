package com.wine.to.up.winelab.parser.service.services;

import com.wine.to.up.parser.common.api.schema.ParserApi;
import com.wine.to.up.winelab.parser.service.dto.Wine;
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
    private static final String wineToStrReference = "Wine(name=Вино Berton Foundstone Shiraz красное сухое 0,75 л, link=https://www.winelab.ru/product/1009581, oldPrice=750, newPrice=649.0, image=https://www.winelab.ru/medias/1009581.png-300Wx300H?context=bWFzdGVyfGltYWdlc3w0NTc2NXxpbWFnZS9wbmd8aW1hZ2VzL2hjOC9oMDcvODgzMjYxNzQ4MDIyMi5wbmd8NGUxN2NiMzk2YjUxOTVmOTBhOTcwMTAwY2I1YjljZWZhMTViY2ViODIzZTczYzgxYWE3YzlmYzEzZmVkMmM5ZQ, manufacturer=Berton Vineyards, brand=Berton Vinyard Foundstone, country=Австралия, region=null, volume=0.75, alcoholContent=13, sparkling=false, color=RED, sugar=DRY, grapeSort=Шираз, description=Регион: Юго-Восточная Австралия. Сорт винограда: 100% Шираз. Выдержка: чаны из нержавеющей стали. Цвет: насыщенный пурпурный с фиолетовым оттенком. Аромат: насыщенный выразительный с яркими нотами специй, спелой ежевики, сливы и легкими сладковатыми оттенками дуба, кофе, ванили и карамели. Вкус: полнотелый насыщенный с умеренно терпкими приятными шелковистыми танинами и оттенками ежевики, черешни, сливы и длительным послевкусием., gastronomy=Гастрономическое сочетание: стейк из говядины прожарки medium, свинина на косточке, твердые сыры, хамон, колбасы. Температура подачи: 14-16° С)";
    private static final String gastronomyReference = "Гастрономическое сочетание: стейк из говядины прожарки medium, свинина на косточке, твердые сыры, хамон, колбасы. Температура подачи: 14-16° С";

    @BeforeEach
    void init() {
        parserService = new ParserService();
        mockedParserService = Mockito.mock(ParserService.class);
        ReflectionTestUtils.setField(parserService, "siteURL", "www.winelab.ru");
        ReflectionTestUtils.setField(parserService, "protocol", "https://");
        ReflectionTestUtils.setField(parserService, "cookies", Map.of("currentPos", "S734", "currentRegion", "RU-SPE"));
        ReflectionTestUtils.setField(parserService, "catalogs", Map.of("wine", "vino", "sparkling", "shampanskie-i-igristye-vina"));
        ReflectionTestUtils.setField(parserService, "filterSelector", "div.filter_block__container.js-facet.js-facet-values div[data-code=%s] div.filter_button span");
        ReflectionTestUtils.setField(parserService, "colorSelector", "Color");
        ReflectionTestUtils.setField(parserService, "sugarSelector", "SugarAmount");
        ReflectionTestUtils.setField(parserService, "countrySelector", "countryfiltr");
        ReflectionTestUtils.setField(parserService, "grapeSelector", "Sort");
        ReflectionTestUtils.setField(parserService, "manufacturerSelector", "manufacture");
        ReflectionTestUtils.setField(parserService, "categorySelector", "category");
    }

    @Test
    void testParsedValuesEqualExpected() {
        try {
            Wine wine = parserService.parseProduct(1009581);
            Assertions.assertEquals("Вино Berton Foundstone Shiraz красное сухое 0,75 л", wine.getName());             //test the fields are being parsed correctly
            Assertions.assertEquals(BigDecimal.valueOf(750), wine.getOldPrice());
            Assertions.assertEquals("https://www.winelab.ru/product/1009581", wine.getLink());
            Assertions.assertEquals(BigDecimal.valueOf(649.0f), wine.getNewPrice());
            Assertions.assertEquals("https://www.winelab.ru/medias/1009581.png-300Wx300H?context=bWFzdGVyfGltYWdlc3w0NTc2NXxpbWFnZS9wbmd8aW1hZ2VzL2hjOC9oMDcvODgzMjYxNzQ4MDIyMi5wbmd8NGUxN2NiMzk2YjUxOTVmOTBhOTcwMTAwY2I1YjljZWZhMTViY2ViODIzZTczYzgxYWE3YzlmYzEzZmVkMmM5ZQ", wine.getImage());
            Assertions.assertEquals("Berton Vineyards", wine.getManufacturer());
            Assertions.assertEquals("Berton Vinyard Foundstone", wine.getBrand());
            Assertions.assertEquals("Австралия", wine.getCountry());
            Assertions.assertEquals(BigDecimal.valueOf(0.75f), wine.getVolume());
            Assertions.assertEquals(ParserApi.Wine.Color.RED, wine.getColor());
            Assertions.assertEquals(ParserApi.Wine.Sugar.DRY, wine.getSugar());
            Assertions.assertEquals("Шираз", wine.getGrapeSort());
            Assertions.assertEquals("Регион: Юго-Восточная Австралия. Сорт винограда: 100% Шираз. Выдержка: чаны из нержавеющей стали. Цвет: насыщенный пурпурный с фиолетовым оттенком. Аромат: насыщенный выразительный с яркими нотами специй, спелой ежевики, сливы и легкими сладковатыми оттенками дуба, кофе, ванили и карамели. Вкус: полнотелый насыщенный с умеренно терпкими приятными шелковистыми танинами и оттенками ежевики, черешни, сливы и длительным послевкусием.", wine.getDescription());
            Assertions.assertEquals(wineToStrReference, wine.toString());
            Assertions.assertEquals(gastronomyReference, wine.getGastronomy());
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    @Test
    void testIdIsValid() {
        try {
            int id = 0;
            Wine wine = parserService.parseProduct(0);
            Assertions.assertNull(wine);
        } catch (Exception ex) {
            ex.printStackTrace();
        }

    }

    @Test
    void testParsedValuesNotNull() {
        try {
            Wine wine = parserService.parseProduct(1009581);
            Assertions.assertNotNull(wine.getName());           //test the fields are not null/null (depends on the field)
            Assertions.assertNotNull(wine.getLink());
            Assertions.assertNotNull(wine.getImage());
            Assertions.assertNotNull(wine.getManufacturer());
            Assertions.assertNotNull(wine.getBrand());
            Assertions.assertNotNull(wine.getCountry());
            Assertions.assertNull(wine.getRegion());
            Assertions.assertNotNull(wine.getDescription());
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    @Test
    void testParseCatalogsNotEmpty() {
        try {
            Map<Integer, Wine> wines = parserService.parseCatalogs();
            Assertions.assertFalse(wines.isEmpty());
            Assertions.assertFalse(wines.values().stream().anyMatch(Objects::isNull));
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}