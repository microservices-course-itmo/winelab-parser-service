package com.wine.to.up.winelab.parser.service;

import com.wine.to.up.winelab.parser.service.dto.Wine;
import com.wine.to.up.winelab.parser.service.services.ParserService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

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
            Assertions.assertEquals(wine.getName(), "Вино Berton Foundstone Shiraz красное сухое 0,75 л");             //test the fields are being parsed correctly
            Assertions.assertEquals(wine.getOldPrice(), BigDecimal.valueOf(750));
            Assertions.assertEquals(wine.getLink(), "https://www.winelab.ru/product/1009581");
            Assertions.assertEquals(wine.getNewPrice(), BigDecimal.valueOf(529.0));
            Assertions.assertEquals(wine.getImage(), "https://www.winelab.ru/medias/1009581.png-300Wx300H?context=bWFzdGVyfGltYWdlc3w0NTc2NXxpbWFnZS9wbmd8aW1hZ2VzL2hjOC9oMDcvODgzMjYxNzQ4MDIyMi5wbmd8NGUxN2NiMzk2YjUxOTVmOTBhOTcwMTAwY2I1YjljZWZhMTViY2ViODIzZTczYzgxYWE3YzlmYzEzZmVkMmM5ZQ");
            Assertions.assertEquals(wine.getManufacturer(), "Berton Vineyards");
            Assertions.assertEquals(wine.getBrand(), "Berton Vinyard Foundstone");
            Assertions.assertEquals(wine.getCountry(), "Австралия");
            Assertions.assertNull(wine.getRegion());
            Assertions.assertEquals(wine.getVolume(), BigDecimal.valueOf(0.75));
            Assertions.assertEquals(wine.getDescription(), "Регион: Юго-Восточная Австралия. Сорт винограда: 100% Шираз. Выдержка: чаны из нержавеющей стали. Цвет: насыщенный пурпурный с фиолетовым оттенком. Аромат: насыщенный выразительный с яркими нотами специй, спелой ежевики, сливы и легкими сладковатыми оттенками дуба, кофе, ванили и карамели. Вкус: полнотелый насыщенный с умеренно терпкими приятными шелковистыми танинами и оттенками ежевики, черешни, сливы и длительным послевкусием.");
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