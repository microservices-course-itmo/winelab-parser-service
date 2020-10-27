package com.wine.to.up.winelab.parser.service;

import com.wine.to.up.winelab.parser.service.dto.Wine;
import com.wine.to.up.winelab.parser.service.services.ParserService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;
import org.springframework.test.util.ReflectionTestUtils;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.Map;

public class ParserServiceTest {
    ParserService parserService;

    @BeforeEach
    public void init() {
        parserService = new ParserService();
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
    public void testParsedValuesEqualExpected() {
        try {
            Wine wine = parserService.parseProduct(1009581);
            Assertions.assertEquals("Вино Berton Foundstone Shiraz красное сухое 0,75 л", wine.getName());             //test the fields are being parsed correctly
            Assertions.assertEquals(BigDecimal.valueOf(750), wine.getOldPrice());
            Assertions.assertEquals( "https://www.winelab.ru/product/1009581", wine.getLink());
            Assertions.assertEquals(BigDecimal.valueOf(529.0), wine.getNewPrice());
            Assertions.assertEquals("https://www.winelab.ru/medias/1009581.png-300Wx300H?context=bWFzdGVyfGltYWdlc3w0NTc2NXxpbWFnZS9wbmd8aW1hZ2VzL2hjOC9oMDcvODgzMjYxNzQ4MDIyMi5wbmd8NGUxN2NiMzk2YjUxOTVmOTBhOTcwMTAwY2I1YjljZWZhMTViY2ViODIzZTczYzgxYWE3YzlmYzEzZmVkMmM5ZQ", wine.getImage());
            Assertions.assertEquals("Berton Vineyards", wine.getManufacturer());
            Assertions.assertEquals( "Berton Vinyard Foundstone", wine.getBrand());
            Assertions.assertEquals("Австралия", wine.getCountry());
            Assertions.assertEquals(BigDecimal.valueOf(0.75), wine.getVolume());
            Assertions.assertEquals( "Регион: Юго-Восточная Австралия. Сорт винограда: 100% Шираз. Выдержка: чаны из нержавеющей стали. Цвет: насыщенный пурпурный с фиолетовым оттенком. Аромат: насыщенный выразительный с яркими нотами специй, спелой ежевики, сливы и легкими сладковатыми оттенками дуба, кофе, ванили и карамели. Вкус: полнотелый насыщенный с умеренно терпкими приятными шелковистыми танинами и оттенками ежевики, черешни, сливы и длительным послевкусием.", wine.getDescription());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testIdIsValid() {
        int id = 0;
        try {
            Assertions.assertThrows(org.jsoup.HttpStatusException.class, (Executable) parserService.parseProduct(id));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testParsedValuesNotNull() {
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
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}