package com.wine.to.up.winelab.parser.service.services;

import com.wine.to.up.parser.common.api.schema.ParserApi;
import com.wine.to.up.winelab.parser.service.dto.Wine;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.test.util.ReflectionTestUtils;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.Map;
import java.util.Objects;

class ParserServiceTest {
    ParserService parserService;
    ParserService mockedParserService;
    private static final String wineToStrReference = "Wine(name=Вино Berton Foundstone Shiraz красное сухое 0,75 л, link=https://winelab.ru/product/1009581, oldPrice=750, newPrice=479.0, image=https://winelab.ru/medias/1009581.png-300Wx300H?context=bWFzdGVyfGltYWdlc3w0NTc2NXxpbWFnZS9wbmd8aW1hZ2VzL2hjOC9oMDcvODgzMjYxNzQ4MDIyMi5wbmd8NGUxN2NiMzk2YjUxOTVmOTBhOTcwMTAwY2I1YjljZWZhMTViY2ViODIzZTczYzgxYWE3YzlmYzEzZmVkMmM5ZQ, manufacturer=Berton Vineyards, brand=Berton Vinyard Foundstone, country=Австралия, region=null, volume=0.75, alcoholContent=13, sparkling=false, color=RED, sugar=DRY, grapeSort=Шираз, description=Регион: Юго-Восточная Австралия. Сорт винограда: 100% Шираз. Выдержка: чаны из нержавеющей стали. Цвет: насыщенный пурпурный с фиолетовым оттенком. Аромат: насыщенный выразительный с яркими нотами специй, спелой ежевики, сливы и легкими сладковатыми оттенками дуба, кофе, ванили и карамели. Вкус: полнотелый насыщенный с умеренно терпкими приятными шелковистыми танинами и оттенками ежевики, черешни, сливы и длительным послевкусием., gastronomy=Гастрономическое сочетание: стейк из говядины прожарки medium, свинина на косточке, твердые сыры, хамон, колбасы. Температура подачи: 14-16° С)";
    private static final String gastronomyReference = "Гастрономическое сочетание: стейк из говядины прожарки medium, свинина на косточке, твердые сыры, хамон, колбасы. Температура подачи: 14-16° С";

    @BeforeEach
    void init() {
        parserService = new ParserService();
        mockedParserService = Mockito.mock(ParserService.class);
        ReflectionTestUtils.setField(parserService, "SITE_URL", "winelab.ru");
        ReflectionTestUtils.setField(parserService, "PROTOCOL", "https://");
        ReflectionTestUtils.setField(parserService, "COOKIES", Map.of("currentPos", "S734", "currentRegion", "RU-SPE"));
        ReflectionTestUtils.setField(parserService, "CATALOGS", Map.of("wine", "vino", "sparkling", "shampanskie-i-igristye-vina"));
        ReflectionTestUtils.setField(parserService, "ID_SELECTOR", "data-id");
        ReflectionTestUtils.setField(parserService, "CARD_SELECTOR", "div.container a.product_card");
        ReflectionTestUtils.setField(parserService, "NEXT_PAGE_SELECTOR", "ul.pagination li.page-item a[rel=next]");
        ReflectionTestUtils.setField(parserService, "CATALOG_NAME_SELECTOR", "div.product_card--header div");
        ReflectionTestUtils.setField(parserService, "CATALOG_START_URL", "https://winelab.ru/catalog/%s;");
        ReflectionTestUtils.setField(parserService, "CATALOG_NEXT_URL", "https://winelab.ru/%s");
        ReflectionTestUtils.setField(parserService, "CATALOG_PAGE_URL", "https://winelab.ru/catalog/%s?page=%d&sort=relevance");
        ReflectionTestUtils.setField(parserService, "PRODUCT_NAME_SELECTOR", "div.product_description div.description");
        ReflectionTestUtils.setField(parserService, "PRODUCT_DETAILS_SELECTOR", "div.container div.row.product-detail-page.product_card_row.js-add-recent-list");
        ReflectionTestUtils.setField(parserService, "BRAND_SELECTOR", "data-brand");
        ReflectionTestUtils.setField(parserService, "PRODUCT_TAG_SELECTOR", "div.product_description div.filters > span");
        ReflectionTestUtils.setField(parserService, "IMAGE_SELECTOR", "div.image-zoom.js-zoom-product img");
        ReflectionTestUtils.setField(parserService, "CARD_COUNTRY_SELECTOR", "div.product_description div.description");
        ReflectionTestUtils.setField(parserService, "NEW_PRICE_SELECTOR", "data-price");
        ReflectionTestUtils.setField(parserService, "OLD_PRICE_SELECTOR", "div.product_description div.prices_main");
        ReflectionTestUtils.setField(parserService, "GASTRONOMY_SELECTOR", "div.product_description_card:contains(Рекомендуемое употребление) p");
        ReflectionTestUtils.setField(parserService, "DESCRIPTION_SELECTOR", "div.product_description_card:contains(Электронный сомелье) p");
        ReflectionTestUtils.setField(parserService, "REGION_SELECTOR", "data-category");
        ReflectionTestUtils.setField(parserService, "SPARKLING_CATEGORY", "Шампанские и игристые вина");
        ReflectionTestUtils.setField(parserService, "PRODUCT_PAGE_URL", "https://winelab.ru/product/%d");
        ReflectionTestUtils.setField(parserService, "FILTER_SELECTOR", "div.filter_block__container.js-facet.js-facet-values div[data-code=%s] div.filter_button span");
        ReflectionTestUtils.setField(parserService, "COLOR_SELECTOR", "Color");
        ReflectionTestUtils.setField(parserService, "SUGAR_SELECTOR", "SugarAmount");
        ReflectionTestUtils.setField(parserService, "COUNTRY_SELECTOR", "countryfiltr");
        ReflectionTestUtils.setField(parserService, "GRAPE_SELECTOR", "Sort");
        ReflectionTestUtils.setField(parserService, "MANUFACTURER_SELECTOR", "manufacture");
        ReflectionTestUtils.setField(parserService, "CATEGORY_SELECTOR", "category");
        ReflectionTestUtils.setField(parserService, "SEARCH_QUERY_BASE", "https://winelab.ru/search?q=%d%%3Arelevance");
        ReflectionTestUtils.setField(parserService, "SEARCH_QUERY_BRAND", "%%3Abrands%%3A%s");
        ReflectionTestUtils.setField(parserService, "SEARCH_QUERY_ALCOHOL", "%%3AAlcoholContent%%3A%%255B%.1f%%2BTO%%2B%.1f%%255De");
        ReflectionTestUtils.setField(parserService, "SEARCH_QUERY_PRICE", "%%3Aprice%%3A%%5B%.0f%%20TO%%20%.0f%%5D");
        ReflectionTestUtils.setField(parserService, "WINES", new String[] {"вино","винный","шампанское","портвейн","глинтвейн","вермут","кагор","сангрия"});
        ReflectionTestUtils.setField(parserService, "SPARKLINGS", new String[] {"игрист","шампанское"});
        ReflectionTestUtils.setField(parserService, "REGIONS", new String[] {"бордо","венето","тоскана","риоха","кастилья ла манча","бургундия","долина луары",
                "кампо де борха","риберо дель дуэро","пьемонт","долина роны","сицилия","другие регионы"});
        ReflectionTestUtils.setField(parserService, "COUNTRY_FIX", Map.of(
                "Российская Федерация","Россия",
            "Южная Африка","ЮАР",
            "Соединенные Штаты Америки","США",
            "Соед. Королев.","Великобритания"));
        ReflectionTestUtils.setField(parserService, "COLORS", Map.of(
                "красное", ParserApi.Wine.Color.RED,
            "розовое", ParserApi.Wine.Color.ROSE,
            "белое", ParserApi.Wine.Color.WHITE));
        ReflectionTestUtils.setField(parserService, "SUGARS", Map.of(
                "брют", ParserApi.Wine.Sugar.DRY,
                "сухое",ParserApi.Wine.Sugar.DRY,
                "полусухое",ParserApi.Wine.Sugar.MEDIUM_DRY,
                "полусладкое",ParserApi.Wine.Sugar.MEDIUM,
                "сладкое",ParserApi.Wine.Sugar.SWEET));
        ReflectionTestUtils.setField(parserService, "PATTERN_VOLUME", "\\d+([,.]\\d+)? [Лл]");
        ReflectionTestUtils.setField(parserService, "PATTERN_ALCOHOL", "\\d{0,2}(.\\d+)? %");
    }

    @Test
    void testParsedValuesEqualExpected() {
        Wine wine = parserService.parseProduct(1009581);
        Assertions.assertEquals("Вино Berton Foundstone Shiraz красное сухое 0,75 л", wine.getName());             //test the fields are being parsed correctly
        Assertions.assertEquals(BigDecimal.valueOf(750), wine.getOldPrice());
        Assertions.assertEquals("https://winelab.ru/product/1009581", wine.getLink());
        Assertions.assertEquals(BigDecimal.valueOf(479.0f), wine.getNewPrice());
        Assertions.assertEquals("https://winelab.ru/medias/1009581.png-300Wx300H?context=bWFzdGVyfGltYWdlc3w0NTc2NXxpbWFnZS9wbmd8aW1hZ2VzL2hjOC9oMDcvODgzMjYxNzQ4MDIyMi5wbmd8NGUxN2NiMzk2YjUxOTVmOTBhOTcwMTAwY2I1YjljZWZhMTViY2ViODIzZTczYzgxYWE3YzlmYzEzZmVkMmM5ZQ", wine.getImage());
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

    @Test
    void testParseCatalogsNotEmpty() {
        Map<Integer, Wine> wines = parserService.parseCatalogs();
        Assertions.assertFalse(wines.isEmpty());
        Assertions.assertFalse(wines.values().stream().anyMatch(Objects::isNull));
    }
}